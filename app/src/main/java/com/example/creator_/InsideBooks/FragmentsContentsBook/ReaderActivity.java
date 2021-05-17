package com.example.creator_.InsideBooks.FragmentsContentsBook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.creator_.R;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ReaderActivity extends AppCompatActivity {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser user = mAuth.getCurrentUser();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private final StorageReference storageRef = storage.getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        init();
        Bundle arg = getIntent().getExtras();
        if (arg != null && user != null){
            String idBook = arg.get("idBook").toString();
            ArrayList<String> filename = (ArrayList<String>) arg.get("filename");
            Toast.makeText(ReaderActivity.this, filename.get(0), Toast.LENGTH_SHORT).show();
        }
    }
    private void init(){

    }
}
