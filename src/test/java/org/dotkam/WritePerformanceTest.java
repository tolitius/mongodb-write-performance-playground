package org.dotkam;

import com.mongodb.*;
import org.dotkam.record.VeryImportantRecord;
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

            VeryImportantRecord veryImportantRecord = createVeryImportantRecord( i );

            veryImportantRecords.setObjectClass( VeryImportantRecord.class );
            veryImportantRecords.insert( veryImportantRecord );

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
            records.add( createVeryImportantRecord( i ) );
        }

        veryImportantRecords.setObjectClass( VeryImportantRecord.class );
        veryImportantRecords.insert( records );

        timer.stop();

        System.out.println( timer.prettyPrint() );
    }

    private VeryImportantRecord createVeryImportantRecord( long id ) {

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
        viRecord.setBusinessId( id );
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
