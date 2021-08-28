package com.example.creator_.UserArchive.FragmentArchive;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.creator_.R;
import com.example.creator_.Adapters.RecyclerMyBook.MyBookClass;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
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
import java.util.Objects;

public class ArchivivesActivity extends AppCompatActivity {
    private File booksDirt;
    private InputMethodManager imm;
    private static final String TAG = "ArchivivesActivity";
    private final ArrayList<String> listIdBook = new ArrayList<>();
    private final FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private final FirebaseUser user= mAuth.getCurrentUser();
    private final FirebaseStorage storage=FirebaseStorage.getInstance();
    private final StorageReference storageRef=storage.getReference();
    private final FirebaseFirestore db=FirebaseFirestore.getInstance();
    private ArrayList<MyBookClass> getListBook = new ArrayList<>();
    private MyLibraryFragment myLibraryFragment=new MyLibraryFragment();
    private FloatingActionButton stop;
    private MaterialToolbar searchBar;
    private EditText BookNameSearch;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archivives);
        Bundle delete = getIntent().getExtras();
        if (delete != null){
            String deleteIdBook = delete.get("deleteIdBook").toString();
            deleteBook(deleteIdBook);
        }
        init();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        stop = findViewById(R.id.stopSearch);
        Bundle arg = getIntent().getExtras();
        if (arg!=null){
            String deleteIdBook = arg.get("deleteIdBook").toString();
            Toast.makeText(ArchivivesActivity.this,deleteIdBook,Toast.LENGTH_LONG).show();
            CollectionReference Book = db.collection("Book");
            Book.document(deleteIdBook).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if (Objects.requireNonNull(snapshot).exists()){
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
        stop.setOnClickListener(v -> {
            if (BookNameSearch.getText().toString().trim().isEmpty()){
                BookNameSearch.setVisibility(View.INVISIBLE);
                searchBar.setTitle(R.string.search_archive);
                searchBar.setNavigationIcon(R.drawable.ic_back_item);
                stop.setVisibility(View.INVISIBLE);
                imm.hideSoftInputFromWindow(stop.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
            BookNameSearch.setText(null);
        });
        getSupportFragmentManager().beginTransaction().add(R.id.ArchiveFragment,myLibraryFragment).commit();
        searchBar.setNavigationOnClickListener(v -> onBackPressed());
        searchBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.search) {
                getListBook.clear();
                if (myLibraryFragment.created) {
                    searchBar.setTitle(null);
                    searchBar.setNavigationIcon(null);
                    stop.setVisibility(View.VISIBLE);
                    myLibraryFragment.getBookList(getListBook);
                    Log.d(TAG, getListBook.size() + "");
                    BookNameSearch.setVisibility(View.VISIBLE);
                    ArrayList<MyBookClass> setListBook = new ArrayList<>();
                    for (MyBookClass i : getListBook) {
                        if (!BookNameSearch.getText().toString().trim().isEmpty()
                                && i.getNameBook().contains(BookNameSearch.getText().toString())) {
                            setListBook.add(i);
                            BookNameSearch.setVisibility(View.INVISIBLE);
                            searchBar.setTitle(R.string.search_archive);
                            stop.setVisibility(View.INVISIBLE);
                            imm.hideSoftInputFromWindow(stop.getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }

                    if (setListBook.size() != 0) {
                        myLibraryFragment.setBookList(setListBook);
                        BookNameSearch.setText(null);
                    }
                }
                return true;
            }
            return false;
        });
    }

    private void deleteBook(String idBook){
        File bookDir = new File(getExternalFilesDir(null)+"/Books/"+idBook);
        if (bookDir.exists()) bookDir.delete();
        db.collection("Book").document(idBook).delete().addOnSuccessListener(unused -> {
            Log.d(TAG, "DocumentSnapshot successfully deleted!");
        }).addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
        db.collection("UserProfile").document(user.getUid()).update("listIdBook", FieldValue.arrayRemove(idBook))
                .addOnSuccessListener(unused -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
    }

    private void init(){
        searchBar = findViewById(R.id.searchArchive);
        BookNameSearch = findViewById(R.id.searchText);

    }

}