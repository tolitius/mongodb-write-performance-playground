package org.dotkam.mongodb;

import java.util.List;

public interface MongoDbWriter<DATA> {

    public void write( List<DATA> record );
}
