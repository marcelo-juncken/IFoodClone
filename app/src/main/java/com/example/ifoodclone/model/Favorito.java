package com.example.ifoodclone.model;

import com.example.ifoodclone.helper.FirebaseHelper;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class Favorito {
    public void salvar(List<String> favoritoList){
        DatabaseReference favoritosRef = FirebaseHelper.getDatabaseReference()
                .child("favoritos")
                .child(FirebaseHelper.getIdFirebase());
        favoritosRef.setValue(favoritoList);

    };
}
