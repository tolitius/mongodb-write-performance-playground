package org.dotkam.mongodb.partition;

import com.mongodb.DBObject;

import java.util.List;
import java.util.Map;

public interface DocumentPartitioner<PARTITION_KEY> {

    public Map<PARTITION_KEY, List<DBObject>> partition( List<DBObject> documents, Integer gridSize );
}
