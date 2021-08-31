package com.example.creator_.FragmentBar.Favorite;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.creator_.Adapters.RecyclerMyBook.AdapterRecyclerMyBook;
import com.example.creator_.Adapters.RecyclerMyBook.MyBookClass;
import com.example.creator_.InsideBooks.FragmentsContentsBook.Books.BookToolsActivity;
import com.example.creator_.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.Objects;


public class FavoriteCreations extends Fragment {
    private final static String TAG = "FavoriteCreations";
    private RecyclerView favoriteBook;
    private AdapterRecyclerMyBook.OnClickBookRec oCBR;
    private final ArrayList<MyBookClass> myBC =  new ArrayList<>();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser user = mAuth.getCurrentUser();
    private final FirebaseStorage storage=FirebaseStorage.getInstance();
    private final StorageReference storageRef=storage.getReference();
    private final FirebaseFirestore db=FirebaseFirestore.getInstance();
    protected boolean create = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorite_creations,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        favoriteBook = view.findViewById(R.id.favoriteBook);
        favoriteBook.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    @Override
    public void onResume() {
        super.onResume();
        myBC.clear();
        myFavoriteBook();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
        myBC.clear();
    }

    protected void myFavoriteBook(){

        if (user!=null){
            db.collection("UserProfile").document(user.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if (Objects.requireNonNull(snapshot).exists()){
                        if (Objects.requireNonNull(snapshot.getData()).get("subBook")!=null){
                            ArrayList<String> subBook = (ArrayList<String>) snapshot.getData().get("subBook");
                            for (String i : Objects.requireNonNull(subBook)){
                                db.collection("Book").document(i).get().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()){
                                        DocumentSnapshot document = task1.getResult();
                                        if (Objects.requireNonNull(document).exists()){
                                            Log.d(TAG, "Good");
                                            StorageReference imageRef = storageRef.child(Objects.requireNonNull(document.getData()).get("userId")+"/Book/"+i+"/coverArt.jpg");
                                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                                myBC.add(new MyBookClass(uri, Objects.requireNonNull(document.getData().get("nameBook")).toString(),
                                                        (int) ((Timestamp) Objects.requireNonNull(document.getData().get("dateBook"))).getSeconds(),
                                                        (boolean) document.getData().get("privacyLevel"), i,
                                                        document.getData().get("userId").toString()));
                                                oCBR = (mBC, position) -> {
                                                    Intent intent = new Intent(getContext(), BookToolsActivity.class);
                                                    intent.putExtra("idBook", mBC.getIdBook());
                                                    intent.putExtra("userId",mBC.getUserId());
                                                    startActivity(intent);
                                                };
                                                AdapterRecyclerMyBook adapterRecyclerMyBook = new AdapterRecyclerMyBook(myBC,getContext(),oCBR);
                                                favoriteBook.setAdapter(adapterRecyclerMyBook);
                                                create = true;
                                            }).addOnFailureListener(e -> Log.e(TAG,e.getMessage()));
                                        }
                                    }
                                });
                            }

                        }
                    }
                }
            });

        }
    }
}
