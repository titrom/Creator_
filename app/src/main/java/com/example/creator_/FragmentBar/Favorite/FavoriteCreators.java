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

import com.example.creator_.Adapters.AdapterFavoriteUser.AdapterFavoriteUser;
import com.example.creator_.Adapters.AdapterFavoriteUser.FavoriteUserClass;
import com.example.creator_.Profile.ProfileInfActivity;
import com.example.creator_.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Objects;

public class FavoriteCreators extends Fragment {
    private final static String TAG = "FavoriteCreators";
    private RecyclerView favoriteUser;
    private final ArrayList<FavoriteUserClass> fUCs = new ArrayList<>();
    private  AdapterFavoriteUser.OnClickFavoriteUser oCFU;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final FirebaseStorage storage=FirebaseStorage.getInstance();
    private final StorageReference storageRef=storage.getReference();
    private final FirebaseFirestore db=FirebaseFirestore.getInstance();
    private boolean create = false;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorite_creators,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        favoriteUser = view.findViewById(R.id.favoriteUser);
        favoriteUser.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        favoriteUsers();
        Log.d(TAG,"onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
        fUCs.clear();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG,"onStop");
    }

    private void favoriteUsers(){
        if (user != null){
            db.collection("UserProfile").document(user.getUid()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if (Objects.requireNonNull(snapshot).exists()){
                        if (Objects.requireNonNull(snapshot.getData()).get("myWriterId")!=null){
                            ArrayList<String> myWriterId = (ArrayList<String>) Objects.requireNonNull(snapshot.getData())
                                    .get("myWriterId");
                            for (String i : Objects.requireNonNull(myWriterId)){
                                StorageReference avatarRef = storageRef.child(i  + "/Avatar.jpg");
                                avatarRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    db.collection("UserProfile").document(i).get().addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()){
                                            DocumentSnapshot document = task1.getResult();
                                            if (Objects.requireNonNull(document).exists()){
                                                fUCs.add(new FavoriteUserClass(Objects.requireNonNull(Objects.requireNonNull(document.getData()).get("nickname")).toString()
                                                        ,uri,i));
                                                oCFU = (fUC, position) -> {
                                                    Intent intent = new Intent(getContext(), ProfileInfActivity.class);
                                                    intent.putExtra("userId",fUC.getIdUser());
                                                    startActivity(intent);
                                                };
                                                AdapterFavoriteUser adapterFU = new AdapterFavoriteUser(fUCs,oCFU,getContext());
                                                favoriteUser.setAdapter(adapterFU);
                                                create = true;
                                            }
                                        }
                                    });
                                }).addOnFailureListener(e -> {
                                    Log.e(TAG,e.getMessage());
                                    db.collection("UserProfile").document(i).get().addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()){
                                            DocumentSnapshot document = task1.getResult();
                                            if (Objects.requireNonNull(document).exists()){
                                                fUCs.add(new FavoriteUserClass(Objects.requireNonNull(Objects.requireNonNull(document.getData()).get("nickname")).toString()
                                                        ,null,i));
                                                oCFU = (fUC, position) -> {
                                                    Intent intent = new Intent(getContext(), ProfileInfActivity.class);
                                                    intent.putExtra("userId",fUC.getIdUser());
                                                    startActivity(intent);
                                                };
                                                AdapterFavoriteUser adapterFU = new AdapterFavoriteUser(fUCs,oCFU,getContext());
                                                favoriteUser.setAdapter(adapterFU);
                                                create = true;
                                            }
                                        }
                                    });
                                });
                            }

                        }
                    }
                }
            });

        }


    }
}
