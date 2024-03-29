package com.example.creator_.Profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.creator_.Adapters.RecyclerMyBook.AdapterRecyclerMyBook;
import com.example.creator_.Adapters.RecyclerMyBook.MyBookClass;
import com.example.creator_.InsideBooks.FragmentsContentsBook.Books.BookToolsActivity;
import com.example.creator_.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Objects;

public class ProfileInfActivity extends AppCompatActivity {
    private final static String TAG = "ProfileInfActivity";
    private ImageView imageUser;
    private TextView nickname, level,xp_progress, subColl;
    private LinearProgressIndicator expBar;
    private ExtendedFloatingActionButton subscribe;
    private boolean isSubscribe = false;
    private String userId;
    private RecyclerView userBook;
    private AdapterRecyclerMyBook.OnClickBookRec oCBR;
    private final ArrayList<MyBookClass> myBC =  new ArrayList<>();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final FirebaseStorage storage=FirebaseStorage.getInstance();
    private final StorageReference storageRef=storage.getReference();
    private final FirebaseFirestore db=FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_inf);
        init();
        Bundle arg = getIntent().getExtras();
        if (arg!= null){
            userId = arg.get("userId").toString();
            information();
            isSubscribe();
            userBook.setLayoutManager(new LinearLayoutManager(ProfileInfActivity.this));
            subscribe.setOnClickListener(v -> {
                clickSubscribe();
            });
        }

    }
    private void init(){
        userBook = findViewById(R.id.userBook);

        imageUser = findViewById(R.id.buttonImageUser);
        nickname = findViewById(R.id.nameTextView);
        level = findViewById(R.id.Level);
        xp_progress = findViewById(R.id.xp_point);
        subColl = findViewById(R.id.subColl);
        expBar = findViewById(R.id.Xp_progress);
        subscribe = findViewById(R.id.subscribe);

    }
    private void isSubscribe(){
        db.collection("UserProfile").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot snapshot = task.getResult();
                if (Objects.requireNonNull(snapshot).exists()){
                    ArrayList<String> subscribersId = (ArrayList<String>) Objects.requireNonNull(snapshot.getData()).get("subscribersId");
                    if (Objects.requireNonNull(subscribersId).contains(user.getUid())){
                        isSubscribe =true;
                        subscribe.setText("Вы подписаны");
                        subscribe.setIconResource(R.drawable.ic_done);
                    }
                    else {
                        isSubscribe = false;
                        subscribe.setText(R.string.subscribe);
                        subscribe.setIcon(null);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        userBookUpdate();
    }


    private void userBookUpdate(){
        myBC.clear();
        if (user!=null){
            db.collection("UserProfile").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if (Objects.requireNonNull(snapshot).exists()){
                        if (Objects.requireNonNull(snapshot.getData()).get("listIdBook")!=null){
                            ArrayList<String> listIdBook = (ArrayList<String>) snapshot.getData().get("listIdBook");
                            for (String i : Objects.requireNonNull(listIdBook)){
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
                                                    Intent intent = new Intent(ProfileInfActivity.this, BookToolsActivity.class);
                                                    intent.putExtra("idBook", mBC.getIdBook());
                                                    intent.putExtra("userId",mBC.getUserId());
                                                    intent.putExtra("settings", true);
                                                    startActivity(intent);
                                                };
                                                AdapterRecyclerMyBook adapterRecyclerMyBook = new AdapterRecyclerMyBook(myBC,this,oCBR);
                                                userBook.setAdapter(adapterRecyclerMyBook);
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

    private void clickSubscribe(){
        db.collection("UserProfile").document(user.getUid()).get().addOnCompleteListener(task -> {
           if (task.isSuccessful()){
               DocumentSnapshot snapshot = task.getResult();
               if (Objects.requireNonNull(snapshot).exists()){
                   if (Objects.requireNonNull(snapshot.getData()).get("myWriterId") == null){
                       db.collection("UserProfile").document(user.getUid())
                               .update("myWriterId",new ArrayList<String>())
                               .addOnSuccessListener(aVoid -> Log.d(TAG,"Good Update !!!"))
                               .addOnFailureListener(e -> Log.e(TAG,e.getMessage()));
                   }
               }
           }
        });
        if (!isSubscribe){
            db.collection("UserProfile").document(userId)
                    .update("xpUser",FieldValue.increment(5))
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Good update !11"))
                    .addOnFailureListener(e -> Log.e(TAG, e.getMessage()));
            db.collection("UserProfile").document(userId)
                    .update("subscribersId", FieldValue.arrayUnion(user.getUid()))
                    .addOnSuccessListener(aVoid -> Log.d(TAG,"Good update !!!"))
                    .addOnFailureListener(e -> Log.e(TAG,e.getMessage()));
            db.collection("UserProfile").document(user.getUid())
                    .update("myWriterId",FieldValue.arrayUnion(userId))
                    .addOnSuccessListener(aVoid ->Log.d(TAG,"Good update !!!"))
                    .addOnFailureListener(e -> Log.e(TAG,e.getMessage()));
        }
        else {
            db.collection("UserProfile").document(userId)
                    .update("xpUser",FieldValue.increment(-5))
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Good update !11"))
                    .addOnFailureListener(e -> Log.e(TAG, e.getMessage()));
            db.collection("UserProfile").document(userId)
                    .update("subscribersId", FieldValue.arrayRemove(user.getUid()))
                    .addOnSuccessListener(aVoid -> Log.d(TAG,"Good update !!!"))
                    .addOnFailureListener(e -> Log.e(TAG,e.getMessage()));
            db.collection("UserProfile").document(user.getUid())
                    .update("myWriterId",FieldValue.arrayRemove(userId))
                    .addOnSuccessListener(aVoid ->Log.d(TAG,"Good update !!!"))
                    .addOnFailureListener(e -> Log.e(TAG,e.getMessage()));
        }
        isSubscribe();
        information();
        userBookUpdate();
    }
    @SuppressLint("SetTextI18n")
    private void information(){
        StorageReference avatar = storageRef.child(userId  + "/Avatar.jpg");
        avatar.getDownloadUrl().addOnSuccessListener(uri -> Picasso.with(this).load(uri).into(imageUser))
                .addOnFailureListener(e -> Log.e(TAG,e.getMessage()));
        db.collection("UserProfile").document(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot snapshot = task.getResult();
                if (Objects.requireNonNull(snapshot).exists()){
                    nickname.setText(Objects.requireNonNull(Objects.requireNonNull(snapshot.getData()).get("nickname")).toString());
                    level.setText("Уровень:"+ Objects.requireNonNull(snapshot.getData().get("level")).toString());
                    xp_progress.setText(Objects.requireNonNull(snapshot.getData().get("xpUser")).toString()
                    + "/" + snapshot.getData().get("xpMax")+"XP");
                    subColl.setText("Подписчиков: " + ((ArrayList<String>) Objects.requireNonNull(snapshot.getData().get("subscribersId"))).size());
                    expBar.setProgress(Integer.parseInt(Objects.requireNonNull(snapshot.getData().get("xpUser")).toString()));

                }
            }
        });

    }
}