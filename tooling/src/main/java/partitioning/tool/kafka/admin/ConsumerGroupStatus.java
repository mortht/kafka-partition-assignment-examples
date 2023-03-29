package partitioning.tool.kafka.admin;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.kafka.clients.admin.AdminClient;

import partitioning.tool.kafka.admin.describers.OffsetTopicsDescriber;
import partitioning.tool.kafka.admin.describers.PartitionAssignmentDescriber;
import partitioning.tool.kafka.common.PropertiesLoader;

public class ConsumerGroupStatus {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        if (args.length != 2) {
            System.err.println("Required parameters: <config-file> <consumer-group>");
            return;
        }

        // load args
        final Properties properties = PropertiesLoader.load(args[0]);
        try (AdminClient adminClient = AdminClient.create(properties)) {

            final List<String> consumerGroups = Arrays.asList(args[1].split("\\s*,\\s*"));
            System.out.println("Date: " + LocalDateTime.now() + " - consumerGroups: " + consumerGroups + "\n");

            final Map<String, PartitionAssignmentDescriber> partitionAssignmentDescriberMap = consumerGroups.stream()
                    .collect(Collectors.toMap(consumerGroup -> consumerGroup, consumerGroup -> new PartitionAssignmentDescriber(adminClient, consumerGroup)));

            final Map<String, OffsetTopicsDescriber> offsetTopicsDescriberMap = consumerGroups.stream()
                    .collect(Collectors.toMap(consumerGroup -> consumerGroup, consumerGroup -> new OffsetTopicsDescriber(adminClient, consumerGroup)));

            final String tableRowTemplate = "%-40.40s  %-4.4s  %-12s  %-12s  %-12s  %-40.40s  %-40.40s";

            while (true) {
                cleanOutput();

                System.out.println("Date: " + LocalDateTime.now() + " - consumerGroups: " + consumerGroups + "\n");
                System.out.println(String.format(tableRowTemplate,
                        "Topic", "Partition", "curr Offset", "end Offset", "message lag", "client Id", "consumerGroup"));
                for (var consumerGroup : consumerGroups) {
                    var offsetTopicsDescriber = offsetTopicsDescriberMap.get(consumerGroup);
                    var partitionAssignmentDescriber = partitionAssignmentDescriberMap.get(consumerGroup);

                    offsetTopicsDescriber.refreshValues();
                    partitionAssignmentDescriber.refreshValues();

                    for (var partition : offsetTopicsDescriber.getAllTopicsPartitions()) {

                        final long currentOffset = offsetTopicsDescriber.getCurrentOffsetOrDefault(partition, -1L);
                        final long endOffset = offsetTopicsDescriber.getEndOffsetOrDefault(partition, -1L);
                        final long lag = endOffset - currentOffset;
                        final String clientId = partitionAssignmentDescriber.getClientId(partition);
                        final String instanceId = partitionAssignmentDescriber.getInstanceId(partition);

                        System.out.println(String.format(tableRowTemplate,
                                partition.topic(), partition.partition(), currentOffset, endOffset, lag, clientId, consumerGroup));
                    }
                }

//            for (var consumerGroup : consumerGroups) {
//                var partitionAssignmentDescriber = partitionAssignmentDescriberMap.get(consumerGroup);
//                partitionAssignmentDescriber.refreshValues();
//
//                System.out.println(partitionAssignmentDescriber.printAssignment());
//            }

                waitHalfSecond();
            }
        }
    }

    private static void cleanOutput() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void waitHalfSecond() {
        try {
            Thread.sleep(500);
        } catch (final InterruptedException e) {
            System.err.println("Ops, sleep was interruped!" + e);
        }
    }
}
