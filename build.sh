#!/usr/bin/env bash

pushd tooling || exit
mvn clean package
popd || exit

cp tooling/target/partitioning-tool-1.0.0-SNAPSHOT-jar-with-dependencies.jar package/
tar -czvf consumer-status.tar.gz package/
#rm package/partitioning-tool-1.0.0-SNAPSHOT-jar-with-dependencies.jar -f
