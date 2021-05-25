package com.example.creator_.InsideBooks;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.creator_.InsideBooks.FragmentsContentsBook.AdapterInformation;
import com.example.creator_.InsideBooks.FragmentsContentsBook.ChapterFragment;
import com.example.creator_.InsideBooks.FragmentsContentsBook.DescriptionFragment;
import com.example.creator_.PlayClass.ReaderActivity;
import com.example.creator_.R;
import com.example.creator_.RecyclerChipsAndAdapter.AdapterRecyclerChips;
import com.example.creator_.RecyclerChipsAndAdapter.ChipRecycler;
import com.example.creator_.UserArchive.AddActivity.AddBookActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

import static com.example.creator_.R.menu.tool_book;


public class OwnerBookToolsActivity extends AppCompatActivity{
    private static final String TAG = "OwnerBookActivity";

    private String idBook;

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

    //Dialogs
    private MaterialButton close, complete;

    //EditDialog
    private TextInputLayout inputNameBook, inputDescriptionBook;

    //AddChapterDialog
    private MaterialButton addFile;
    private RecyclerView rv;
    private ArrayList<MediaFile> list= new ArrayList<>();

    private static final int  FILE_REQUEST_CODE=3;

    private final FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private final FirebaseUser user= mAuth.getCurrentUser();
    private final FirebaseFirestore db=FirebaseFirestore.getInstance();
    private final FirebaseStorage storage=FirebaseStorage.getInstance();
    private final StorageReference storageRef=storage.getReference();

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.owner_book_tools_activity);
        Bundle arg=getIntent().getExtras();
        if (arg!=null && user!=null) idBook=arg.get("idBook").toString();
        init();
        viewPager2();
        update();
        createDirectory(idBook);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId()==R.id.ToolMenuButton) showMenu();
            return false;
        });
        read.setOnClickListener(v -> onRead(idBook));
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
    }


    private void update(){
        if (user != null){
            information(idBook);
            swipeRefreshLayout.setOnRefreshListener(() -> {
                Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
                information(idBook);
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


    private void onRead(String idBook){
        db.collection("Book").document(idBook).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot snapshot = task.getResult();
                if (Objects.requireNonNull(snapshot).exists()){
                    int collChapter = Integer.parseInt(Objects.requireNonNull
                            (Objects.requireNonNull(snapshot.getData())
                                    .put("collChapter", 0)).toString());
                    for (int i=1;i<=collChapter;i++){
                        File localFile = new File(bookDir+ "/" + "Глава"+ i + ".pdf");
                        if (!localFile.exists()){
                            StorageReference  reference = storageRef
                                    .child(user.getUid()+ "/" + "Book/" + idBook + "/" + "Глава" + (i-1) + ".pdf");
                            reference.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                                Log.d(TAG,"OkDownload");
                            }).addOnFailureListener(e -> {
                            });
                        }else {
                            if (i == collChapter){
                                Intent intent = new Intent(OwnerBookToolsActivity.this, ReaderActivity.class);
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
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
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


    private void dialogEdit(Dialog dialog){
        inputNameBook = dialog.findViewById(R.id.NameBook);
        inputDescriptionBook = dialog.findViewById(R.id.description);
        close = dialog.findViewById(R.id.close);
        complete = dialog.findViewById(R.id.complete);
        close.setOnClickListener(v -> dialog.cancel());
        complete.setOnClickListener(v -> {
            if (!Objects.requireNonNull(inputNameBook.getEditText()).getText().toString().trim().isEmpty()){
                db.collection("Book").document(idBook).update("nameBook",
                        Objects.requireNonNull(inputNameBook.getEditText()).getText().toString())
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                        .addOnFailureListener(e ->Log.w(TAG, "Error updating document", e));
                swipeRefreshLayout.setRefreshing(true);
                information(idBook);
                dialog.cancel();
            }
            if (!Objects.requireNonNull(inputDescriptionBook.getEditText()).getText().toString().trim().isEmpty()){
                db.collection("Book").document(idBook).update("description",
                        Objects.requireNonNull(inputDescriptionBook.getEditText()).getText().toString())
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                        .addOnFailureListener(e ->Log.w(TAG, "Error updating document", e));
                swipeRefreshLayout.setRefreshing(true);
                information(idBook);
                dialog.cancel();
            }
            if (Objects.requireNonNull(inputDescriptionBook.getEditText()).getText().toString().trim().isEmpty()
            && Objects.requireNonNull(inputNameBook.getEditText()).getText().toString().trim().isEmpty())
                Toast.makeText(OwnerBookToolsActivity.this,"Нет изменений",Toast.LENGTH_LONG).show();
        });
    }


    private void dialogAdd(Dialog dialog){
        addFile = dialog.findViewById(R.id.addFile);
        rv = dialog.findViewById(R.id.RecyclerFiles);
        close = dialog.findViewById(R.id.close1);
        complete = dialog.findViewById(R.id.complete1);
        addFile.setOnClickListener(v -> {
            Intent intent = new Intent(OwnerBookToolsActivity.this, FilePickerActivity.class);
            intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                    .setCheckPermission(true)
                    .enableImageCapture(true)
                    .setMaxSelection(5)
                    .setShowImages(false)
                    .setShowVideos(false)
                    .setShowFiles(true)
                    .setSkipZeroSizeFiles(true)
                    .setSuffixes("pdf")
                    .build());
            startActivityForResult(intent, FILE_REQUEST_CODE);
        });
        close.setOnClickListener(v -> dialog.cancel());
        complete.setOnClickListener(v -> {

        });
    }


    @SuppressLint("RestrictedApi")
    private void showMenu(){
        Dialog dialogEdit = new Dialog(OwnerBookToolsActivity.this);
        dialogEdit.setContentView(R.layout.dialog_edit_book);


        Dialog dialogAddChapter = new Dialog(OwnerBookToolsActivity.this);
        dialogAddChapter.setContentView(R.layout.dialog_add_chapter);


        MenuBuilder menuBuilder =new MenuBuilder(this);
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(tool_book, menuBuilder);
        MenuPopupHelper optionsMenu = new MenuPopupHelper(this, menuBuilder, view);
        optionsMenu.setForceShowIcon(true);

        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem items) {
                switch (items.getItemId()){
                    case R.id.edit: {
                        dialogEdit(dialogEdit);
                        dialogEdit.show();
                    }
                    return true;
                    case R.id.add_chapter: {
                        dialogAdd(dialogAddChapter);
                        dialogAddChapter.show();
                    }
                        return true;
                    case R.id.delete: Toast.makeText(OwnerBookToolsActivity.this,"Диогоговое окно удаления книги", Toast.LENGTH_SHORT).show();
                        return true;
                    default: return false;
                }
            }
            @Override
            public void onMenuModeChange(@NonNull MenuBuilder menu) { }
        });

        optionsMenu.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==FILE_REQUEST_CODE && data!=null){
            ArrayList<ChipRecycler> chipRecyclerArrayList = new ArrayList<>();
            list = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
            rv.setLayoutManager(new LinearLayoutManager(OwnerBookToolsActivity.this));
            AdapterRecyclerChips.OnClickCloseChips clickCloseChips= (cp, Position) -> {
            };
            for (MediaFile i:list) {
                chipRecyclerArrayList.add(new ChipRecycler(i.getName(),i.getMimeType()));
                Log.w(TAG,i.getMimeType());
            }
            AdapterRecyclerChips adapterRecyclerChips=new AdapterRecyclerChips(chipRecyclerArrayList,OwnerBookToolsActivity.this,clickCloseChips);
            rv.setAdapter(adapterRecyclerChips);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {
            Log.i(TAG, "Refresh menu item selected");
            swipeRefreshLayout.setRefreshing(true);
            information(idBook);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}