package org.dotkam;

import com.mongodb.*;
import org.dotkam.mongodb.concurrent.MongoDocumentWriter;
import org.dotkam.mongodb.datasource.CollectionDataSource;
import org.dotkam.record.VeryImportantRecord;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;

public class ConcurrentWritePerformanceTest {

    private static final int HU_MONGO_US_NUMBER_OF_RECORDS = 100000;
    private static final int GRID_SIZE = 5;

    private static final String DB_NAME = "writePerformanceDumbDb";
    private static final String COLLECTION_NAME = "vipRecords";

    private MongoDocumentWriter documentWriter;

    @Before
    public void createDbAndCollection() throws Exception {

        CollectionDataSource dataSource = new CollectionDataSource( DB_NAME, COLLECTION_NAME );
        documentWriter = new MongoDocumentWriter( dataSource, GRID_SIZE, VeryImportantRecord.class );
    }

    @After
    public void dropDataBase() throws Exception {
        ( new Mongo() ).getDB( DB_NAME ).dropDatabase();
    }

    @Test
    public void insertRecordsWithPartitioning() {

        StopWatch timer = new StopWatch("-- MongoDB Insert All With Partitioning --");

        timer.start( "adding " + HU_MONGO_US_NUMBER_OF_RECORDS + " number of documents.." );

        List<DBObject> documents = new ArrayList<DBObject>();

        for ( int i = 0; i < HU_MONGO_US_NUMBER_OF_RECORDS; i++ ) {
            documents.add( createVeryImportantRecord( i ) );
        }

        documentWriter.write( documents );

        timer.stop();

        System.out.println( timer.prettyPrint() );
    }

    private VeryImportantRecord createVeryImportantRecord( int id ) {

        VeryImportantRecord viRecord = new VeryImportantRecord();

        viRecord.setYou( "you" );
        viRecord.setCant( "cant" );
        viRecord.setLose( "lose" );
        viRecord.setMe( "me" );
        viRecord.setBecause( "because" );
        viRecord.setI( "I" );
        viRecord.setAm( "am" );
        viRecord.setAn( "an" );
        viRecord.setExtremely( "extremely" );
        viRecord.setImportant( "important" );
        viRecord.setRecord( "record" );
        viRecord.setWith( "with" );
        viRecord.setId( String.valueOf( id ) );
        viRecord.setIwould( "I would" );
        viRecord.setLike( "like" );
        viRecord.setTo( "to" );
        viRecord.setBe( "be" );
        viRecord.setPersisted( "persisted" );
        viRecord.setAnd( "and" );
        viRecord.setKept( "kept" );
        viRecord.setIn( "in" );
        viRecord.setDry( "dry" );
        viRecord.setCozy( "cozy" );
        viRecord.setPlace( "place" );
        viRecord.setAlso( "also" );
        viRecord.setBring( "bring" );
        viRecord.setMyself( "my self" );
        viRecord.setSome( "some" );
        viRecord.setMongos( "mongos" );

        return viRecord;
    }
}
