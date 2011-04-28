package org.dotkam.mongodb.concurrent;

import com.mongodb.DBObject;
import org.dotkam.mongodb.datasource.CollectionDataSource;
import org.dotkam.mongodb.partition.DocumentPartitioner;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class MongoDocumentWriter {

    private CollectionDataSource collectionDataSource;
    private int gridSize;

    // just so we don't need to calculate it from _generics_ at runtime => every millisecond counts
    private Class documentClass;

    public MongoDocumentWriter( CollectionDataSource collectionDataSource, int gridSize, Class documentClass ) {
        this.collectionDataSource = collectionDataSource;
        this.gridSize = gridSize;
        this.documentClass = documentClass;
    }

    public void write( List<DBObject> documents ) {

        Set<Future> tasks = new HashSet<Future>( gridSize );
        ExecutorService executorService = new ScheduledThreadPoolExecutor( gridSize );

        DocumentPartitioner<DBObject> documentPartitioner = new DocumentPartitioner<DBObject>();

        // map

        Map<Integer, List<DBObject>> partitions = documentPartitioner.partition( documents, gridSize );

        for ( Integer partition: partitions.keySet() ) {
            Future<Void> task = executorService.submit(
                    new MongoInsertTask(
                            collectionDataSource.getCollection(), partitions.get( partition ), documentClass ) );
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
}
