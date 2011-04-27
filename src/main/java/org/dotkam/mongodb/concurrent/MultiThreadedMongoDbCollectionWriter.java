package org.dotkam.mongodb.concurrent;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import org.dotkam.mongodb.MongoDbWriter;

import java.util.List;

public class MultiThreadedMongoDbCollectionWriter<RECORD> implements MongoDbWriter<RECORD> {

    private DB db;
    private DBCollection dbCollection;

    private String dbName;
    private String collectionName;

    private int gridSize;

    public void write( List<RECORD> records ) {

        //for
        dbCollection.insert(  );
    }

    public void setDb( DB db ) {
        this.db = db;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

}
