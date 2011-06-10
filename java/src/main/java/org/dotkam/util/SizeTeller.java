package org.dotkam.util;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class SizeTeller {

    private SizeTeller() {}

    public static long tellMeMyApproximateSize( Serializable me ) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ObjectOutputStream oos = new ObjectOutputStream( baos );
            oos.writeObject( me );
            oos.close();
        } catch (IOException e) {
            throw new RuntimeException( e );
        }

        return baos.size();
    }
}
