package com.example.creator_.InsideBooks.FragmentsContentsBook.UserLib;

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

import com.example.creator_.InsideBooks.FragmentsContentsBook.RecyclerChapter.AdapterChapter;
import com.example.creator_.InsideBooks.FragmentsContentsBook.RecyclerChapter.ChapterClass;
import com.example.creator_.PlayActivities.ReaderActivity;
import com.example.creator_.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class ChapterFragment extends Fragment {
    private static final String TAG = "ChapterFragment";
    private RecyclerView chapterRV;
    private String idBook;
    private File bookDir;
    private int i;
    protected  boolean create = false;
    private final ArrayList<ChapterClass> ccList = new ArrayList<>();;
    private final FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private final FirebaseUser user= mAuth.getCurrentUser();
    private final FirebaseFirestore db=FirebaseFirestore.getInstance();;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.chapter_fragment_layout,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        chapterRV = view.findViewById(R.id.chapterRV);
    }
    protected void updateChapter(){
        if (ccList.size()!= 0){
            chapterRV.setAdapter(null);
            ccList.clear();
        }
        OwnerBookToolsActivity ownerBookToolsActivity = (OwnerBookToolsActivity) this.getActivity();
        idBook = Objects.requireNonNull(ownerBookToolsActivity).StringIdBook();
        if (idBook!= null){
            create = true;
        }
        bookDir = new File(requireContext().getExternalFilesDir(null)+"/Books/"+idBook);
        if (!bookDir.exists()){
            if (bookDir.mkdir()){
                Log.d(TAG,"Good!!");
            }
        }
        if (user!=null){
            db.collection("Book").document(idBook).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if (Objects.requireNonNull(snapshot).exists()){
                        int collChapter = Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(snapshot.getData()).get("collChapter")).toString());
                        for (i=1;i<=collChapter;i++){
                            File localFile = new File(bookDir+ "/" + "Глава"+ i + ".pdf");
                            if (!localFile.exists()){
                                ccList.add(new ChapterClass("Глава"+i,false));
                            }else {
                                ccList.add(new ChapterClass("Глава"+i,true));

                            }
                            AdapterChapter.OnClickChapter oCC = (cc, position) -> {
                                db.collection("UserProfile").document(user.getUid())
                                        .update(idBook+".chapter",position+1)
                                        .addOnSuccessListener(aVoid -> Log.d(TAG,"Good update !!! "))
                                        .addOnFailureListener(e -> Log.e(TAG,e.getMessage()));
                                db.collection("UserProfile").document(user.getUid())
                                        .update(idBook+".page",0)
                                        .addOnSuccessListener(aVoid -> Log.d(TAG,"Good update !!! "))
                                        .addOnFailureListener(e -> Log.e(TAG,e.getMessage()));
                                Intent intent = new Intent(getContext(), ReaderActivity.class);
                                intent.putExtra("idBook",idBook);
                                startActivity(intent);
                            };


                            chapterRV.setLayoutManager(new LinearLayoutManager(getContext()));
                            AdapterChapter adapter = new AdapterChapter(ccList,oCC,getContext());
                            chapterRV.setAdapter(adapter);
                        }

                    }
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateChapter();
    }
}
