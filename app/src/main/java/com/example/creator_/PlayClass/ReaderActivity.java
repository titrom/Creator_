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

import java.util.ArrayList;
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
        pdfView = findViewById(R.id.pdf_view);
        Bundle arg = getIntent().getExtras();
        if (arg != null && user != null){
            String idBook = arg.get("idBook").toString();
            db.collection("UserProfile").document(user.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()){
                        if (snapshot.getData().get(idBook)!= null){
                            HashMap<String, Long> chapterAndPage = (HashMap<String, Long>) snapshot.getData().get(idBook);
                            long page = chapterAndPage.get("page");
                            File file = new File(getExternalFilesDir(null)+"/Books/"+idBook+"/Глава1.pdf");
                            pdfView.fromFile(file).defaultPage(1).swipeHorizontal(true).pageSnap(true).autoSpacing(true)
                                    .pageFling(true).pageFitPolicy(FitPolicy.WIDTH).load();
                        }else {
                            Log.d(TAG,"Good!!");
                            HashMap<String,Integer> pageAndChapter = new HashMap<>();
                            pageAndChapter.put("chapter",1);
                            pageAndChapter.put("page",0);
                            db.collection("UserProfile").document(user.getUid()).update(idBook,pageAndChapter)
                                    .addOnSuccessListener(aVoid -> Log.d(TAG,"Good update !!!"))
                                    .addOnFailureListener(e -> Log.e(TAG,e.getMessage()));
                        }

                    }
                }
            });


        }
    }
}
