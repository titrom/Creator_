package com.example.creator_.FragmentBar.Top;

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

import com.example.creator_.FragmentBar.Top.AdapterBooks.AdapterAllBooksRv;
import com.example.creator_.FragmentBar.Top.AdapterBooks.AllBooksClass;
import com.example.creator_.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class FragmentTopLibrary extends Fragment {
    private final static String TAG = "FragmentTopLibrary";
    private RecyclerView rvBooks;
    private AdapterAllBooksRv.OnClickAllBooksRv oCABR;
    private ArrayList<AllBooksClass> abcList = new ArrayList<>();



    private final FirebaseFirestore db=FirebaseFirestore.getInstance();
    private final FirebaseStorage storage=FirebaseStorage.getInstance();
    private final StorageReference storageRef=storage.getReference();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.top_fragmen_library,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvBooks = view.findViewById(R.id.rv_books);
        rvBooks.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
//        BooksRv();
    }

    protected void BooksRv(){
        abcList.clear();
        db.collection("Book").whereEqualTo("privacyLevel", true).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())){
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    String nameBook = Objects.requireNonNull(document.getData().get("nameBook")).toString();
                    String userId = Objects.requireNonNull(document.getData().get("userId")).toString();
                    Timestamp timestamp = (Timestamp) document.getData().get("dateBook");
                    storageRef.child
                            (userId+ "/" + "Book/" + document.getId() + "/" + "coverArt" + ".jpg")
                            .getDownloadUrl().addOnSuccessListener(uri -> {
                        abcList.add(new AllBooksClass(nameBook, timestamp.getSeconds(), uri,document.getId(),userId));
                        Collections.sort(abcList, (o1, o2) -> Double.compare(o2.getTimestamp(), o1.getTimestamp()));
                        oCABR = (bc, position) -> {
                        };
                        AdapterAllBooksRv adapterABR = new AdapterAllBooksRv(abcList,oCABR,getContext());
                        rvBooks.setAdapter(adapterABR);
                    }).addOnFailureListener(e -> {});
                    TopFragment topFragment = (TopFragment) getParentFragment();
                    if (topFragment.updateTop!=null){
                        topFragment.updateTop.setRefreshing(false);
                    }
                }

            }else Log.d(TAG, "Error getting documents: ", task.getException());
        });


    }
}
