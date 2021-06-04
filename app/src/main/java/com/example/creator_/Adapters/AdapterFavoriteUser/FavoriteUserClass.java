package com.example.creator_.Adapters.AdapterFavoriteUser;

import android.net.Uri;

public class FavoriteUserClass {
    private final String nicknameFavoriteWriter;
    private final String idUser;
    private final Uri imageUser;

    public FavoriteUserClass(String nicknameFavoriteWriter, Uri imageUser,String idUser) {
        this.nicknameFavoriteWriter = nicknameFavoriteWriter;
        this.imageUser = imageUser;
        this.idUser = idUser;
    }

    public String getIdUser() {
        return idUser;
    }

    public String getNicknameFavoriteWriter() {
        return nicknameFavoriteWriter;
    }

    public Uri getImageUser() {
        return imageUser;
    }
}
