package com.dan.booksearching;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Dat T Do on 7/14/2017.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    static class ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView authorTextView;
    }


    /**
     * Constructs a new {@link BookAdapter}.
     *
     * @param context     of the app
     * @param books is the list of books, which is the data source of the adapter
     */
    public BookAdapter(Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    /**
     * Returns a list item view that displays information about the book at the given position
     * in the list of boooks.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.book_list_item, parent, false);
            viewHolder = new ViewHolder();
            //Find the ImageView with view ID imag
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_view);
            viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.title_text_view);
            viewHolder.authorTextView = (TextView) convertView.findViewById(R.id.author_text_view);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Find the book at the given position in the list of books
        Book currentBook = getItem(position);

        //Display the image of the current book in that ImageView
        String imageURl = currentBook.getImageUrl();
        if (imageURl.isEmpty()) {
            viewHolder.imageView.setImageResource(R.drawable.notebook);
        } else {
            // set image to image view from image url
            AsyncTask<ImageView, Void, Bitmap> downloadImageAsync = new DownloadImageAsync(imageURl).execute(viewHolder.imageView);
        }

        //Display the title of the current book in that ImageView
        viewHolder.titleTextView.setText(currentBook.getTitle());

        //Display the image of the current book in that ImageView
        viewHolder.authorTextView.setText(currentBook.getAuthor());

        // Return the list item view that is now showing the appropriate data
        return convertView;
    }


    //To download the image
    public class DownloadImageAsync extends AsyncTask<ImageView,Void, Bitmap> {
        private ImageView mImageView;
        private String url;

        public DownloadImageAsync(String url) {
            this.url = url;
        }

        @Override
        //request image bitmap data in background thread
        protected Bitmap doInBackground(ImageView... imageViews) {
            this.mImageView = imageViews[0];
            return download_Images(url);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        //set the image to the Image View using with Bitmap data
        protected void onPostExecute(Bitmap result) {
            if (mImageView.getDrawable() == null) {
                mImageView.setImageBitmap(result);
            }
        }

        // method to fetch Bitmap data of the image using image URL
        private Bitmap download_Images(String urlParam) {
            Bitmap bmp = null;
            try {
                URL url = new URL(urlParam);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                InputStream is = con.getInputStream();
                bmp = BitmapFactory.decodeStream(is);
                if (null != bmp) {
                    return bmp;
                }
            } catch (Exception e) {
            }
            return bmp;
        }

    }
}
