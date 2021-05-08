package com.example.creator_.InsideBooks;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.creator_.InsideBooks.FragmentsContentsBook.ChapterFragment;
import com.example.creator_.InsideBooks.FragmentsContentsBook.DescriptionFragment;
import com.example.creator_.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
public class OwnerBookToolsActivity extends AppCompatActivity{
    private ImageView imageView;
    private TabLayout tabLayout;
    private ViewPager pager;
    private TextView nameBook;
    private TextView nameWriter;
    private TextView collSub;
    private TextView bookDate;

    private final FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private final FirebaseUser user= mAuth.getCurrentUser();
    private final FirebaseStorage storage=FirebaseStorage.getInstance();
    private final StorageReference storageRef=storage.getReference();
    private final FirebaseFirestore db=FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_book_tools);
        init();
        viewPager();
        Bundle arg=getIntent().getExtras();
        if (arg!=null && user!=null){
            String idBook=arg.get("idBook").toString();
            db.collection("Book").document(idBook).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot snapshot=task.getResult();
                    if (Objects.requireNonNull(snapshot).exists()){
                        SpannableString writerName=new SpannableString(user.getDisplayName());
                        writerName.setSpan(new UnderlineSpan(),0,writerName.length(),0);
                        String subCollString="Почитатили: "+ Objects.requireNonNull(snapshot.getData()).put("subColl",0);
                        Timestamp timestamp= (Timestamp) snapshot.getData().put("dateBook",0);
                        @SuppressLint("SimpleDateFormat")
                        DateFormat df=new SimpleDateFormat("dd.MM.yyyy");
                        bookDate.setText(df.format(Objects.requireNonNull(timestamp).toDate()));
                        nameBook.setText((String) Objects.requireNonNull(snapshot.getData()).put("nameBook",""));
                        nameWriter.setText(writerName);
                        collSub.setText(subCollString);
                        storageRef.child("BookImage/"+idBook+".jpg").getDownloadUrl()
                                .addOnSuccessListener(uri ->
                                        Picasso.with(this).load(uri).into(imageView))
                                .addOnFailureListener(e -> {});
                    }
                }
            });

        }
    }
    private void init(){
        bookDate=findViewById(R.id.dateText);
        collSub=findViewById(R.id.CollSub);
        nameWriter=findViewById(R.id.WriterName);
        nameBook=findViewById(R.id.YourBookName);
        imageView=findViewById(R.id.ThisBookImage);
        tabLayout=findViewById(R.id.tab_layout_page);
        pager=findViewById(R.id.viewpager_owner);
    }
    private static class ContentsFragmentAdapter extends FragmentPagerAdapter{
        private final List<Fragment> fragmentList=new ArrayList<>();
        private final List<String> titleList=new ArrayList<>();
        public void addFragment(Fragment fragment,String title){
            fragmentList.add(fragment);
            titleList.add(title);
        }
        public ContentsFragmentAdapter(@NonNull FragmentManager fm,int behavior) {
            super(fm,behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titleList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }
    private void viewPager(){
        ContentsFragmentAdapter contentsFragmentAdapter;
        contentsFragmentAdapter = new ContentsFragmentAdapter(getSupportFragmentManager(),1);
        contentsFragmentAdapter.addFragment(new DescriptionFragment(),"Подробно");
        contentsFragmentAdapter.addFragment(new ChapterFragment(),"Главы");
        pager.setAdapter(contentsFragmentAdapter);
        tabLayout.setupWithViewPager(pager);
    }
}