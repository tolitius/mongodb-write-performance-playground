# What is "MongoDB Write Performance Playground"? ##

+ A simple playground where ( for now ) a mongo-java-driver is used to INSERT X number of "some" records into MongoDB.
+ This simple project is _meant_ to be "cloned" and changed to reflect what _you_ really need: e.g. change records, indexes, collections, number of records, etc..

For "As Is" structure / configs, running it on Mac Book Pro i7 2.8 GHz.. Here are the results I got:

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

### The numbers above can be surely improved

 By using more clients [ I am using one ], having more RAM, using C driver, etc..
 But the whole idea is to start somewhere...
