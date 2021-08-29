package com.example.creator_.Adapters.AdapterUsers;

import android.net.Uri;

public class AllUsers {
    private final String nameUser;
    private final Uri imageUser;
    private final String idUser;
    private final int subColl;

    public AllUsers(String nameUser, Uri imageUser, String idUser, int subColl) {
        this.nameUser = nameUser;
        this.imageUser = imageUser;
        this.idUser = idUser;
        this.subColl = subColl;
    }

    public String getNameUser() {
        return nameUser;
    }

    public Uri getImageUser() {
        return imageUser;
    }

    public String getIdUser() {
        return idUser;
    }

    public int getSubColl() {
        return subColl;
    }
}
