package org.dotkam.mongodb.partition;

import com.mongodb.DBObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GridSizeDocumentPartitioner implements DocumentPartitioner<Integer> {

    // { 1 : listOfDocuments1, 2 : listOfDocuments2, ... }
    public Map<Integer, List<DBObject>> partition( List<DBObject> documents, Integer gridSize ) {

        Map<Integer, List<DBObject>> partitions = new HashMap<Integer, List<DBObject>>();

        int numberOfDocuments = documents.size();

        int min = 0;
        int max = numberOfDocuments - 1;

        int partitionSize = ( max - min ) / gridSize + 1;

        int partitionNumber = 0;
        int start = min;
        int end = start + partitionSize - 1;

        while ( start <= max ) {

            //logger.debug( "partition: " + partitionNumber + " from: " + start + ", to: " + end );

            partitions.put( partitionNumber, documents.subList( start, end + 1 ) );

            start += partitionSize;
            end += partitionSize;

            if ( end >= max ) { end = max; }

            partitionNumber++;
        }

        return partitions;
    }
}
