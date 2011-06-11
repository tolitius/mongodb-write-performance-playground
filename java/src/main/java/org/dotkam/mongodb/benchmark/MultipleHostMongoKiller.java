package org.dotkam.mongodb.benchmark;


import org.dotkam.document.VeryImportantDocument;
import org.dotkam.mongodb.concurrent.MongoSingleHostDocumentWriter;
import org.dotkam.mongodb.datasource.CollectionDataSource;
import org.dotkam.mongodb.partition.GridSizeDocumentPartitioner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;

/**
 * <p> Quick and dirty way to "be killing" a single MongoS / MongoD pair </p>
 */
public class MultipleHostMongoKiller extends AbstractMongoKiller {

    private final static Logger logger = LoggerFactory.getLogger( MultipleHostMongoKiller.class );

    public MultipleHostMongoKiller(Map<String, Object> config) {

        super.numberOfDocuments = Long.valueOf( config.get( "number_of_documents" ).toString() );
        super.gridSize = Integer.valueOf( config.get("grid_size").toString() );
        super.batchThreshold = Integer.valueOf(config.get("single_batch_threshold").toString());
        super.uniqueSalt = new Date().getTime();

        CollectionDataSource dataSource =
                new CollectionDataSource( config.get( "db_name" ).toString(),
                                          config.get( "collection_name" ).toString() );

        super.documentWriter = new MongoSingleHostDocumentWriter(  VeryImportantDocument.class,
                                                   new GridSizeDocumentPartitioner(),
                                                   dataSource,
                                                   super.gridSize );
    }

    public void destroyTarget() {
        super.doDestroyTarget();
    }
}
