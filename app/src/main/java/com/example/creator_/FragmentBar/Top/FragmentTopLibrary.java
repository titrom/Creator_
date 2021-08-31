package com.example.creator_.FragmentBar.Top;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.creator_.Adapters.AdapterBooks.AdapterAllBooksRv;
import com.example.creator_.Adapters.AdapterBooks.AllBooksClass;
import com.example.creator_.Adapters.AdapterUsers.AdapterAllUsers;
import com.example.creator_.Adapters.AdapterUsers.AllUsers;
import com.example.creator_.Profile.ProfileInfActivity;
import com.example.creator_.R;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class FragmentTopLibrary extends Fragment {
    private final static String TAG = "FragmentTopLibrary";
    private RecyclerView rvNewTop;
    private RecyclerView rvBooksTop;
    private RecyclerView rvUsersTop;
    private AdapterAllBooksRv.OnClickAllBooksRv oCABRNT;
    private AdapterAllBooksRv.OnClickAllBooksRv oCABRTBs;
    private AdapterAllUsers.OnClickUsers oAAUs;
    private ArrayList<AllBooksClass> abcListNewTop = new ArrayList<>();
    private ArrayList<AllBooksClass> abcListTopBooks = new ArrayList<>();
    private ArrayList<AllUsers> auListTopUsers = new ArrayList<>();


    private SwipeRefreshLayout swipe;



    private final FirebaseFirestore db=FirebaseFirestore.getInstance();
    private final FirebaseStorage storage=FirebaseStorage.getInstance();
    private final StorageReference storageRef=storage.getReference();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.top_fragmen_library,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvNewTop = view.findViewById(R.id.top_new);
        rvBooksTop = view.findViewById(R.id.top_books);
        rvUsersTop = view.findViewById(R.id.top_users);
        swipe = view.findViewById(R.id.swipe);
        rvNewTop.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        rvBooksTop.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        rvUsersTop.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));

        update();
    }


    private void update(){
        swipe.setRefreshing(true);
        rvTopNew();
        rvTopBooks();
        rvTopUsers();
        swipe.setOnRefreshListener(() -> {
            Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
            rvTopNew();
            rvTopBooks();
            rvTopUsers();

        });
        swipe.setColorSchemeResources(R.color.itemColor);
    }


    protected void rvTopNew(){
        abcListNewTop.clear();
        db.collection("Book").whereEqualTo("privacyLevel", true).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())){
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    String nameBook = Objects.requireNonNull(document.getData().get("nameBook")).toString();
                    String userId = Objects.requireNonNull(document.getData().get("userId")).toString();
                    Timestamp timestamp = (Timestamp) document.getData().get("dateBook");
                    int subColl =Integer.parseInt(Objects.requireNonNull(document.getData().get("subColl")).toString());
                    storageRef.child
                            (userId+ "/" + "Book/" + document.getId() + "/" + "coverArt" + ".jpg")
                            .getDownloadUrl().addOnSuccessListener(uri -> {
                        abcListNewTop.add(new AllBooksClass(nameBook, timestamp.getSeconds(), uri,document.getId(),userId, subColl));
                        for (AllBooksClass i: abcListNewTop){
                            if (Timestamp.now().getSeconds() - i.getTimestamp() >= 604800){
                                abcListNewTop.remove(i);
                            }
                        }
                        Collections.sort(abcListNewTop, (o1, o2) -> Double.compare(o2.getTimestamp(), o1.getTimestamp()));
                        oCABRNT = (bc, position) -> {
                        };
                        AdapterAllBooksRv adapterABR = new AdapterAllBooksRv(abcListNewTop, oCABRNT,getContext());
                        rvNewTop.setAdapter(adapterABR);
                    }).addOnFailureListener(e -> {});
                }swipe.setRefreshing(false);

            }else Log.d(TAG, "Error getting documents: ", task.getException());
        });
    }


    protected void rvTopBooks(){
        abcListTopBooks.clear();
        db.collection("Book").whereEqualTo("privacyLevel", true).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())){
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    String nameBook = Objects.requireNonNull(document.getData().get("nameBook")).toString();
                    String userId = Objects.requireNonNull(document.getData().get("userId")).toString();
                    Timestamp timestamp = (Timestamp) document.getData().get("dateBook");
                    int subColl =Integer.parseInt(Objects.requireNonNull(document.getData().get("subColl")).toString());
                    storageRef.child
                            (userId+ "/" + "Book/" + document.getId() + "/" + "coverArt" + ".jpg")
                            .getDownloadUrl().addOnSuccessListener(uri -> {
                        abcListTopBooks.add(new AllBooksClass(nameBook, timestamp.getSeconds(), uri,document.getId(),userId, subColl));
                        for (AllBooksClass i: abcListTopBooks){
                            if (i.getSubColl() <= 2){
                                abcListTopBooks.remove(i);
                            }
                        }
                        Collections.sort(abcListTopBooks, (o1, o2) -> Double.compare(o2.getSubColl(), o1.getSubColl()));
                        oCABRTBs = (bc, position) -> {
                        };
                        AdapterAllBooksRv adapterABR = new AdapterAllBooksRv(abcListTopBooks, oCABRTBs,getContext());
                        rvBooksTop.setAdapter(adapterABR);
                    }).addOnFailureListener(e -> {});
                }swipe.setRefreshing(false);

            }else Log.d(TAG, "Error getting documents: ", task.getException());
        });
    }

    protected void rvTopUsers(){
        auListTopUsers.clear();
        db.collection("UserProfile").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())){
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    String nickname = Objects.requireNonNull(document.getData().get("nickname")).toString();
                    ArrayList<String> subId = (ArrayList<String>) document.getData().get("subscribersId");
                    storageRef.child(document.getId() + "/" + "Avatar.jpg").getDownloadUrl().addOnSuccessListener(uri -> {
                        auListTopUsers.add(new AllUsers(nickname, uri, document.getId(), Objects.requireNonNull(subId).size()));
                        for (AllUsers i: auListTopUsers){
                            if (i.getSubColl()<=5){
                                auListTopUsers.remove(i);
                            }
                        }
                        Collections.sort(auListTopUsers, (o1, o2) -> Double.compare(o2.getSubColl(), o1.getSubColl()));
                        oAAUs = (au, position) ->{
                            if (user != null) {
                                if (!au.getIdUser().equals(user.getUid())){
                                    Intent intent = new Intent(getActivity(), ProfileInfActivity.class);
                                    intent.putExtra("userId",document.getId());
                                    startActivity(intent);
                                }
                            }
                        };
                        AdapterAllUsers aAU = new AdapterAllUsers(auListTopUsers,oAAUs,getContext());
                        rvUsersTop.setAdapter(aAU);
                    }).addOnFailureListener(e -> Log.e(TAG,e.getMessage()));
                }swipe.setRefreshing(false);
            }else Log.d(TAG, "Error getting documents: ", task.getException());
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {
            Log.i(TAG, "Refresh menu item selected");
            swipe.setRefreshing(true);
            rvTopNew();
            rvTopBooks();
            rvTopUsers();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
