package org.dotkam.mongodb.concurrent;

import com.mongodb.DBCollection;
import com.mongodb.ReflectionDBObject;

import java.util.List;
import java.util.concurrent.Callable;

public class MongoInserter<RECORD extends ReflectionDBObject> implements Callable<Void> {

    private DBCollection collection;
    private List<RECORD> records;

    // just so we don't need to calculate it from generics at runtime => every millisecond matters
    private Class recordClass;

    public MongoInserter( DBCollection collection, List<RECORD> records, Class recordClass ) {
        this.collection  = collection;
        this.records = records;
        this.recordClass = recordClass;
    }

    public Void call() throws Exception {

        for ( RECORD record: records ) {
            this.collection.setObjectClass( recordClass );
            this.collection.insert( record );
        }

        return null;
    }
}
