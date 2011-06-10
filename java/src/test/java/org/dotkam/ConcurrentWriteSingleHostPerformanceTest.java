package org.dotkam;

import com.mongodb.*;
import org.dotkam.document.VeryImportantDocument;
import org.dotkam.mongodb.concurrent.MongoDocumentWriter;
import org.dotkam.mongodb.concurrent.MongoSingleHostDocumentWriter;
import org.dotkam.mongodb.datasource.CollectionDataSource;
import org.dotkam.mongodb.partition.GridSizeDocumentPartitioner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.StopWatch;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class ConcurrentWriteSingleHostPerformanceTest {

    private static final int HU_MONGO_US_NUMBER_OF_RECORDS = 30000000;
    private static final int SINGLE_BATCH_THRESHOLD = 1000000;
    private static final int GRID_SIZE = 4;

    private static final String DB_NAME = "writePerformanceDumbDb";
    private static final String COLLECTION_NAME = "vipRecords";

    // to make sure every _test run_ produces (to a degree) unique documents
    private static final long uniqueSalt = new Date().getTime();

    private CollectionDataSource dataSource;
    private MongoDocumentWriter documentWriter;

    @Before
    public void createDbAndCollection() throws Exception {

        dataSource = new CollectionDataSource( DB_NAME, COLLECTION_NAME );

        //dataSource.getCollection().drop();

        documentWriter = new MongoSingleHostDocumentWriter(  VeryImportantDocument.class,
                                                   new GridSizeDocumentPartitioner(),
                                                   dataSource,
                                                   GRID_SIZE );
    }

    @After
    public void dropDataBase() throws Exception {
        //dataSource.getCollection().drop();
    }

    @Test
    public void insertRecordsWithPartitioning() throws Exception {

        StopWatch timer = new StopWatch("-- MongoDB Insert All With Partitioning [ grid size = " + GRID_SIZE + " ] --");

        System.out.println( "working with a document size of " +
                tellMeMySize( new VeryImportantDocument() ) + " bytes" );

        timer.start( "adding " + HU_MONGO_US_NUMBER_OF_RECORDS + " number of documents.." );

        List<DBObject> documents = new ArrayList<DBObject>();

        long i;
        for ( i = 0; i < HU_MONGO_US_NUMBER_OF_RECORDS; i++ ) {

            VeryImportantDocument document = new VeryImportantDocument();

            document.set_id( i );
            document.setSalt( uniqueSalt );

            documents.add( document );

            if ( ( i + 1 ) % SINGLE_BATCH_THRESHOLD == 0 ) {

                System.out.println( "=> sending " + SINGLE_BATCH_THRESHOLD + " documents down the Mongo pipe" );
                documentWriter.write( documents );
                documents = new ArrayList<DBObject>();

            }
        }

        // if we still have a remainder of documents..
        if ( ( i ) % SINGLE_BATCH_THRESHOLD != 0 ) {
            System.out.println( "=> sending the remainder: " + documents.size() + " documents down the Mongo pipe" );
            documentWriter.write( documents );
        }

        timer.stop();

//        // giving "some" time for documents to propagate to disk
//        Thread.sleep( 10000 );
//
//        DBCollection collection = ( new Mongo() ).getDB( DB_NAME ).getCollection( COLLECTION_NAME );
//        assertEquals ( "database has unexpected number of records",
//                       HU_MONGO_US_NUMBER_OF_RECORDS, collection.count() );

        System.out.println( timer.prettyPrint() );
    }

    private long tellMeMySize( Serializable object ) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ObjectOutputStream oos = new ObjectOutputStream( baos );
            oos.writeObject( object );
            oos.close();
        } catch (IOException e) {
            throw new RuntimeException( e );
        }

        return baos.size();
    }
}

