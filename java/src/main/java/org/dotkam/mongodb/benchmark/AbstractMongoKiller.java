package org.dotkam.mongodb.benchmark;


import com.mongodb.DBObject;
import org.dotkam.document.VeryImportantDocument;
import org.dotkam.mongodb.concurrent.MongoSingleHostDocumentWriter;
import org.dotkam.util.SizeTeller;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractMongoKiller {
    long numberOfDocuments;
    int gridSize;
    int batchThreshold;
    long uniqueSalt;

    MongoSingleHostDocumentWriter documentWriter;

    protected void doDestroyTarget() {

        StopWatch timer = new StopWatch("-- MongoDB Insert All With Partitioning [ grid size = " + gridSize + " ] --");

        System.out.println( "working with a document size of " +
                SizeTeller.tellMeMyApproximateSize(new VeryImportantDocument()) + " bytes" );

        timer.start( "adding " + numberOfDocuments + " number of documents.." );

        List<DBObject> documents = new ArrayList<DBObject>();

        long i;
        for ( i = 0; i < numberOfDocuments; i++ ) {

            VeryImportantDocument document = new VeryImportantDocument();

            document.set_id( i );
            document.setSalt( uniqueSalt );

            documents.add( document );

            if ( ( i + 1 ) % batchThreshold == 0 ) {

                System.out.println( "=> sending " + batchThreshold + " documents down the Mongo pipe" );
                documentWriter.write( documents );
                documents = new ArrayList<DBObject>();

            }
        }

        // if we still have a remainder of documents..
        if ( ( i ) % batchThreshold != 0 ) {
            System.out.println( "=> sending the remainder: " + documents.size() + " documents down the Mongo pipe" );
            documentWriter.write( documents );
        }

        timer.stop();
        System.out.println( timer.prettyPrint() );
    }
}
