package org.dotkam.mongodb.datasource;


import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

import java.net.UnknownHostException;

public class CollectionDataSource {

    private final DBCollection dbCollection;

    public CollectionDataSource( String dbName, String collectionName ) {

        try {
            Mongo mongo = new Mongo();
            DB db = mongo.getDB( dbName );

            this.dbCollection = db.getCollection( collectionName );
            this.dbCollection.remove( new BasicDBObject() );

            // forcing '_id' index to pre load the index in memory during a "cold" start
            this.dbCollection.ensureIndex( new BasicDBObject( "_id", 1 ) );
        }
        catch ( UnknownHostException uhe ) {
            throw new RuntimeException( uhe );
        }

    }

    public CollectionDataSource( String dbName, String collectionName, String hostname, int portNumber ) {

        try {
            Mongo mongo = new Mongo( hostname, portNumber );
            DB db = mongo.getDB( dbName );

            this.dbCollection = db.getCollection( collectionName );
            this.dbCollection.remove( new BasicDBObject() );

            // forcing '_id' index to pre load the index in memory during a "cold" start
            this.dbCollection.ensureIndex( new BasicDBObject( "_id", 1 ) );
        }
        catch ( UnknownHostException uhe ) {
            throw new RuntimeException( uhe );
        }

    }

    public DBCollection getCollection() {
        return this.dbCollection;
    }
}
