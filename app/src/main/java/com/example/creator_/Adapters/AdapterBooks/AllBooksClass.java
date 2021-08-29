package com.example.creator_.Adapters.AdapterBooks;

import android.net.Uri;

public class AllBooksClass {
    private final String nameBook;
    private final double timestamp;
    private final Uri imageBook;
    private final String idBook;
    private final String writerId;
    private final int subColl;

    public AllBooksClass(String nameBook, double timestamp, Uri imageBook, String idBook, String writerId, int subColl) {
        this.nameBook = nameBook;
        this.timestamp = timestamp;
        this.imageBook = imageBook;
        this.idBook = idBook;
        this.writerId = writerId;
        this.subColl = subColl;
    }

    public int getSubColl() {
        return subColl;
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
