package org.dotkam;

import com.mongodb.*;
import org.dotkam.document.VeryImportantDocument;
import org.junit.*;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;

public class WritePerformanceTest {

    private static final int HU_MONGO_US_NUMBER_OF_RECORDS = 100000;

    private DB db;
    private DBCollection veryImportantRecords;

    @Before
    public void createDbAndCollection() throws Exception {

        Mongo mongo = new Mongo();
        db = mongo.getDB( "writePerformanceDumbDb" );

        veryImportantRecords = db.getCollection( "vipRecords" );
        veryImportantRecords.remove( new BasicDBObject() );

        veryImportantRecords.ensureIndex( new BasicDBObject( "_id", 1 ) );
        //veryImportantRecords.createIndex( new BasicDBObject( "id", 1 ) );
    }

    @After
    public void dropDataBase() {
        db.dropDatabase();
    }

    @Test
    @Ignore
    public void insertRecordsOneByOne() {

        StopWatch timer = new StopWatch("-- MongoDB Insert One By One --");

        timer.start( "adding " + HU_MONGO_US_NUMBER_OF_RECORDS + " number of records.." );

        for ( long i = 0; i < HU_MONGO_US_NUMBER_OF_RECORDS; i++ ) {

            VeryImportantDocument veryImportantDocument = new VeryImportantDocument();

            veryImportantDocument.set_id( i );
            veryImportantRecords.setObjectClass( VeryImportantDocument.class );
            veryImportantRecords.insert(veryImportantDocument);

        }

        timer.stop();

        System.out.println( timer.prettyPrint() );
    }

    @Test
    public void insertAllRecordsAtOnce() {

        StopWatch timer = new StopWatch("-- MongoDB Insert All At Once --");

        timer.start( "adding " + HU_MONGO_US_NUMBER_OF_RECORDS + " number of records.." );

        List<DBObject> records = new ArrayList<DBObject>();

        for ( int i = 0; i < HU_MONGO_US_NUMBER_OF_RECORDS; i++ ) {

            records.add( new VeryImportantDocument() );
        }

        veryImportantRecords.setObjectClass( VeryImportantDocument.class );
        veryImportantRecords.insert( records );

        timer.stop();

        System.out.println( timer.prettyPrint() );
    }
}
