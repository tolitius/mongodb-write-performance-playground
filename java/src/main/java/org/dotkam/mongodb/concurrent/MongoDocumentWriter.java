package org.dotkam.mongodb.concurrent;


import com.mongodb.DBObject;

import java.util.List;

public interface MongoDocumentWriter {

    public void write ( List<DBObject> documents );
}
