package com.example.creator_.PlayClass;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.creator_.R;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class ReaderActivity extends AppCompatActivity {
    private PDFView pdfView;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser user = mAuth.getCurrentUser();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int pageIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        init();
        Bundle arg = getIntent().getExtras();
        if (arg != null && user != null){
            pageIndex = 0;
            String idBook = arg.get("idBook").toString();
            db.collection("Book").document(idBook).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if (Objects.requireNonNull(snapshot).exists()){
                        int collChapter = Integer.parseInt(Objects.requireNonNull
                                (Objects.requireNonNull(snapshot.getData()).put("collChapter", 0))
                                .toString());
                        File file = new File(getExternalFilesDir(null)
                                +"/Books/"+idBook+"/Глава1.pdf");
                        pdfView.fromFile(file).swipeHorizontal(true).pageSnap(true).autoSpacing(true)
                                .pageFling(true).pageFitPolicy(FitPolicy.BOTH).load();

                    }
                }
            });


        }
    }
    private void init(){
        pdfView = findViewById(R.id.pdf_view);
    }
}
