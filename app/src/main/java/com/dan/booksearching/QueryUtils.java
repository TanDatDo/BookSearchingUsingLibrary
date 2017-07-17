package com.dan.booksearching;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Pipe;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.content.res.AssetManager;

import static com.dan.booksearching.BookActivity.LOG_TAG;


/**
 * Created by Dat T Do on 7/15/2017.
 */

public class QueryUtils {

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */

    private QueryUtils() {
    }

    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private static final String JSON = "";


    /**
     * Query the USGS dataset and return a list of {@link Book} objects.
     */
    public static List<Book> fetchBookData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Book}s
        List<Book> books = extractBooksFromJSON(jsonResponse);

        // Return the list of {@link Book}s
        return books;
    }


    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing a JSON response.
     */
    public static List<Book> extractBooksFromJSON(String jasonResponse) {


        // Create an empty ArrayList that we can start adding books to
        ArrayList<Book> books = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the SAMPLE_BOOK_RESPONSE string
            JSONObject baseJsonResponse = new JSONObject(jasonResponse);

            // Extract the JSONArray associated with the key called "items",
            // which represents a list of items (or books).

            if (baseJsonResponse.has("item")) {
                JSONArray itemArray = baseJsonResponse.getJSONArray("items");

                // For each book in the itemArray, create an {@link book} object
                for (int i = 0; i < itemArray.length(); i++) {

                    String mAuthor = "";
                    String mImageUrl = "";
                    String mTitle = "";

                    // Get a single book at position i within the list of books
                    JSONObject currentBook = itemArray.getJSONObject(i);

                    // For a given item, extract the JSONObject associated with the
                    // key called "volumeInfo", which represents a list of all information about volume
                    // for that book.
                    if (currentBook.has("volumeInfo")) {
                        JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                        //Extract the title for the key called title
                        mTitle = volumeInfo.getString("title");


                        //Extract the author name for the key called author
                        if (volumeInfo.has("authors")) {
                            JSONArray authorArray = volumeInfo.getJSONArray("authors");
                            StringBuilder authorStringBuilder = new StringBuilder();
                            authorStringBuilder.append(authorArray.getString(0));
                            for (int j = 1; j < authorArray.length(); j++) {
                                authorStringBuilder.append(", " + authorArray.getString(j));
                            }
                            mAuthor = authorStringBuilder.toString();
                        }

                        //Extract the image URl
                        if (volumeInfo.has("imageLinks")) {
                            JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                            mImageUrl = imageLinks.getString("smallThumbnail");
                        }
                    }

                    // Create a new {@link Book} object with the imageUrl, title, author, price, and currency
                    // from the JSON response.
                    Book book = new Book(mImageUrl, mTitle, mAuthor);

                    // Add the new {@link Book} to the list of books.
                    books.add(book);
                }
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }

        // Return the list of books
        return books;
    }


}






