package com.example.creator_.UserArchive.FragmentArchive;
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
import com.example.creator_.UserArchive.AddActivity.AddBookActivity;
import com.example.creator_.InsideBooks.OwnerBookToolsActivity;
import com.example.creator_.R;
import com.example.creator_.RecyclerMyBook.AdapterRecyclerMyBook;
import com.example.creator_.RecyclerMyBook.MyBookClass;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
public class MyLibraryFragment extends Fragment {
    private RecyclerView rv;
    private ArrayList<MyBookClass> myBC;
    private final String TAG="MyLibraryFragment";
    private SwipeRefreshLayout swipeRefreshLayout;
    private final FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private final FirebaseUser user= mAuth.getCurrentUser();
    private final FirebaseStorage storage=FirebaseStorage.getInstance();
    private final StorageReference storageRef=storage.getReference();
    private final FirebaseFirestore db=FirebaseFirestore.getInstance();
    private boolean userClickBookItem;

    @Override
    public void onStart() {
        super.onStart();
        userClickBookItem = false;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.archive_my_library_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FloatingActionButton buttonAddBook = view.findViewById(R.id.add_book);
        rv=view.findViewById(R.id.MyLibraryRecycler);
        swipeRefreshLayout=view.findViewById(R.id.swipe_library);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
            LoadListMyBook();

        });
        swipeRefreshLayout.setColorSchemeResources(R.color.ItemColor);
        DocumentReference docRef=db.collection("UserProfile").document(Objects.requireNonNull(user).getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot snapshot=task.getResult();
                if (Objects.requireNonNull(snapshot).exists()){
                    ArrayList<String> listBook= (ArrayList<String>) Objects.requireNonNull(snapshot.getData()).put("listIdBook","");
                    swipeRefreshLayout.setRefreshing(Objects.requireNonNull(listBook).size() != 0);
                }
            }
        });
        LoadListMyBook();

        buttonAddBook.setOnClickListener(v -> {
            Intent intent=new Intent(view.getContext(), AddBookActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {
            Log.i(TAG, "Refresh menu item selected");
            swipeRefreshLayout.setRefreshing(true);
            LoadListMyBook();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void LoadListMyBook(){
        rv.setAdapter(null);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        myBC= new ArrayList<>();
        if (user!=null){
            DocumentReference docRef=db.collection("UserProfile").document(user.getUid());
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot snapshot=task.getResult();
                    if (Objects.requireNonNull(snapshot).exists()){
                        Log.d(TAG, "DocumentSnapshot data: " + snapshot.getData());
                        ArrayList<String> listBook=(ArrayList<String>) snapshot.getData().put("listIdBook","");
                        if (Objects.requireNonNull(listBook).size()!=0){
                            for (String i: Objects.requireNonNull(listBook)) {
                                CollectionReference Book=db.collection("Book");
                                Book.whereEqualTo("userId",user.getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
                                    for ( int f=1;f<=queryDocumentSnapshots.size();f++){
                                        DocumentSnapshot lastVisible=queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size()-f);
                                        storageRef.child(user.getUid() + "/" + "Book/" + lastVisible.getId()+ "/" + "coverArt" +".jpg").getDownloadUrl().addOnSuccessListener(uri -> {
                                            if (i.equals(lastVisible.getId())){
                                                String nameBook= (String) Objects.requireNonNull(lastVisible.getData()).put("nameBook","");
                                                Timestamp timestamp= (Timestamp) lastVisible.getData().put("dateBook",null);
                                                boolean privacy=(boolean) lastVisible.getData().put("privacyLevel",false);
                                                myBC.add(new MyBookClass(uri,nameBook, (int) Objects.requireNonNull(timestamp).getSeconds(),privacy));
                                                Collections.sort(myBC, (o1, o2) -> Double.compare(o1.getTimestamp(), o2.getTimestamp()));
                                                AdapterRecyclerMyBook.OnClickBookRec oCBR= (mBC, position) -> {
                                                    if(!userClickBookItem) {
                                                        userClickBookItem = true;
                                                        Intent intent = new Intent(getContext(), OwnerBookToolsActivity.class);
                                                        intent.putExtra("idBook", listBook.get(position));
                                                        startActivity(intent);
                                                    }
                                                };
                                                AdapterRecyclerMyBook adapterRecyclerMyBook=new AdapterRecyclerMyBook(myBC,getContext(),oCBR);
                                                rv.setAdapter(adapterRecyclerMyBook);
                                                swipeRefreshLayout.setRefreshing(false);
                                            }
                                        }).addOnFailureListener(e -> {

                                        });
                                    }
                                });
                            }
                        }else {
                            swipeRefreshLayout.setRefreshing(false);
                        }

                    }else {
                        Log.d(TAG, "No such document");
                    }
                }else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            });
        }
    }

}
