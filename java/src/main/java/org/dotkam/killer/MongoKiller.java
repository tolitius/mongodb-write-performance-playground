package org.dotkam.killer;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.dotkam.mongodb.benchmark.MultipleHostMongoKiller;
import org.dotkam.mongodb.benchmark.SingleHostMongoKiller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

public class MongoKiller {

    private final static Logger logger = LoggerFactory.getLogger( MongoKiller.class );

    private static Map<String, Object> config;
    private static String DEFAULT_CONFIG_LOCATION = "src/main/resources/mongo_killer.yaml";

    public static void main( String[] args ) {

        String configLocation = DEFAULT_CONFIG_LOCATION;

        OptionParser parser = new OptionParser();
        parser.accepts( "config" ).withRequiredArg();
        parser.accepts( "multiple-hosts" );

        OptionSet options = parser.parse( args );

        if ( options.has( "config" ) ) {
            configLocation = options.valueOf( "config" ).toString();
        }

        logger.trace( "loading mongo killer config from: " + configLocation );
        config = loadConfig( configLocation );

        if ( options.has( "multiple-hosts" ) ) {
            logger.trace( "killing multiple Mongo hosts.." );
            new MultipleHostMongoKiller( config ).destroyTarget();
        }
        else {
            logger.trace( "killing a single Mongo host ( localhost:27017 ).." );
            new SingleHostMongoKiller( config ).destroyTarget();
        }

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
