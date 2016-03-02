package edu.stevens.cs522.chat.oneway.server.managers;

import android.database.Cursor;

/**
 * Created by Xuefan on 2/26/2016.
 */
public interface IEntityCreator<T> {
    public T create(Cursor cursor);
}
