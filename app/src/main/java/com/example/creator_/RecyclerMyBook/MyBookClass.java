package com.example.creator_.RecyclerMyBook;

import android.net.Uri;
public class MyBookClass {
    private final Uri ImageBook;
    private final String NameBook;
    private final int Timestamp;
    private final boolean privacy;
    private final String idBook;

    public MyBookClass(Uri imageBook, String nameBook, int timestamp, boolean privacy,String idBook) {
        ImageBook = imageBook;
        NameBook = nameBook;
        Timestamp = timestamp;
        this.privacy = privacy;
        this.idBook = idBook;
    }

    public String getIdBook() {
        return idBook;
    }

    public boolean isPrivacy() {
        return privacy;
    }

    public double getTimestamp() {
        return Timestamp;
    }

    public Uri getImageBook() {
        return ImageBook;
    }


    public String getNameBook() {
        return NameBook;
    }
}
