package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.entities.Author;
import edu.stevens.cs522.bookstore.entities.Book;
import edu.stevens.cs522.bookstore.manager.BookManager;
import edu.stevens.cs522.bookstore.manager.IEntityCreator;

public class AddBookActivity extends Activity {
	
	// Use this as the key to return the book details as a Parcelable extra in the result intent.
	public static final String BOOK_RESULT_KEY = "book_result";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_book);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// TODO provide SEARCH and CANCEL options
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.addbook_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO
		switch (item.getItemId()){
			case R.id.addbook_search: // SEARCH: return the book details to the BookStore activity
				EditText titleText = (EditText) findViewById(R.id.search_title);
				EditText authorText = (EditText) findViewById(R.id.search_author);
				EditText isbnText = (EditText) findViewById(R.id.search_isbn);
				if ( !titleText.getText().toString().isEmpty()
						|| !authorText.getText().toString().isEmpty()
						|| !isbnText.getText().toString().isEmpty()) {
					Book book = searchBook();

					BookManager bookManager = new BookManager(this,new IEntityCreator<Book>() {
						@Override
						public Book create(Cursor cursor) {
							return new Book(cursor);
						}
					},1);
					bookManager.persistAsync(book);

					if (book != null){
						Intent result = new Intent();
						result.putExtra(BOOK_RESULT_KEY, book);
						setResult(RESULT_OK, result);
						finish();
					}
				}
				return true;
			case R.id.addbook_cancel:  // CANCEL: cancel the search request
				setResult(RESULT_CANCELED);
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public Book searchBook(){
		/*
		 * Search for the specified book.
		 */
		// TODO Just build a Book object with the search criteria and return that.
		return getBookObj();
	}

	private Book getBookObj(){
		Book book = createDefaultBook();
		EditText titleText = (EditText)findViewById(R.id.search_title);
		if (!titleText.getText().toString().isEmpty()){
			book.title = titleText.getText().toString();
		}
		EditText authorText = (EditText) findViewById(R.id.search_author);
		if (!authorText.getText().toString().isEmpty()){
			fillAuthorVal(book, authorText.getText().toString());
		}
		if (authorText.getText().toString().isEmpty()){

		}
		EditText isbnText = (EditText) findViewById(R.id.search_isbn);
		if (!isbnText.getText().toString().isEmpty()){
			book.isbn = isbnText.getText().toString();
		}
		return book;
	}
	private Book createDefaultBook(){
		int id = 1000;
		String title = "Harry Potter and the Sorcerer's Stone";
		String isbn = "978-0439708180";
		String price = "$6.70";
		Author defaultAuthor = new Author();
		defaultAuthor.firstName = "J.K.";
		defaultAuthor.middleInitial = "";
		defaultAuthor.lastName = "Rowling";
		Author[] authors = new Author[3];
		authors[0] = defaultAuthor;
		authors[1] = defaultAuthor;
		authors[2] = defaultAuthor;
		return new Book(id, title, authors, isbn, price);
	}

	private void fillAuthorVal(Book book, String str){
		String[] authorSeperator = str.split("/");
        book.authors = new Author[authorSeperator.length];
		for(int i = 0; i< authorSeperator.length;i++){
            book.authors[i] = new Author();
			String[] stringArr = authorSeperator[i].split(" ");
			book.authors[i].firstName = stringArr[0];
			switch (stringArr.length){
				case 1:
					break;
				case 2:
                    book.authors[i].lastName = stringArr[1];
					break;
				case 3:
					book.authors[i].middleInitial = stringArr[1];
					book.authors[i].lastName = stringArr[2];
					break;
		}


		}

	}

}