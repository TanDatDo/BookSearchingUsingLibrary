package com.dan.booksearching;

/**
 * Created by Dat T Do on 7/14/2017.
 */

public class Book {

    /**
     * Image resource of the book
     */
    private String mImageUrl;

    /**
     * Title of the book
     */
    private String mTitle;


    /**
     * Author name of the book
     */
    private String mAuthor;

    /**
     * Constructs a new {@link Book} object.
     *
     * @param imageUrl is the image resource id of the book
     * @param title    is the title of the book
     * @param author   is the author name of the book
     */

    public Book(String imageUrl, String title, String author) {
        mImageUrl = imageUrl;
        mTitle = title;
        mAuthor = author;
//        mPrice=price;
//        mCurrency=currency;
    }


    /**
     * Returns the image resource of the book
     */
    public String getImageUrl() {
        return mImageUrl;
    }

    /**
     * Returns the title of the book
     */
    public String getTitle() {
        return mTitle;
    }


    /**
     * Returns the author name of the book
     */
    public String getAuthor() {
        return mAuthor;
    }

}

