package org.dotkam.mongodb.concurrent.task;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;

import java.util.List;
import java.util.concurrent.Callable;

public class MongoInsertTask implements Callable<Void> {

    private DBCollection collection;
    private List<DBObject> documents;

    // just so we don't need to calculate it from _generics_ at runtime => every millisecond counts
    private Class documentClass;

    public MongoInsertTask( DBCollection collection, List<DBObject> documents, Class documentClass ) {
        this.collection  = collection;
        this.documents = documents;
        this.documentClass = documentClass;
    }

    public Void call() throws Exception {

        this.collection.setObjectClass( documentClass );
        //this.collection.insert( documents, WriteConcern.FSYNC_SAFE );
        this.collection.insert( documents );

        return null;
    }
}
