package com.dan.booksearching;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.duration;
import static android.view.View.GONE;

public class BookActivity extends AppCompatActivity implements LoaderCallbacks<List<Book>> {

    public static final String LOG_TAG = BookActivity.class.getName();

    public static final String DEFAULT_BOOK_REQUEST_URL="https://www.googleapis.com/books/v1/volumes?q=gone&maxResults=5";

    public static final String BOOK_REQUEST_STEM="https://www.googleapis.com/books/v1/volumes?q=";

    public static final String MAX_REQUEST_NUMBER="&maxResults=6";

    public static String BOOK_REQUEST_URL;

    private ListView listView;

    private TextView emptyTextView;


    /**
     * Constant value for the book loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    public int BOOK_LOADER_ID=0;

    /** Adapter for the list of book */
    private BookAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        open default search when the app launching

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

//        set the book request url to default url
        BOOK_REQUEST_URL=DEFAULT_BOOK_REQUEST_URL;


        emptyTextView = (TextView) findViewById(R.id.empty_text_view);

        listView=(ListView) findViewById(R.id.list);
        mAdapter = new BookAdapter(this, new ArrayList<Book>());
        listView.setAdapter(mAdapter);
        ImageView searchImage = (ImageView) findViewById(R.id.search_image);

        //the app start to search for new key word (is typed by the users) when the users hit the search image
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    // connection is okay and fetch data
                    emptyTextView.setVisibility(GONE);
                    listView.setVisibility(View.VISIBLE);
                    EditText searchEditText = (EditText) findViewById(R.id.search_edit_text);
                    String searchKeyWord = searchEditText.getText().toString().trim();
                    searchKeyWord = searchKeyWord.replace(" ", "");

                    if (searchKeyWord.isEmpty()) {
                        //user type no key word, inform them to type
                        Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.entry_request), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                    else {
                        BOOK_REQUEST_URL = "";
                        BOOK_REQUEST_URL = BOOK_REQUEST_STEM + searchKeyWord+ MAX_REQUEST_NUMBER ;
                        //restart loader, fetch new data
                        getLoaderManager().restartLoader(BOOK_LOADER_ID, null, BookActivity.this);
                    }


                } else {
                    // No network connection, inform user
                    listView.setVisibility(GONE);
                    emptyTextView.setVisibility(View.VISIBLE);
                    emptyTextView.setText(getString(R.string.no_internet));
                    Log.e(LOG_TAG, getString(R.string.no_internet));
                }
            }
        });


        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);


        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        }
        else{
            // no internet, inform the user
            listView.setVisibility(GONE);
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(R.string.no_internet);
        }
    }




    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        return new BookLoader(this, BOOK_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {

        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            //show the results
            mAdapter.addAll(books);
            listView.setAdapter(mAdapter);
        }else {
            //no results found, inform the users
            listView.setVisibility(GONE);
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(getString(R.string.no_result));
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

}
