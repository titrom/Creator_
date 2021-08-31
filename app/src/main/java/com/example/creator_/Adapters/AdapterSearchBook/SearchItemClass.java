package com.example.creator_.Adapters.AdapterSearchBook;

import android.net.Uri;

public class SearchItemClass {
    private final String nameBookS;
    private final Uri imageBookS;
    private final String idBookS;
    private final int subCollS;
    private final String userIdS;

    public SearchItemClass(String nameBookS, Uri imageBookS, String idBookS, int subCollS, String userIdS) {
        this.nameBookS = nameBookS;
        this.imageBookS = imageBookS;
        this.idBookS = idBookS;
        this.subCollS = subCollS;

        this.userIdS = userIdS;
    }

    public String getUserIdS() {
        return userIdS;
    }

    public String getNameBookS() {
        return nameBookS;
    }

    public Uri getImageBookS() {
        return imageBookS;
    }

    public String getIdBookS() {
        return idBookS;
    }

    public int getSubCollS() {
        return subCollS;
    }
}
