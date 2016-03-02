package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.entities.Book;
import edu.stevens.cs522.bookstore.manager.BookManager;
import edu.stevens.cs522.bookstore.manager.IEntityCreator;
import edu.stevens.cs522.bookstore.manager.IQueryListener;
import edu.stevens.cs522.bookstore.manager.TypedCursor;
import edu.stevens.cs522.bookstore.providers.BookProvider;

public class BookStoreActivity extends Activity {

	private static final int BOOK_LOADER_ID = 1;
	private static final int AUTHORS_LOADER_ID = 2;
	// Use this when logging errors and warnings.
	@SuppressWarnings("unused")
	private static final String TAG = BookStoreActivity.class.getCanonicalName();
	
	// These are request codes for subactivity request calls
	static final private int ADD_REQUEST = 1;
	
	@SuppressWarnings("unused")
	static final private int CHECKOUT_REQUEST = ADD_REQUEST + 1;

	// There is a reason this must be an ArrayList instead of a List.
	public static final String CART_CONTENT_KEY = "cart_contents";
	public static final String CART_SIZE_KEY = "cart_size";
	public static final String BOOK_DETAIL_KEY = "book_detail";
	public static final String BOOK_DETAIL_ID_KEY = "book_detail_id";

	BookAdapter mAdapter;
	BookManager bookManager;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cart);

		bookManager = new BookManager(this,new IEntityCreator<Book>() {
			@Override
			public Book create(Cursor cursor) {
				return new Book(cursor);
			}
		},BOOK_LOADER_ID);
		bookManager.executeQuery(BookContract.CONTENT_URI,null,null,null,
				new IQueryListener<Book>(){

					@Override
					public void handleResult(TypedCursor<Book> results) {
						mAdapter.swapCursor(results.getCursor());
					}

					@Override
					public void closeResult() {
						mAdapter.swapCursor(null);
					}
				});


		View empty = findViewById(R.id.empty);
		final ListView listView = (ListView)findViewById(R.id.cart_view);
		listView.setEmptyView(empty);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getApplicationContext(), BookDetailActivity.class);
				intent.putExtra(BOOK_DETAIL_ID_KEY, (int) id);
				startActivity(intent);

			}
		});

		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

			private int nr = 0;
			private ArrayList<Long> row_ids = new ArrayList<Long>();

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				mAdapter.clearSelection();
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				nr = 0;
				MenuInflater inflater = getMenuInflater();
				inflater.inflate(R.menu.contextual_menu, menu);
				return true;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {

					case R.id.item_delete:
						nr = 0;
						mAdapter.clearSelection();
						for(int i = 0; i<row_ids.size();i++){
							bookManager.removeAsync(row_ids.get(i));
						}
				}
				return false;
			}
			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int position,
												  long id, boolean checked) {
				// TODO Auto-generated method stub
				if (checked) {
					nr++;
					mAdapter.setNewSelection(position, checked);
					row_ids.add(id);
				} else {
					nr--;
					row_ids.remove(id);
					mAdapter.removeSelection(position);
				}
				mode.setTitle(nr + " selected");

			}
		});

		mAdapter = new BookAdapter(this, android.R.layout.simple_list_item_2,null);
		listView.setAdapter(mAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.bookstore_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		// TODO
		switch (item.getItemId()){
			case R.id.add:
				Intent addIntent = new Intent(this,AddBookActivity.class);
				startActivityForResult(addIntent,ADD_REQUEST);
				return true;
			case R.id.delete:
				// DELETE delete the currently selected book
				return true;
			case R.id.checkout:
				// CHECKOUT provide the UI for checking out
					Intent checkoutIntent = new Intent(this, CheckoutActivity.class);
					checkoutIntent.putExtra(CART_SIZE_KEY, BookProvider.cartSize());
					startActivityForResult(checkoutIntent, CHECKOUT_REQUEST);
				return true;

		}
		return false;
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		// TODO Handle results from the Search and Checkout activities.
		// Use SEARCH_REQUEST and CHECKOUT_REQUEST codes to distinguish the cases.
		switch (requestCode){
			// SEARCH: add the book that is returned to the shopping cart.
			case ADD_REQUEST:
				if(resultCode == RESULT_OK){
//					Book book = intent.getParcelableExtra(AddBookActivity.BOOK_RESULT_KEY);
//					bookManager.persistAsync(book);
				}
				break;
			case CHECKOUT_REQUEST:
				if(resultCode == RESULT_OK) {
					bookManager.removeAsync();
				}
				break;
			default:
				break;
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// TODO save the shopping cart contents (which should be a list of parcelables).
		//savedInstanceState.putParcelableArrayList(CART_CONTENT_KEY,shoppingCart);
		super.onSaveInstanceState(savedInstanceState);
	}







	//BookAdapter

	private class BookAdapter extends ResourceCursorAdapter {

		protected final static int ROW_LAYOUT = android.R.layout.simple_list_item_2;
		private HashMap<Integer, Boolean> mSelection = new HashMap<Integer, Boolean>();

		public BookAdapter(Context context, int layout, Cursor c) {
			super(context, layout, c,0);
		}

		public void clearSelection() {
			mSelection = new HashMap<Integer, Boolean>();
			notifyDataSetChanged();
		}

		public void setNewSelection(int position, boolean value) {
			mSelection.put(position, value);
			notifyDataSetChanged();
		}

		public void removeSelection(int position) {
			mSelection.remove(position);
			notifyDataSetChanged();
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater)
					context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflater.inflate(ROW_LAYOUT,parent,false);

			return v;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			TextView titleLine = (TextView)view.findViewById(android.R.id.text1);
			TextView authorLine = (TextView)view.findViewById(android.R.id.text2);
			titleLine.setText(BookContract.getTitle(cursor));
			String authors = BookContract.getAuthorName(cursor);
			authorLine.setText(authors);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = super.getView(position, convertView, parent);//let the adapter handle setting up the row views
			v.setBackgroundColor(getResources().getColor(android.R.color.background_dark)); //default color

			if (mSelection.get(position) != null) {
				v.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));// this is a selected position so make it red
			}
			return v;
		}


	}


	
}