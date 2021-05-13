package com.example.creator_.InsideBooks;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.creator_.InsideBooks.FragmentsContentsBook.AdapterInformation;
import com.example.creator_.InsideBooks.FragmentsContentsBook.ChapterFragment;
import com.example.creator_.InsideBooks.FragmentsContentsBook.DescriptionFragment;
import com.example.creator_.InsideBooks.FragmentsContentsBook.ReaderActivity;
import com.example.creator_.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
public class OwnerBookToolsActivity extends AppCompatActivity{
    private ImageView imageView;
    private TabLayout tabLayout;
    private ViewPager2 pager;
    private TextView nameBook;
    private TextView nameWriter;
    private TextView collSub;
    private TextView bookDate;
    private MaterialButton read;

    private final FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private final FirebaseUser user= mAuth.getCurrentUser();
    private final FirebaseStorage storage=FirebaseStorage.getInstance();
    private final StorageReference storageRef=storage.getReference();
    private final FirebaseFirestore db=FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_book_tools_activity);
        init();
        viewPager2();
        Bundle arg=getIntent().getExtras();
        if (arg!=null && user!=null){
            String idBook=arg.get("idBook").toString();
            information(idBook);
            read.setOnClickListener(v -> {
                try {
                    StorageReference  reference = storageRef
                            .child(user.getUid()+ "/" + "Book/" + idBook + "/" + "Глава0" + ".pdf");
                    File localFile = File.createTempFile("Глава0", "pdf");
                    Intent intent = new Intent(OwnerBookToolsActivity.this, ReaderActivity.class);
                    intent.putExtra("idBook",idBook);
                    startActivity(intent);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });



        }
    }
    private void information(String idBook){
        db.collection("Book").document(idBook).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot snapshot=task.getResult();
                if (Objects.requireNonNull(snapshot).exists()){
                    SpannableString writerName=new SpannableString(user.getDisplayName());
                    writerName.setSpan(new UnderlineSpan(),0,writerName.length(),0);
                    String subCollString="Почитатили: "+
                            Objects.requireNonNull(snapshot.getData()).put("subColl",0);
                    Timestamp timestamp= (Timestamp) snapshot.getData().put("dateBook",0);
                    @SuppressLint("SimpleDateFormat")
                    DateFormat df=new SimpleDateFormat("dd.MM.yyyy");
                    bookDate.setText(df.format
                            (Objects.requireNonNull(timestamp).toDate()));
                    nameBook.setText((String) Objects.requireNonNull
                            (snapshot.getData()).put("nameBook",""));
                    nameWriter.setText(writerName);
                    collSub.setText(subCollString);
                    storageRef.child
                            (user.getUid()+ "/" + "Book/" + idBook + "/" + "coverArt" + ".jpg")
                            .getDownloadUrl().addOnSuccessListener(uri ->
                            Picasso.with(this).load(uri).into(imageView))
                            .addOnFailureListener(e -> {});
                }
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
    }
    private void viewPager2(){
        ArrayList<Fragment> fragments = new ArrayList<>();
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Подробно");
        arrayList.add("Главы");
        fragments.add(new DescriptionFragment());
        fragments.add(new ChapterFragment());
        FragmentStateAdapter adapter=new AdapterInformation(OwnerBookToolsActivity.this,fragments);
        pager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, pager, (tab, position) -> {
            if (position==0){
                tab.setText("Подробно");
            }else if (position==1){
                tab.setText("Главы");
            }
        }).attach();
    }
}