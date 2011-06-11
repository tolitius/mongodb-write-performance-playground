package org.dotkam.mongodb.benchmark;


import com.mongodb.DBObject;
import org.dotkam.document.VeryImportantDocument;
import org.dotkam.mongodb.concurrent.MongoSingleHostDocumentWriter;
import org.dotkam.util.SizeTeller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class AbstractMongoKiller {

    private final static Logger logger = LoggerFactory.getLogger( AbstractMongoKiller.class );

    protected long numberOfDocuments = 1000000;
    protected int gridSize = 1;
    protected int numberOfHosts = 1;
    protected int batchThreshold = 1000000;
    protected long uniqueSalt = new Date().getTime();

    MongoSingleHostDocumentWriter documentWriter;

    protected void doDestroyTarget() {

        StopWatch timer = new StopWatch( "Killing Mongo" );

        logger.trace( "Bringing " + this.getClass().getSimpleName() + " to life with:\n\n" +
                "\t Number Of Documents:\t" + this.numberOfDocuments +
                "\n\t Document Size ~:\t\t" + SizeTeller.tellMeMyApproximateSize(
                                                                            new VeryImportantDocument() ) + " bytes" +
                "\n\t Grid Size:\t\t\t\t" + this.gridSize +
                "\n\t Number Of Hosts:\t\t" + this.numberOfHosts +
                "\n\t Batch Threshold:\t\t" + this.batchThreshold +  "\n"
        );

        timer.start( "adding " + numberOfDocuments + " number of documents.." );

        List<DBObject> documents = new ArrayList<DBObject>();

        long i;
        for ( i = 0; i < numberOfDocuments; i++ ) {

            VeryImportantDocument document = new VeryImportantDocument();

            document.set_id( i );
            document.setBusinessId( i % numberOfHosts );
            document.setSalt( uniqueSalt );

            documents.add( document );

            if ( ( i + 1 ) % batchThreshold == 0 ) {

                logger.debug( "=> sending " + batchThreshold + " documents down the Mongo pipe" );
                documentWriter.write( documents );
                documents = new ArrayList<DBObject>();

            }
        }

        // if we still have a remainder of documents..
        if ( ( i ) % batchThreshold != 0 ) {
            logger.debug( "=> sending " + documents.size() + " documents down the Mongo pipe" );
            documentWriter.write( documents );
        }

        timer.stop();
        logger.info( "\n" + timer.prettyPrint() );
    }
}
