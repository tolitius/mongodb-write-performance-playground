package org.dotkam.mongodb.concurrent;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import org.dotkam.mongodb.concurrent.task.MongoInsertTask;
import org.dotkam.mongodb.datasource.CollectionDataSource;
import org.dotkam.mongodb.partition.DocumentPartitioner;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class MongoMultipleHostDocumentWriter implements MongoDocumentWriter {

    private int gridSize;
    private List<CollectionDataSource> collectionDataSources;

    // just so we don't need to calculate the class from _generics_ at runtime => every millisecond counts
    private Class documentClass;
    private DocumentPartitioner<Integer> documentPartitioner;

    // TODO: tobe refactored into a separate MongoCollectionHostIterator
    private int nextCollectionHost;

    public MongoMultipleHostDocumentWriter(Class documentClass, DocumentPartitioner documentPartitioner,
                                           List<CollectionDataSource> collectionDataSources, int gridSize) {

        this.gridSize = gridSize;
        this.documentClass = documentClass;
        this.documentPartitioner = documentPartitioner;
        this.collectionDataSources = collectionDataSources;
        this.nextCollectionHost = collectionDataSources.size();

        if ( this.nextCollectionHost == 0 ) {
            throw new RuntimeException( "at least one Mongo host data source needs to be provided" );
        }
    }

    public void write( List<DBObject> documents ) {

        Set<Future> tasks = new HashSet<Future>( gridSize );
        ExecutorService executorService = new ScheduledThreadPoolExecutor( gridSize );

        // map

        Map<Integer, List<DBObject>> partitions = this.documentPartitioner.partition( documents, gridSize );

        for ( Integer partition: partitions.keySet() ) {
            Future<Void> task = executorService.submit(
                    new MongoInsertTask(
                            this.nextHostCollection(), partitions.get( partition ), documentClass ) );
            tasks.add( task );
        }

        // reduce

		for ( Future pendingTask : tasks ) {
            try {
                pendingTask.get();
            }
            catch (Exception exc) {
                throw new RuntimeException( exc );
            }
        }
    }

    // TODO: tobe refactored into a separate MongoCollectionHostIterator
    private DBCollection nextHostCollection() {

        int nextHost = this.nextCollectionHost % collectionDataSources.size();

        DBCollection collection =  collectionDataSources.get( nextHost ).getCollection();

        nextCollectionHost++;
        return collection;
    }
}
