package com.example.creator_.InsideBooks.FragmentsContentsBook.Books;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.creator_.Adapters.AdapterViewPager2;
import com.example.creator_.PlayActivities.ReaderActivity;
import com.example.creator_.Profile.ProfileInfActivity;
import com.example.creator_.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

public class BookToolsActivity extends AppCompatActivity {
    private static final String TAG = "BookActivity";

    private String idBook;
    private String userId;
    private boolean aBoolean= false;
    private AlienDescriptionFragment descriptionFragment = new AlienDescriptionFragment();
    private AlienChapterFragment chapterFragment = new AlienChapterFragment();

    private RadioButton subscribeBook;
    private boolean sub = false;
    private File bookDir;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;
    private ImageView imageView;
    private TabLayout tabLayout;
    private ViewPager2 pager;
    private TextView nameBook;
    private TextView nameWriter;
    private TextView collSub;
    private TextView bookDate;
    private MaterialButton read;
    private MaterialToolbar toolbar;

    //Read
    private LinearProgressIndicator progressIndicator;
    private TextView percent;
    private MaterialButton stopDownloadButton;
    private boolean stopDownload =false;
    private int progressDownloadFileBook;
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private final FirebaseFirestore db=FirebaseFirestore.getInstance();
    private final FirebaseStorage storage=FirebaseStorage.getInstance();
    private final StorageReference storageRef=storage.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_tools);
        Bundle arg=getIntent().getExtras();
        if (arg!=null) {
            idBook=arg.get("idBook").toString();
            userId = arg.get("userId").toString();
            aBoolean =true;
        }
        init();
        swipeRefreshLayout.setRefreshing(true);
        update();
        viewPager2();
        createDirectory(idBook);
        toolbar.setNavigationOnClickListener(v -> finish());
        read.setOnClickListener(v -> onRead(idBook));
        isSub();
        subscribeBooks();
        nameWriter.setOnClickListener(v -> {
            Intent intent = new Intent(BookToolsActivity.this, ProfileInfActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }


    private void isSub(){
        db.collection("UserProfile").document(user.getUid()).get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if (Objects.requireNonNull(snapshot).exists()){
                        if (Objects.requireNonNull(snapshot.getData()).get("subBook") != null){
                            ArrayList<String> subBook = (ArrayList<String>) snapshot.getData().get("subBook");
                            sub = Objects.requireNonNull(subBook).contains(idBook);
                        }else {
                            sub = false;
                        }
                        subscribeBook.setChecked(sub);
                    }
                }
            });

    }


    private void subscribeBooks(){
        subscribeBook.setOnClickListener(v -> {
            if (sub){
                sub = false;
                subscribeBook.setChecked(sub);
                db.collection("UserProfile").document(user.getUid()).update("subBook", FieldValue.arrayRemove(idBook))
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Good remove !!!"))
                        .addOnFailureListener(e -> Log.e(TAG, e.getMessage()));
                db.collection("Book").document(idBook).update("subId", FieldValue.arrayRemove(user.getUid()))
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Good remove !!!"))
                        .addOnFailureListener(e -> Log.e(TAG, e.getMessage()));
                db.collection("Book").document(idBook).update("subColl", FieldValue.increment(-1))
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Good remove !!!"))
                        .addOnFailureListener(e -> Log.e(TAG, e.getMessage()));
            }
            else {
                sub = true;
                subscribeBook.setChecked(sub);
                db.collection("UserProfile").document(user.getUid()).update("subBook", FieldValue.arrayUnion(idBook))
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Good Update !!!"))
                        .addOnFailureListener(e -> Log.e(TAG, e.getMessage()));
                db.collection("Book").document(idBook).update("subId", FieldValue.arrayUnion(user.getUid()))
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Good Update !!!"))
                        .addOnFailureListener(e -> Log.e(TAG, e.getMessage()));
                db.collection("Book").document(idBook).update("subColl", FieldValue.increment(1))
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Good Update !!!"))
                        .addOnFailureListener(e -> Log.e(TAG, e.getMessage()));
            }
        });
    }
    private void init(){
        read = findViewById(R.id.read);
        bookDate = findViewById(R.id.dateText);
        collSub = findViewById(R.id.CollSub);
        nameWriter = findViewById(R.id.WriterName);
        nameBook = findViewById(R.id.YourBookName);
        imageView = findViewById(R.id.ThisBookImage);
        tabLayout = findViewById(R.id.tab_layout_page);
        pager = findViewById(R.id.viewpager_owner);
        toolbar = findViewById(R.id.toolBarBook);
        view = findViewById(R.id.view);
        swipeRefreshLayout = findViewById(R.id.update);
        progressIndicator = findViewById(R.id.progress_download);
        percent = findViewById(R.id.percentView);
        stopDownloadButton = findViewById(R.id.stopDownload);
        subscribeBook = findViewById(R.id.subscribeBook);
    }

    private void update(){
        if (aBoolean){
            information(idBook);
            swipeRefreshLayout.setOnRefreshListener(() -> {
                Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
                information(idBook);
                descriptionFragment.dsrUpdate();
                if (chapterFragment.create){
                    chapterFragment.updateChapter();
                }
            });
            swipeRefreshLayout.setColorSchemeResources(R.color.ItemColor);
        }
    }
    private void createDirectory(String idBook){
        bookDir = new File(getExternalFilesDir(null)+"/Books/"+idBook);
        if (!bookDir.exists()){
            if (bookDir.mkdir()){
                Log.d(TAG,"Create OK");
            }
        }
    }


    @SuppressLint("NewApi")
    private void onRead(String idBook){
        db.collection("Book").document(idBook).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot snapshot = task.getResult();
                if (Objects.requireNonNull(snapshot).exists()){
                    int collChapter = Integer.parseInt(Objects.requireNonNull
                            (Objects.requireNonNull(snapshot.getData())
                                    .get("collChapter")).toString());
                    ArrayList<File> fileList= new ArrayList<>();
                    for (int i=1;i<=collChapter;i++){
                        File localFile = new File(bookDir+ "/" + "Глава"+ i + ".pdf");
                        if (!localFile.exists()){
                            fileList.add(localFile);

                            progressIndicator.setMin(0);
                            progressIndicator.setMax(100);
                            progressIndicator.setVisibility(View.VISIBLE);
                            stopDownloadButton.setVisibility(View.VISIBLE);
                            percent.setVisibility(View.VISIBLE);
                            read.setVisibility(View.INVISIBLE);
                            StorageReference  reference = storageRef
                                    .child(userId+ "/" + "Book/" + idBook + "/" + "Глава" + (i-1) + ".pdf");
                            reference.getFile(localFile).addOnProgressListener(snapshot1 -> {
                                int youProgress = (int) (100*snapshot1.getBytesTransferred()/snapshot1.getTotalByteCount());
                                if (progressDownloadFileBook==0){
                                    progressDownloadFileBook+=youProgress;
                                }else {
                                    progressDownloadFileBook += (youProgress-progressDownloadFileBook);
                                }
                                progressIndicator.incrementProgressBy(progressDownloadFileBook/fileList.size());
                                percent.setText(progressIndicator.getProgress()+"%");
                                Log.d(TAG,String.valueOf(youProgress));
                                stopDownloadButton.setOnClickListener(v -> {
                                    stopDownload =true;
                                    progressDownloadFileBook=0;
                                    progressIndicator.setProgress(0);
                                    progressIndicator.setVisibility(View.INVISIBLE);
                                    stopDownloadButton.setVisibility(View.INVISIBLE);
                                    percent.setVisibility(View.INVISIBLE);
                                    read.setVisibility(View.VISIBLE);
                                    snapshot1.getTask().cancel();
                                    for (File j:fileList){
                                        j.delete();
                                    }
                                });
                            }).addOnSuccessListener(taskSnapshot -> {
                                Log.d(TAG,"OkDownload");

                                if (stopDownload){
                                    taskSnapshot.getTask().cancel();
                                    for (File j:fileList){
                                        j.delete();
                                    }
                                }
                                if (progressIndicator.getProgress()==100){
                                    progressIndicator.setProgress(0);
                                    progressDownloadFileBook=0;
                                    progressIndicator.setVisibility(View.INVISIBLE);
                                    stopDownloadButton.setVisibility(View.INVISIBLE);
                                    percent.setVisibility(View.INVISIBLE);
                                    read.setVisibility(View.VISIBLE);
                                }
                            }).addOnFailureListener(e -> {});
                        }else {
                            if (i == collChapter){
                                Intent intent = new Intent(BookToolsActivity.this, ReaderActivity.class);
                                intent.putExtra("idBook",idBook);
                                startActivity(intent);
                            }
                        }
                    }

                }
            }
        });
    }


    private void information(String idBook){
        db.collection("Book").document(idBook).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot snapshot=task.getResult();
                if (Objects.requireNonNull(snapshot).exists()){
                    db.collection("UserProfile").document(userId).get().addOnCompleteListener(task1 -> {
                        if (task.isSuccessful()){
                            DocumentSnapshot snapshot1 = task1.getResult();
                            if (snapshot1.exists()){
                                String nameWriterString = snapshot1.getData().get("nickname").toString();
                                SpannableString writerName=new SpannableString(nameWriterString);
                                writerName.setSpan(new UnderlineSpan(),0,writerName.length(),0);
                                nameWriter.setText(writerName);
                            }
                        }
                    });

                    String subCollString="Почитатили: "+
                            Objects.requireNonNull(snapshot.getData()).get("subColl");
                    Timestamp timestamp= (Timestamp) snapshot.getData().get("dateBook");
                    @SuppressLint("SimpleDateFormat")
                    DateFormat df=new SimpleDateFormat("dd.MM.yyyy");
                    bookDate.setText(df.format
                            (Objects.requireNonNull(timestamp).toDate()));
                    nameBook.setText((String) Objects.requireNonNull
                            (snapshot.getData()).get("nameBook"));

                    collSub.setText(subCollString);
                    storageRef.child
                            (userId+ "/" + "Book/" + idBook + "/" + "coverArt" + ".jpg")
                            .getDownloadUrl().addOnSuccessListener(uri ->
                            Picasso.with(this).load(uri).into(imageView))
                            .addOnFailureListener(e -> {});
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    protected ArrayList<String> StringIdBookAndUserID(){
        if (aBoolean && idBook!= null&& userId!=null){
            ArrayList<String> ids = new ArrayList<>();
            ids.add(idBook);
            ids.add(userId);
            return ids;
        }
        return null;
    }

    private void viewPager2(){
        ArrayList<Fragment> fragments = new ArrayList<>();
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Подробно");
        arrayList.add("Главы");
        fragments.add(descriptionFragment);
        fragments.add(chapterFragment);
        FragmentStateAdapter adapter=new AdapterViewPager2(BookToolsActivity.this,fragments);
        pager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, pager, (tab, position) -> {
            if (position==0){
                tab.setText("Подробно");
            }else if (position==1){
                tab.setText("Главы");
            }
        }).attach();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {
            Log.i(TAG, "Refresh menu item selected");
            swipeRefreshLayout.setRefreshing(true);
            information(idBook);
            if (chapterFragment.create){
                chapterFragment.updateChapter();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}