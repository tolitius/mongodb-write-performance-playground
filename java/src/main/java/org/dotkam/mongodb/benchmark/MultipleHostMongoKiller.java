package org.dotkam.mongodb.benchmark;


import org.dotkam.document.VeryImportantDocument;
import org.dotkam.mongodb.concurrent.MongoMultipleHostDocumentWriter;
import org.dotkam.mongodb.concurrent.MongoSingleHostDocumentWriter;
import org.dotkam.mongodb.datasource.CollectionDataSource;
import org.dotkam.mongodb.partition.GridSizeDocumentPartitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p> Quick and dirty way to "be killing" multiple MongoS / MongoD pairs </p>
 */
public class MultipleHostMongoKiller extends AbstractMongoKiller {

    private final static Logger logger = LoggerFactory.getLogger( MultipleHostMongoKiller.class );

    public MultipleHostMongoKiller( Map<String, Object> config ) {

        super.numberOfDocuments = Long.valueOf( config.get( "number_of_documents" ).toString() );
        super.gridSize = Integer.valueOf( config.get( "grid_size" ).toString() );
        super.batchThreshold = Integer.valueOf(config.get( "single_batch_threshold" ).toString());
        super.uniqueSalt = new Date().getTime();

        List<CollectionDataSource> multipleHosts = this.createHostDataSources( config );

        super.documentWriter = new MongoMultipleHostDocumentWriter(  VeryImportantDocument.class,
                                                                     new GridSizeDocumentPartitioner(),
                                                                     multipleHosts,
                                                                     super.gridSize );
    }

    public void destroyTarget() {
        super.doDestroyTarget();
    }

    private List<CollectionDataSource> createHostDataSources ( Map<String, Object> config ) {

        List<CollectionDataSource> multipleHosts = new ArrayList<CollectionDataSource>();

        logger.trace( config.get("hosts").toString() );
        List<Map<String, Object>> hosts = ( List ) config.get( "hosts" );
        super.numberOfHosts = hosts.size();

        for ( int i = 0; i < super.numberOfHosts; i++) {

            CollectionDataSource host = new CollectionDataSource(
                    config.get( "db_name" ).toString(),
                    config.get( "collection_name" ).toString(),
                    hosts.get( i ).get( "name" ).toString(),
                    Integer.valueOf( hosts.get( i ).get( "port" ).toString() ) );

            //host.getCollection().drop();
            multipleHosts.add( host );
        }

        return multipleHosts;
    }
}
