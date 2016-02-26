package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
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
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.providers.BookProvider;

public class BookStoreActivity extends Activity
		implements LoaderManager.LoaderCallbacks<Cursor> {

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
	//CartDbAdapter db = new CartDbAdapter(this);

	BookAdapter mAdapter;
	//SelectionAdapter mAdapter;




	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//shoppingCart = new ArrayList<Book>();
		// TODO check if there is saved UI state, and if so, restore it (i.e. the cart contents)
		if (savedInstanceState != null) {
			//shoppingCart = savedInstanceState.getParcelableArrayList(CART_CONTENT_KEY);
		}
		// TODO Set the layout (use cart.xml layout)
		setContentView(R.layout.cart);
		// TODO use an array adapter to display the cart contents.



		final ListView listView = (ListView)findViewById(R.id.cart_view);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//				Book book = db.fetchBook(id);
//				Intent intent = new Intent(getApplicationContext(), BookDetailActivity.class);
//				intent.putExtra(BOOK_DETAIL_KEY, book);
//				startActivity(intent);
				Intent intent = new Intent(getApplicationContext(), BookDetailActivity.class);
				intent.putExtra(BOOK_DETAIL_ID_KEY, (int)id);
				startActivity(intent);

			}
		});

		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

			private int nr = 0;
			private ArrayList<Long> row_ids = new ArrayList<Long>();

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				// TODO Auto-generated method stub
				mAdapter.clearSelection();
			}

			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				// TODO Auto-generated method stub

				nr = 0;
				MenuInflater inflater = getMenuInflater();
				inflater.inflate(R.menu.contextual_menu, menu);
				return true;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				// TODO Auto-generated method stub
				switch (item.getItemId()) {

					case R.id.item_delete:
						nr = 0;
						mAdapter.clearSelection();
						//db.delete(row_Id);
						//getContentResolver().delete(BookContract.CONTENT_URI(String.valueOf(row_Id)), null, null);
						for(int i = 0; i<row_ids.size();i++){
							getContentResolver().delete(BookContract.CONTENT_URI(String.valueOf(row_ids.get(i))), null, null);
						}
						//mAdapter.getCursor().requery();
						//mode.finish();
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


//		try {
//			//db.open();
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		Cursor cursor = db.fetchAllBooks();
//		startManagingCursor(cursor);
//
//
//
		String[] columns = new String[] { BookContract.TITLE,
				BookContract.AUTHORS };
		int[] to = new int[] { android.R.id.text1, android.R.id.text2 };

		mAdapter = new BookAdapter(this, android.R.layout.simple_list_item_2,null);
		//mAdapter = new SelectionAdapter(this, android.R.layout.simple_list_item_2, null, columns, to);
		listView.setAdapter(mAdapter);
		LoaderManager lm = getLoaderManager();
		lm.initLoader(BOOK_LOADER_ID, null, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// TODO provide ADD, DELETE and CHECKOUT options
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
				// ADD provide the UI for adding a book
				// Intent addIntent = new Intent(this, AddBookActivity.class);
				// startActivityForResult(addIntent, ADD_REQUEST);
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
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {

		switch (id){
			case BOOK_LOADER_ID:
				String[] projections = {BookContract.COLUMN_ID, BookContract.TITLE, BookContract.AUTHORS };
				return new CursorLoader(this,
						BookProvider.CONTENT_URI,
						projections,null,null,null);
			case AUTHORS_LOADER_ID:
				//return

			default:
				return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		this.mAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		this.mAdapter.swapCursor(null);
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

				}
				break;
			// CHECKOUT: empty the shopping cart.
			case CHECKOUT_REQUEST:
				if(resultCode == RESULT_OK) {
					//db.deleteAll();
					getContentResolver().delete(BookContract.CONTENT_URI,null,null);

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



	private class SelectionAdapter extends SimpleCursorAdapter {

		private HashMap<Integer, Boolean> mSelection = new HashMap<Integer, Boolean>();
		public SelectionAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
			super(context, layout, c, from, to);
		}
		public void setNewSelection(int position, boolean value) {
			mSelection.put(position, value);
			notifyDataSetChanged();
		}
		public boolean isPositionChecked(int position) {
			Boolean result = mSelection.get(position);
			return result == null ? false : result;
		}
		public Set<Integer> getCurrentCheckedPosition() {
			return mSelection.keySet();
		}
		public void removeSelection(int position) {
			mSelection.remove(position);
			notifyDataSetChanged();
		}
		public void clearSelection() {
			mSelection = new HashMap<Integer, Boolean>();
			notifyDataSetChanged();
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