package edu.stevens.cs522.bookstore.manager;

/**
 * Created by Xuefan on 2/26/2016.
 */
public interface IQueryListener<T> {

    public void handleResult(TypedCursor<T> results);

    public void closeResult();
}
