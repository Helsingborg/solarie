#!/bin/bash

# -Xss : the size of the stack. Prevayler might require a large value here in order to deserialize large snapshots.
# -Xmx : max heap size (ex: -Xmx1024)
# -Xms : min heap size. Having -Xms = 1.8GB (32bit) can be bad, because you don't let memory for anything else.
# -Xmn : the size of the heap for the young generation
# Young generation represents all the objects which have a short life of time. Young generation objects are in a specific location into the heap, where the garbage collector will pass often. All new objects are created into the young generation region (called "eden"). When an object survive is still "alive" after more than 2-3 gc cleaning, then it will be swap has an "old generation" : they are "survivor" .
# Good size is 33%
# -XX:NewRatio : the same as Wmn, but using a % (dynamic fs static -Xmn option). -XX:NewRatio=3 means that the ratio between the old and young generation is 1:3
# -XX:NewSize - Size of the young generation at JVM init. Calculated automatically if you specify -XX:NewRatio
# -XX:MaxNewSize - The largest size the young generation can grow to (unlimited if this value is not specified at command line)
# -XX:SurvivorRatio : "old generation" called tenured generation, ratio, in %. For example, -XX:SurvivorRatio=6 sets the ratio between each survivor space and eden to be 1:6 (eden is where new objects are created)
# -XX:MinHeapFreeRatio: default is 40%. JVM will allocate memory to always have as minimum 40% of free memory. When -Xmx = -Xms, it's useless.
# -XX:MaxHeapFreeRatio: default is 70%. The same as Min, to avoid unnecessary memory allocation.

export MAVEN_OPTS="-Xmx1g -Xms256m"


echo "Sending output to log.txt..."
mvn jetty:run >> log.txt 2>&1 &
echo $! > server.pid
echo "Waiting for process to terminate..."
wait

echo "Solarie has terminated!"

rm server.pid
exit 0

