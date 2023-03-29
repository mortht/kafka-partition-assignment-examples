#!/usr/bin/env bash

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

CONSUMER_GROUP=ssa-enrichment-decorator-application,ssa-enrichment-filterer-application,connect-ssa-output-topic-elasticsearch-sink,connect-ssa-simulation-topic-elasticsearch-sink
LAG_THRESHOLD=10

"$SCRIPT_DIR/status.sh" "$CONSUMER_GROUP" "$LAG_THRESHOLD"
