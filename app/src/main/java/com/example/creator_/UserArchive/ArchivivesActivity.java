package com.example.creator_.UserArchive;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.creator_.UserArchive.FragmentArchive.MyLibraryFragment;
import com.example.creator_.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

public class ArchivivesActivity extends AppCompatActivity {
    private File booksDirt;
    private static final String TAG = "ArchivivesActivity";
    private ArrayList<String> listIdBook = new ArrayList<>();
    private final FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private final FirebaseUser user= mAuth.getCurrentUser();
    private final FirebaseStorage storage=FirebaseStorage.getInstance();
    private final StorageReference storageRef=storage.getReference();
    private final FirebaseFirestore db=FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archivives);
        Bundle arg = getIntent().getExtras();
        if (arg!=null){
            String deleteIdBook = arg.get("deleteIdBook").toString();
            Toast.makeText(ArchivivesActivity.this,deleteIdBook,Toast.LENGTH_LONG).show();
            CollectionReference Book = db.collection("Book");
            Book.document(deleteIdBook).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()){
                        int collChapter = Integer.parseInt(snapshot.getData().put("collChapter",0).toString());
                        for (int i = 0; i<collChapter; i++){
                            StorageReference fileRef =  storageRef.child(user.getUid()+"/Book/"+deleteIdBook+"/"+"Глава"+i+".pdf");
                            fileRef.delete().addOnSuccessListener(aVoid -> Log.d(TAG,"Good delete !!!"))
                                    .addOnFailureListener(e -> Log.e(TAG,e.getMessage()));
                        }
                        StorageReference imageRef = storageRef.child(user.getUid()+"/Book/"+deleteIdBook+"/coverArt.jpg");
                        imageRef.delete().addOnSuccessListener(aVoid -> Log.d(TAG,"Good delete !!!"))
                                .addOnFailureListener(e -> Log.e(TAG,e.getMessage()));
                        Book.document(deleteIdBook).delete().addOnSuccessListener(aVoid ->Log.d(TAG,"Good delete !!!"))
                                .addOnFailureListener(e -> Log.e(TAG,e.getMessage()));
                    }
                }
            });

        }
        booksDirt = new File(getExternalFilesDir(null)+"/Books");
        if (!booksDirt.exists()){
            if (booksDirt.mkdir()){
                Log.d(TAG,"Create OK");
            }
        }
        MyLibraryFragment myLibraryFragment=new MyLibraryFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.ArchiveFragment,myLibraryFragment).commit();



    }
}