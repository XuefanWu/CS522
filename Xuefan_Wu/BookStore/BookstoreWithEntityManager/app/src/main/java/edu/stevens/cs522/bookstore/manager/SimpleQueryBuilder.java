package edu.stevens.cs522.bookstore.manager;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xuefan on 2/26/2016.
 */
public class SimpleQueryBuilder<T> implements IContinue<Cursor> {

    private IEntityCreator<T> helper;
    private ISimpleQueryListener listener;

    private SimpleQueryBuilder(
            IEntityCreator<T> helper,
            ISimpleQueryListener<T> listener){
        this.helper = helper;
        this.listener = listener;
    }

    public static <T> void executeQuery(Activity context,
                                   Uri uri,
                                   IEntityCreator<T> helper,
                                   ISimpleQueryListener<T> listener){
        SimpleQueryBuilder<T> qb =
                new SimpleQueryBuilder<T>(helper,listener);

        AsyncContentResolver resolver =
                new AsyncContentResolver(context.getContentResolver());

        resolver.queryAsync(uri,null,null,null,null,qb);
    }

    public static <T> void executeQuery(Activity context,
                                        Uri uri,
                                        String[] projection,
                                        String selection,
                                        String[] selectionArgs,
                                        IEntityCreator<T> helper,
                                        ISimpleQueryListener<T> listener){
        SimpleQueryBuilder<T> qb =
                new SimpleQueryBuilder<T>(helper,listener);

        AsyncContentResolver resolver =
                new AsyncContentResolver(context.getContentResolver());

        resolver.queryAsync(uri,projection,selection,selectionArgs,null,qb);
    }
//
//    public static <T> void executeInsert(Activity context,
//                                           Uri uri,
//                                           IEntityCreator<T> helper,
//                                           ISimpleQueryListener<T> listener){
//        SimpleQueryBuilder<T> qb =
//                new SimpleQueryBuilder<T>(helper,listener);
//
//        AsyncContentResolver resolver =
//                new AsyncContentResolver(context.getContentResolver());
//
//        resolver.insertAsync(uri, null, null);
//    }
//
//    public static <Book> void executeDelete(Activity context,
//                                            Uri uri,
//                                            IEntityCreator<Book> helper,
//                                            ISimpleQueryListener<Book> listener){
//        SimpleQueryBuilder<Book> qb =
//                new SimpleQueryBuilder<Book>(helper,listener);
//
//        AsyncContentResolver resolver =
//                new AsyncContentResolver(context.getContentResolver());
//
//        resolver.deleteAsync(uri,null,null,null);
//    }
//    public static <Book> void executeUpdate(Activity context,
//                                            Uri uri,
//                                            IEntityCreator<Book> helper,
//                                            ISimpleQueryListener<Book> listener){
//        SimpleQueryBuilder<Book> qb =
//                new SimpleQueryBuilder<Book>(helper,listener);
//
//        AsyncContentResolver resolver =
//                new AsyncContentResolver(context.getContentResolver());
//
//        resolver.updateAsync(uri,null,null,null,null);
//    }

    @Override
    public void kontinue(Cursor value) {
        List<T> instances = new ArrayList<T>();
        if(value.moveToFirst()){
            do{
                T instance = helper.create(value);
                instances.add(instance);
            }while(value.moveToNext());
        }
        value.close();
        listener.handleResults(instances);
    }
}
