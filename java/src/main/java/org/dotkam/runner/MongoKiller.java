package org.dotkam.runner;

import org.dotkam.mongodb.benchmark.SingleHostMongoKiller;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

public class MongoKiller {

    private static Map<String, Object> config;
    private static String DEFAULT_CONFIG_LOCATION = "src/main/resources/mongo_killer.yaml";

    public static void main( String[] args ) {

        if ( args.length == 1 ) {
            config = loadConfig( args[0] );
        }
        else {
            config = loadConfig( DEFAULT_CONFIG_LOCATION );
        }

        new SingleHostMongoKiller( config ).destroyTarget();

        System.exit( 0 );
    }

    private static Map<String, Object> loadConfig( String location ) {
        try {
            return (Map)( new Yaml() ).load( new FileInputStream( new File( location ) ) );
        }
        catch ( Exception ex ) {
            throw new RuntimeException( "Could not load the config. Reason => ", ex );
        }
    }
}
