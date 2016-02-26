package edu.stevens.cs522.chat.oneway.server.contract;

import android.content.ContentValues;
import android.database.Cursor;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Xuefan on 2/14/2016.
 */
public class PeerContract {

    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String ADDRESS = "address";
    public static final String PORT = "port";


    public static long getId(Cursor cursor) {
        long a = cursor.getLong(cursor.getColumnIndexOrThrow(ID));
        return a;
    }
    public static void putId(ContentValues values, long id) {
        values.put(ID, id);
    }
    public static String getName(Cursor cursor){
        String s = null;
        if(cursor != null && cursor.moveToFirst()){
            s= cursor.getString(cursor.getColumnIndexOrThrow(NAME));
        }
        return s;
    }

    public static InetAddress getAddress(Cursor cursor){
        String address = cursor.getString(cursor.getColumnIndexOrThrow(ADDRESS));
        address = address.replace("/", "");
        InetAddress ipAddresses[] = new InetAddress[1];
        try {
            ipAddresses = InetAddress.getAllByName(address);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ipAddresses[0];
    }

    public static int getPort(Cursor cursor){
        int s = 0;
        if(cursor != null && cursor.moveToFirst()){
            s= cursor.getInt(cursor.getColumnIndexOrThrow(PORT));
        }
        return s;
    }

    public static void putName(ContentValues values, String name) {

        values.put(NAME,name);
    }
    public static void putAddress(ContentValues values, InetAddress address) {
        values.put(ADDRESS,address.toString());
    }

    public static void putPort(ContentValues values, int port) {
        values.put(PORT,port);
    }


}
