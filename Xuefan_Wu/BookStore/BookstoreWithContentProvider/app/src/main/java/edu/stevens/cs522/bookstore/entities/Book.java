package edu.stevens.cs522.bookstore.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.bookstore.contracts.BookContract;

public class Book implements Parcelable{
	
	// TODO Modify this to implement the Parcelable interface.
	
	// TODO redefine toString() to display book title and price (why?).
	
	public long id;
	
	public String title;
	
	public Author[] authors;
	
	public String isbn;
	
	public String price;

	public Book(int id, String title, Author[] author, String isbn, String price) {
		this.id = id;
		this.title = title;
		this.authors = author;
		this.isbn = isbn;
		this.price = price;
	}

	public Book(Cursor cursor) {
		this.id = BookContract.getColumnId(cursor);
		this.title = BookContract.getTitle(cursor);
		this.authors = BookContract.getAuthors(cursor);
		this.isbn = BookContract.getIsbn(cursor);
		this.price = BookContract.getPrice(cursor);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(id);
		dest.writeString(title);
		dest.writeInt(authors.length);
		dest.writeTypedArray(authors,flags);
		dest.writeString(isbn);
		dest.writeString(price);
	}

	private Book(Parcel in){
// 		this();
		id = in.readInt();
		title = in.readString();
		authors = new Author[in.readInt()];
		in.readTypedArray(authors, Author.CREATOR);
		this.isbn = in.readString();
		this.price = in.readString();
	}
	@Override
	public String toString(){
		return "The title of the Book is:"+ title + "\nThe price is: " + price;
	}

	public static final Parcelable.Creator<Book> CREATOR
			= new Parcelable.Creator<Book>(){
		public Book createFromParcel(Parcel in){
			return new Book(in);
		}
		public Book[] newArray(int size){
			return new Book[size];
		}
	};

	public void writeToProvider(ContentValues values,Book book) {
		//BookContract.putColumnID(values,book.id);
		BookContract.putTitle(values,book.title);
		BookContract.putAuthors(values, book.authors[0].toString());
		BookContract.putIsbn(values, book.isbn);
		BookContract.putPrice(values,book.price);
	}

}