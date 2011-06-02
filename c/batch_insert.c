#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "bson.h"
#include "mongo.h"

static void batch_insert( mongo_connection *conn, int numberOfRecords );

int main( int argc, char *argv[] ) {

   if ( argc != 2 ) /* argc should be 2 for correct execution */
    {
        printf( "usage: %s number_of_records", argv[0] );
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

    batch_insert( conn, atoi( argv[1] ) );

    mongo_destroy( conn );
    printf( "\nconnection closed\n" );

    return 0;
}


static void batch_insert( mongo_connection *conn, int numberOfRecords ) {

    printf( "inserting %d records...", numberOfRecords );

    bson *p, **ps;
    bson_buffer *p_buf;
    int i;

    ps = ( bson ** )malloc( sizeof( bson * ) * numberOfRecords );

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
        ps[i] = p;
        free( p_buf );
    }

    mongo_insert_batch( conn, "dumb.collection", ps, numberOfRecords );

    for ( i = 0; i < numberOfRecords; i++ ) {
        bson_destroy( ps[i] );
        free( ps[i] );
    }
}
