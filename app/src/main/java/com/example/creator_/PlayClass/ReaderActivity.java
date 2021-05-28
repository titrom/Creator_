package com.example.creator_.PlayClass;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.creator_.R;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.io.File;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class ReaderActivity extends AppCompatActivity {
    private final static String TAG = "ReaderActivity";
    private PDFView pdfView;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser user = mAuth.getCurrentUser();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        init();
        Bundle arg = getIntent().getExtras();
        if (arg != null && user != null){
            String idBook = arg.get("idBook").toString();
            db.collection("UserProfile").document(user.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()){
                        HashMap<String,Integer> chapterAndPage = (HashMap<String, Integer>) snapshot.getData().put(idBook,null);
                        Toast.makeText(ReaderActivity.this,String.valueOf(chapterAndPage.get("page")),Toast.LENGTH_LONG).show();
                        File file = new File(getExternalFilesDir(null)+"/Books/"+idBook+"/Глава1.pdf");
                        pdfView.fromFile(file).defaultPage(0).swipeHorizontal(true).pageSnap(true).autoSpacing(true)
                                .pageFling(true).pageFitPolicy(FitPolicy.WIDTH).load();
                    }
                }
            }).addOnFailureListener(e -> Log.d(TAG,e.getMessage()));
        }
    }
    private void init(){
        pdfView = findViewById(R.id.pdf_view);
    }
}
