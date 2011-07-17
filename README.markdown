# What is "MongoDB Write Performance Playground"?

A simple playground where a mongo-java-driver and a mongo-c-driver are used to INSERT X number of "some" records into MongoDB.

+ On a Java side can be run with [MongoKiller](https://github.com/anatoly-polinsky/mongodb-write-performance-playground/blob/master/java/src/main/java/org/dotkam/killer/MongoKiller.java)
+ Relies on [mongo_killer.yaml](https://github.com/anatoly-polinsky/mongodb-write-performance-playground/blob/master/java/src/main/resources/mongo_killer.yaml) config by default, but a custom config may be provided though a "--config" parameter
+ Can be run against a single MongoDB instance (MongoS or MongoD), as well as multiple MongoDB instances by specifying hosts in YAML config and running with a "--multiple-hosts" parameter
+ When running against multiple hosts, batches of documents ( = numberOfDocuments / gridSize ) are sent to hosts in a Round Robin fashion

### Things tried here:

+ Inserting documents One By One
+ Inserting documents All At Once
+ Partitioning documents for a given number of threads, and inserting them in parallel ( ThreadPoolExecutor )
+ Partitioning documents for a given number of threads... Inserting to MongoS having multiple Shards ( Shard cluster )
+ Partitioning documents for a given number of threads... Inserting to multiple MongoDs directly
+ Pre-splitting, moving chunks for a known number of threads, so the shard key is effective [ tags: partitioning, sharding ]

#### Sample MongoKiller run with '--multiple-hosts':

```bash
loading mongo killer config from: src/main/resources/mongo_killer.yaml
killing multiple Mongo hosts..
[{name=localhost, port=30000}, {name=localhost, port=30001}, {name=localhost, port=30002}, {name=localhost, port=30003}]
Bringing MultipleHostMongoKiller to life with:

     Number Of Documents:   5200000
     Document Size ~:       643 bytes
     Grid Size:             4
     Number Of Hosts:       4
     Batch Threshold:       1000000

=> sending 1000000 documents down the Mongo pipe
=> sending 1000000 documents down the Mongo pipe
=> sending 1000000 documents down the Mongo pipe
=> sending 1000000 documents down the Mongo pipe
=> sending 1000000 documents down the Mongo pipe
=> sending 200000 documents down the Mongo pipe

StopWatch 'Killing Mongo': running time (millis) = 70427
-----------------------------------------
ms     %     Task name
-----------------------------------------
70427  100%  adding 5200000 number of documents..
```

## What is "all this" for..

This creation is _meant_ to be "cloned" and changed to reflect what _you_ really need: e.g. change documents, indexes, collections, number of documents, etc..

# NOTE(!)

The results below are the best benchmarks that could be squeezed out of Mongo on a given hardware. 

###HOWEVER: 
All these results are for a "Fire and Forget" writing mode, where WriteConcern is set to NORMAL (which is a default setting btw). Which means the data was pushed through the socket and "hopefully" got persisted. In case the WriteConcern is set to something more durable e.g. SAFE / FSYNC_SAFE, the performace goes down really fast.

###HOWEVER II: 
If plans are to work with "Big Data", which (its index) most likely will not fit into RAM, MongoDB performance is unpredictably bad, and mostly averages to low hundreds ( 200 / 300 ) documents per seconds. More about this topic here: [NoRAM DB => “If It Does Not Fit in RAM, I Will Quietly Die For You”](http://www.dotkam.com/2011/07/06/noram-db-if-it-does-not-fit-in-ram-i-will-quietly-die-for-you/)

###CONCLUSION: 
In a lightweght CRUD Webapp, which does not really need a high throughput, does not need to keep GB/TB of data, and might benefit from a document oriented schemaless data store, MongoDB would be a perfect choice: very nice query language, fun to work with. In case "Big Data" and high throughput are needed, I would recommend looking elsewhere.

# "Show Me The Money"

## Mr. C goes first

+ Running it on Mac Book Pro i7 2.8 GHz..
+ Single document has 25 fields and its size is roughly *320* bytes

### to compile

```c
gcc -Isrc --std=c99 ./mongo-c-driver/src/*.c -I ./mongo-c-driver/src/ batch_insert.c -o batch_insert
```

### to run

```bash
$ ./batch_insert
    usage: ./batch_insert number_of_records batch_size
```

## 100,000 ( One Hundred Thousand ) records 

```bash
$ ./batch_insert 100000 100000

inserting 100000 records with a batch size of 100000 => took 0.393889 seconds...
```

```bash
$ ./batch_insert 100000 10000

inserting 100000 records with a batch size of 10000 => took 1.351205 seconds...
```

```bash
$ ./batch_insert 100000 50000

inserting 100000 records with a batch size of 50000 => took 0.864108 seconds...
```

## Now Ms. Java..

+ Running it on Mac Book Pro i7 2.8 GHz..
+ Single document has 30 fields, and its size is *665* bytes
+ A 1,000,000 documents is hungry, so: "-Xms512m -Xmx1024m -XX:MaxPermSize=384m -Xss128k"

## 10,000 ( Ten Thousand ) records

    StopWatch '-- MongoDB Insert One By One --': running time (millis) = 901
    -----------------------------------------
    ms     %     Task name
    -----------------------------------------
    00901  100%  adding 10000 number of records..

    StopWatch '-- MongoDB Insert All At Once --': running time (millis) = 185
    -----------------------------------------
    ms     %     Task name
    -----------------------------------------
    00185  100%  adding 10000 number of records..

    StopWatch '-- MongoDB Insert All With Partitioning [ grid size = 3 ] --': running time (millis) = 359
    -----------------------------------------
    ms     %     Task name
    -----------------------------------------
    00359  100%  adding 10000 number of documents..

## 100,000 ( One Hundred Thousand ) records

    StopWatch '-- MongoDB Insert One By One --': running time (millis) = 3692
    -----------------------------------------
    ms     %     Task name
    -----------------------------------------
    03692  100%  adding 100000 number of records..

    StopWatch '-- MongoDB Insert All At Once --': running time (millis) = 2038
    -----------------------------------------
    ms     %     Task name
    -----------------------------------------
    02038  100%  adding 100000 number of records..

    StopWatch '-- MongoDB Insert All With Partitioning [ grid size = 4 ] --': running time (millis) = 1142
    -----------------------------------------
    ms     %     Task name
    -----------------------------------------
    01142  100%  adding 100000 number of documents..

## 1,000,000 ( One Million ) records

    StopWatch '-- MongoDB Insert One By One --': running time (millis) = 31157
    -----------------------------------------
    ms     %     Task name
    -----------------------------------------
    31157  100%  adding 1000000 number of records..

    StopWatch '-- MongoDB Insert All At Once --': running time (millis) = 20238
    -----------------------------------------
    ms     %     Task name
    -----------------------------------------
    20238  100%  adding 1000000 number of records..

    StopWatch '-- MongoDB Insert All With Partitioning [ grid size = 3 ] --': running time (millis) = 12785
    -----------------------------------------
    ms     %     Task name
    -----------------------------------------
    12785  100%  adding 1000000 number of documents..

    StopWatch '-- MongoDB Partitioning / Multiple Hosts [ grid size = 15 / number of hosts = 5 ] --': running time (millis) = 9602
    -----------------------------------------
    ms     %     Task name
    -----------------------------------------
    09602  100%  adding 1000000 number of documents..

### Current version of MongoDB ( 1.9.0 ) does not provide even distribution over shards

  Unfortunately.
  The way sharding is done, Mongo looks at the chunk size and moves chunks around in async manner.
  Which means when X number of records are sent to MongoS it will only use "next" shard in case a chunk size is reached.
  Hence inserts are still "sequential".
  JIRA that "kind of" addresses that: https://jira.mongodb.org/browse/SERVER-939
  
  Even if chunks are 'pre-split' for a known number of shards / threads, INSERTing speed is way ( at least 3 times ) slower than a manual even distribution with [MongoMultipleHostDocumentWriter.java](https://github.com/anatoly-polinsky/mongodb-write-performance-playground/blob/master/java/src/main/java/org/dotkam/mongodb/concurrent/MongoMultipleHostDocumentWriter.java)

### Hence real time Even Distribution is needed. Which is done via manual partitioning by:

+ number of documents / grid size [ where in this example grid size = number of threads ]
+ number of documents / grid size Evenly Distributed over multiple MongoDB Daemons [ this.nextCollectionHost % collectionDataSources.size() ]

