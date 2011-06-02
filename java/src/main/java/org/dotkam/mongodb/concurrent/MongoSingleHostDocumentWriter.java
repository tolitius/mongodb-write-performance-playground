package org.dotkam.mongodb.concurrent;

import com.mongodb.DBObject;
import org.dotkam.mongodb.concurrent.task.MongoInsertTask;
import org.dotkam.mongodb.datasource.CollectionDataSource;
import org.dotkam.mongodb.partition.DocumentPartitioner;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class MongoSingleHostDocumentWriter implements MongoDocumentWriter {

    private CollectionDataSource collectionDataSource;
    private int gridSize;

    // just so we don't need to calculate the class from _generics_ at runtime => every millisecond counts
    private Class documentClass;
    private DocumentPartitioner<Integer> documentPartitioner;

    public MongoSingleHostDocumentWriter(Class documentClass, DocumentPartitioner documentPartitioner,
                                         CollectionDataSource collectionDataSource, int gridSize) {

        this.collectionDataSource = collectionDataSource;
        this.gridSize = gridSize;
        this.documentClass = documentClass;
        this.documentPartitioner = documentPartitioner;
    }

    public void write( List<DBObject> documents ) {

        Set<Future> tasks = new HashSet<Future>( gridSize );
        ExecutorService executorService = new ScheduledThreadPoolExecutor( gridSize );

        // map

        Map<Integer, List<DBObject>> partitions = this.documentPartitioner.partition( documents, gridSize );

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
