#!/bin/sh

if [ -z "$BATCH_OPTS" ]; then
  BATCH_OPTS="-Xms64m -Xmx768m"
fi

java $BATCH_OPTS -jar invoker.jar -cpdir lib org.carrot2.cli.batch.BatchApp $@