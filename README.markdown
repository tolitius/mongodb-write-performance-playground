# What is "MongoDB Write Performance Playground"? ##

A simple playground where ( for now ) a mongo-java-driver is used to INSERT X number of "some" records into MongoDB.

### Things tried here:

+ Inserting documents One By One
+ Inserting documents All At Once
+ Partitioning documents for a given number of threads, and inserting them in parallel ( ThreadPoolExecutor )
+ Partitioning documents for a given number of threads... Inserting to MongoS having multiple Shards
+ Partitioning documents for a given number of threads... Inserting to multiple Mongo Daemons

### What is "all this" for..

This creation is _meant_ to be "cloned" and changed to reflect what _you_ really need: e.g. change documents, indexes, collections, number of documents, etc..

### "Show Me The Money"

+ Running it on Mac Book Pro i7 2.8 GHz..
+ Single document (record) size is:  *665* bytes

# RESULTS

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

    StopWatch '-- MongoDB Insert All With Partitioning [ grid size = 3 ] --': running time (millis) = 1313
    -----------------------------------------
    ms     %     Task name
    -----------------------------------------
    01313  100%  adding 100000 number of documents..

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

    StopWatch '-- MongoDB Insert All With Partitioning [ grid size = 3 ] --': running time (millis) = 15865
    -----------------------------------------
    ms     %     Task name
    -----------------------------------------
    15865  100%  adding 1000000 number of documents..

    StopWatch '-- MongoDB Partitioning / Multiple Hosts [ grid size = 10 ] --': running time (millis) = 10639
    -----------------------------------------
    ms     %     Task name
    -----------------------------------------
    10639  100%  adding 1000000 number of documents..

### Current version of MongoDB ( 1.8.1 ) does not provide even distribution over shards

    Unfortunately.
    The way sharding is done, Mongo looks at the chunk size and moves chunks around in async manner.
    Which means when X number of records are sent to MongoS it will only use "next" shard in case a chunk size is reached.
    Hence inserts are still "sequential".
    JIRA that "kind of" addresses that: https://jira.mongodb.org/browse/SERVER-939

### Hence real time Even Distribution is needed. Which is done via manual partitioning by:

+ number of documents / grid size [ where in this example grid size = number of threads ]
+ number of documents / grid size Evenly Distributed over multiple MongoDB Daemons [ this.nextCollectionHost % collectionDataSources.size() ]

### The numbers above can be surely improved

 By having more RAM, having multiple physical clients / servers, using a C driver, etc..
 But the whole idea is to start somewhere...
