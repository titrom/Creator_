package com.example.creator_.FragmentBar.Top;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.creator_.Adapters.AdapterSearchBook.SearchItemClass;
import com.example.creator_.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class TopFragment extends Fragment {
    private final static String TAG = "TopFragment";
    private final FragmentTopLibrary fragmentTopLib = new FragmentTopLibrary();
    private final SearchCompleteFragment searchCompleteFragment = new SearchCompleteFragment();
    private String message;
    private FloatingActionButton stop;
    private EditText searchEdit;
    private MaterialToolbar search;


    protected ArrayList<SearchItemClass> sICList;


    private final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    private final FirebaseStorage storage=FirebaseStorage.getInstance();
    private final StorageReference storageRef=storage.getReference();
    private final FirebaseFirestore db=FirebaseFirestore.getInstance();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.top_fragment,container,false);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        fragmentSettings();

        search.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.search){
                getParentFragmentManager().beginTransaction().hide(fragmentTopLib).commit();
                search.setTitle(null);
                stop.setVisibility(View.VISIBLE);
                searchEdit.setVisibility(View.VISIBLE);
                stopSettings();
                message();
                bookSearch();
                return true;
            }
            return false;
        });
    }



    @RequiresApi(api = Build.VERSION_CODES.N)
    private void bookSearch(){
        if (message != null && !message.isEmpty()){
            db.collection("Book").whereEqualTo("privacyLevel", true).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document: Objects.requireNonNull(task.getResult())){
                        Log.d("TAG", document.getData().toString());
                        sICList = new ArrayList<>();
                        String nameBook = Objects.requireNonNull(document.getData().get("nameBook")).toString();
                        if (nameBook.contains(message)) {
                            String userId = Objects.requireNonNull(document.getData().get("userId")).toString();
                            int subColl = Integer.parseInt(Objects.requireNonNull(document.getData().get("subColl")).toString());
                            storageRef.child
                                    (userId + "/" + "Book/" + document.getId() + "/" + "coverArt" + ".jpg")
                                    .getDownloadUrl().addOnSuccessListener(uri -> {
                                sICList.add(new SearchItemClass(nameBook, uri, document.getId(), subColl, userId));
                                Collections.sort(sICList, (o1, o2) -> Double.compare(o2.getSubCollS(), o1.getSubCollS()));
                            });
                        }
                    }

                }

            });
        }
    }


    private void stopSettings(){
        stop.setOnClickListener(v -> {
            if (!searchEdit.getText().toString().trim().isEmpty()){
                searchEdit.setText("");
            }else {
                search.setTitle(R.string.search);
                stop.setVisibility(View.INVISIBLE);
                searchEdit.setVisibility(View.INVISIBLE);
                getParentFragmentManager().beginTransaction().show(fragmentTopLib).commit();
                getParentFragmentManager().beginTransaction().hide(searchCompleteFragment).commit();
            }
        });
    }

    private void message(){
        if (!searchEdit.getText().toString().trim().isEmpty()){
            message = searchEdit.getText().toString();
            getParentFragmentManager().beginTransaction().show(searchCompleteFragment).commit();
        }else{
            message = null;
            if (sICList != null) sICList.clear();
        }
    }

    private void fragmentSettings(){
        getParentFragmentManager().beginTransaction().add(R.id.fragment_top,fragmentTopLib).commit();
        getParentFragmentManager().beginTransaction().add(R.id.fragment_top, searchCompleteFragment).hide(searchCompleteFragment).commit();

    }


    private void init(View view){
        searchEdit = view.findViewById(R.id.searchEditText);
        search = view.findViewById(R.id.topSearchBar);
        stop = view.findViewById(R.id.stopSearch);
    }
}
