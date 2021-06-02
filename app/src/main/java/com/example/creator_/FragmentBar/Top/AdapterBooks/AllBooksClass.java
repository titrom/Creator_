package com.example.creator_.FragmentBar.Top.AdapterBooks;

import android.net.Uri;

import java.sql.Timestamp;

public class AllBooksClass {
    private final String nameBook;
    private final double timestamp;
    private final Uri imageBook;
    private final String idBook;
    private final String writerId;

    public AllBooksClass(String nameBook, double timestamp, Uri imageBook, String idBook, String writerId) {
        this.nameBook = nameBook;
        this.timestamp = timestamp;
        this.imageBook = imageBook;
        this.idBook = idBook;
        this.writerId = writerId;
    }

    public String getIdBook() {
        return idBook;
    }

    public String getWriterId() {
        return writerId;
    }

    public String getNameBook() {
        return nameBook;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public Uri getImageBook() {
        return imageBook;
    }
}
