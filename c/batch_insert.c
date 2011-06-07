#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <sys/time.h>
#include "bson.h"
#include "mongo.h"

static void batch_insert_with_batch_size( mongo_connection *conn, int numberOfRecords, int batchSize );
static void timer_pretty_print( struct timeval startTime, struct timeval endTime );

int main( int argc, char *argv[] ) {

   if ( argc != 3 )
    {
        printf( "\tusage: %s number_of_records batch_size\n", argv[0] );
        return 1;
    }

    mongo_connection conn[1]; /* ptr */
  
    mongo_conn_return status = mongo_connect( conn, "127.0.0.1", 27017 );

    switch (status) {
        case mongo_conn_success: printf( "connection succeeded\n" ); break;
        case mongo_conn_bad_arg: printf( "bad arguments\n" ); return 1;
        case mongo_conn_no_socket: printf( "no socket\n" ); return 1;
        case mongo_conn_fail: printf( "connection failed\n" ); return 1;
        case mongo_conn_not_master: printf( "not master\n" ); return 1;
    }

    struct timeval startTime;
    struct timeval endTime;
    gettimeofday( &startTime, NULL );

    batch_insert_with_batch_size( conn, atoi( argv[1] ), atoi( argv[2] ) );

    gettimeofday( &endTime, NULL );
    timer_pretty_print( startTime, endTime );

    mongo_destroy( conn );
    printf( "\nconnection closed\n" );

    return 0;
}


static void batch_insert_with_batch_size( mongo_connection *conn, int numberOfRecords, int batchSize ) {

    printf( "\ninserting %d records with a batch size of %d", numberOfRecords, batchSize );

    bson *p, **partition;
    bson_buffer *p_buf;
    int i;

    partition = ( bson ** )malloc( sizeof( bson * ) * batchSize );

    for ( i = 0; i < numberOfRecords; i++ ) {

        p = ( bson * )malloc( sizeof( bson ) );
        p_buf = ( bson_buffer * )malloc( sizeof( bson_buffer ) );

        bson_buffer_init( p_buf );

        bson_append_new_oid( p_buf, "_id" );

        bson_append_long( p_buf, "1", 11111111 );
        bson_append_long( p_buf, "2", 22222222 );
        bson_append_long( p_buf, "3", 33333333 );
        bson_append_long( p_buf, "4", 44444444 );
        bson_append_long( p_buf, "5", 55555555 );
        bson_append_long( p_buf, "6", 66666666 );
        bson_append_long( p_buf, "7", 77777777 );
        bson_append_long( p_buf, "8", 88888888 );
        bson_append_long( p_buf, "9", 99999999 );
        bson_append_long( p_buf, "10", 1010101010101010 );

        bson_append_long( p_buf, "11", 11111111 );
        bson_append_long( p_buf, "12", 22222222 );
        bson_append_long( p_buf, "13", 33333333 );
        bson_append_long( p_buf, "14", 44444444 );
        bson_append_long( p_buf, "15", 55555555 );
        bson_append_long( p_buf, "16", 66666666 );
        bson_append_long( p_buf, "17", 77777777 );
        bson_append_long( p_buf, "18", 88888888 );
        bson_append_long( p_buf, "19", 99999999 );
        bson_append_long( p_buf, "20", 1010101010101010 );

        bson_append_long( p_buf, "21", 11111111 );
        bson_append_long( p_buf, "22", 22222222 );
        bson_append_long( p_buf, "23", 33333333 );
        bson_append_long( p_buf, "24", 44444444 );
        bson_append_long( p_buf, "25", 55555555 );

        bson_from_buffer( p, p_buf );
        partition[ i % batchSize ] = p;
        free( p_buf );
        
        // if we reach a batch size
        if ( ( i + 1 ) % batchSize == 0 ) {

            //printf( "\nOn %d record => ", i + 1 );
            //printf( " persisting a batch size of %d records... \n", batchSize );

            mongo_insert_batch( conn, "dumb.collection", partition, batchSize );

            //printf( "[Ok]\n" );

            // (!) need to free partition here before reassigning
            for ( int j = 0; j < batchSize; j++ ) {
                bson_destroy( partition[j] );
                free( partition[j] );
            }

            partition = ( bson ** )malloc( sizeof( bson * ) * batchSize );
        }
    }

    // insert the remainder if any
    if ( i % batchSize != 0 ) {
            //printf( "\npersisting a remainder.. \n" );

            mongo_insert_batch( conn, "dumb.collection", partition, i % batchSize );

            //printf( "[Ok]\n" );

            for ( int j = 0; j < ( i % batchSize ) ; j++ ) {
                bson_destroy( partition[j] );
                free( partition[j] );
            }
    }
}

static void timer_pretty_print( struct timeval startTime, struct timeval endTime ) {

    double took = ( endTime.tv_sec - startTime.tv_sec ) * 1000000 + ( endTime.tv_usec - startTime.tv_usec );
    printf( " => took %f seconds...\n", took / 1000000 );
}
