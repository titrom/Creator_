package com.example.creator_.InsideBooks.FragmentsContentsBook;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import android.annotation.SuppressLint;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.creator_.PlayClass.ReaderActivity;
import com.example.creator_.R;
import com.example.creator_.RecyclerChipsAndAdapter.AdapterRecyclerChips;
import com.example.creator_.RecyclerChipsAndAdapter.ChipRecycler;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
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
import com.google.firebase.storage.UploadTask;
import com.jaiselrahman.filepicker.activity.FilePickerActivity;
import com.jaiselrahman.filepicker.config.Configurations;
import com.jaiselrahman.filepicker.model.MediaFile;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Random;

import static com.example.creator_.R.menu.tool_book;


public class OwnerBookToolsActivity extends AppCompatActivity{
    private static final String TAG = "OwnerBookActivity";

    private String idBook;
    private boolean aBoolean= false;
    private DescriptionFragment descriptionFragment = new DescriptionFragment();
    private ChapterFragment chapterFragment = new ChapterFragment();

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
    //Dialogs
    private MaterialButton close, complete;
    private boolean privacy;
    //EditDialog
    private final static String  SAMPLE_CROPPED_IMG_NAME = "SampleCropImg";
    private static final int GALLERY_REQUEST = 2;
    ArrayList<MediaFile> list = new ArrayList<>();
    private Uri uriImageBookLoad;
    private TextInputLayout inputNameBook, inputDescriptionBook;
    private ImageButton imageButton;
    //AddChapterDialog
    private static final int  FILE_REQUEST_CODE=3;
    private MaterialButton addFile;
    private RecyclerView rv;
    private int H;
    private ProgressDialog pd;
    private int collChapter;
    private int progress;
    private boolean service =false;
    private boolean stop = false;

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
        if (arg!=null && user!=null) {
            idBook=arg.get("idBook").toString();
            aBoolean =true;
        }
        init();
        swipeRefreshLayout.setRefreshing(true);
        update();
        viewPager2();
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
        progressIndicator = findViewById(R.id.progress_download);
        percent = findViewById(R.id.percentView);
        stopDownloadButton = findViewById(R.id.stopDownload);
    }

    private void update(){
        if (user != null && aBoolean){
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
                                    .child(user.getUid()+ "/" + "Book/" + idBook + "/" + "Глава" + (i-1) + ".pdf");
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
                            Objects.requireNonNull(snapshot.getData()).get("subColl");
                    Timestamp timestamp= (Timestamp) snapshot.getData().get("dateBook");
                    @SuppressLint("SimpleDateFormat")
                    DateFormat df=new SimpleDateFormat("dd.MM.yyyy");
                    bookDate.setText(df.format
                            (Objects.requireNonNull(timestamp).toDate()));
                    nameBook.setText((String) Objects.requireNonNull
                            (snapshot.getData()).get("nameBook"));
                    nameWriter.setText(writerName);
                    collSub.setText(subCollString);
                    storageRef.child
                            (user.getUid()+ "/" + "Book/" + idBook + "/" + "coverArt" + ".jpg")
                            .getDownloadUrl().addOnSuccessListener(uri ->
                            Picasso.with(this).load(uri).into(imageView))
                            .addOnFailureListener(e -> {});
                    privacy = (boolean) snapshot.getData().put("privacyLevel",false);
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
        fragments.add(descriptionFragment);
        fragments.add(chapterFragment);
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
        dialog.setContentView(R.layout.dialog_edit_book);
        inputNameBook = dialog.findViewById(R.id.NameBook);
        inputDescriptionBook = dialog.findViewById(R.id.description);
        close = dialog.findViewById(R.id.close);
        complete = dialog.findViewById(R.id.complete);
        imageButton = dialog.findViewById(R.id.updateBookImage);
        imageButton.setOnClickListener(v -> {
            Intent galUserImage=new Intent(Intent.ACTION_PICK);
            galUserImage.setType("image/*");
            startActivityForResult(galUserImage,GALLERY_REQUEST);
        });
        db.collection("Book").document(idBook).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot snapshot = task.getResult();
                if (Objects.requireNonNull(snapshot).exists()){
                    Objects.requireNonNull(inputNameBook.getEditText())
                            .setText(String.valueOf(Objects.requireNonNull(snapshot.getData()).put("nameBook","")));
                    Objects.requireNonNull(inputDescriptionBook.getEditText())
                            .setText(String.valueOf(snapshot.getData().put("description","")));
                }
            }
        });
        close.setOnClickListener(v -> dialog.cancel());
        complete.setOnClickListener(v -> {
            if (uriImageBookLoad!=null){
                StorageReference imageRef =storageRef.child(user.getUid()+ "/" + "Book/" + idBook+ "/" + "coverArt" + ".jpg");
                UploadTask uploadTaskImageBook= imageRef.putFile(uriImageBookLoad);
                uploadTaskImageBook.addOnFailureListener(e -> {
                }).addOnSuccessListener(taskSnapshot -> {
                });
            }
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
        dialog.setContentView(R.layout.dialog_add_chapter);
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
            dialog.cancel();
            db.collection("Book").document(idBook).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if (Objects.requireNonNull(snapshot).exists()){
                        collChapter = Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(snapshot.getData()).put("collChapter", 0)).toString());
                        H = collChapter;
                        ArrayList<UploadTask> arrayList = new ArrayList<>();
                        for (MediaFile i:list){
                            if (i.getSize() != 0) {
                                StorageReference fileRef = storageRef.child(user.getUid()+ "/" + "Book/" + idBook + "/" + "Глава" + H + ".pdf");
                                Uri uri = i.getUri();
                                UploadTask uploadTask = fileRef.putFile(uri);
                                arrayList.add(uploadTask);
                                H++;
                            } else Toast.makeText(OwnerBookToolsActivity.this, "Файл " + i.getName() + ".pdf пустой, не будет сохранён", Toast.LENGTH_LONG).show();
                        }
                        ProgressDialog cancelDialog=new ProgressDialog(OwnerBookToolsActivity.this);
                        cancelDialog.setCancelable(false);
                        cancelDialog.setCanceledOnTouchOutside(false);
                        cancelDialog.setTitle("Отмена");
                        cancelDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        pd = new ProgressDialog(OwnerBookToolsActivity.this);
                        pd.setTitle("Добавление глав");
                        pd.setMax(100);
                        pd.setCanceledOnTouchOutside(false);
                        pd.setCancelable(false);
                        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        for (UploadTask i: arrayList){
                            i.addOnProgressListener(snapshot1 -> {
                                int progressYou = (int) (100*snapshot1.getBytesTransferred()/snapshot1.getTotalByteCount());
                                if (progress==0){
                                    progress+=progressYou;
                                }else {
                                    progress += (progressYou-progress);
                                }
                                pd.setButton(Dialog.BUTTON_POSITIVE,"Свернуть",(dialog1, which) -> {
                                    service=true;
                                    pd.cancel();
                                    Toast.makeText(OwnerBookToolsActivity.this,"Дождитесь оповищения окончания загрузки. Не закрывайте приложение",Toast.LENGTH_LONG).show();

                                });
                                pd.setButton(Dialog.BUTTON_NEGATIVE, "Отмена", (dialog1, which) -> {
                                    cancelDialog.show();
                                    snapshot1.getTask().cancel();
                                    stop=true;
                                    StorageReference storageRef1=storage.getReference();
                                    db.collection("Book").document(idBook).update("collChapter",collChapter)
                                            .addOnSuccessListener(aVoid ->Log.d(TAG, "DocumentSnapshot successfully updated!"))
                                            .addOnFailureListener(e ->Log.w(TAG, "Error updating document", e));
                                    for (int j = collChapter;j<collChapter+arrayList.size();j++){
                                        StorageReference fileRef = storageRef1.child(user.getUid()+ "/" + "Book/" + idBook+"/"+ "Глава" + j + ".pdf");
                                        fileRef.delete().addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully deleted!"))
                                                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
                                    }
                                    pd.cancel();
                                    Toast.makeText(OwnerBookToolsActivity.this,"Загрузка прервона",Toast.LENGTH_LONG).show();
                                });
                                pd.setMessage("Дождитесь окончания загрузки");
                                pd.incrementProgressBy(progress/arrayList.size());

                                if (progress==0&&idBook!=null&&!service&&!stop) pd.show();


                            }).addOnSuccessListener(taskSnapshot -> {
                                Log.d(TAG,"Ok");
                                if (pd.getProgress()==100&&!stop) {
                                    pd.cancel();
                                    Toast.makeText(OwnerBookToolsActivity.this,"Загрузка завершина.",Toast.LENGTH_LONG).show();
                                }
                                if (stop){
                                    taskSnapshot.getTask().cancel();
                                    StorageReference storageRef1=storage.getReference();
                                    for (int j = collChapter;j<collChapter+arrayList.size();j++){
                                        StorageReference fileRef = storageRef1.child(user.getUid()+ "/" + "Book/" + idBook+"/"+ "Глава" + j + ".pdf");
                                        fileRef.delete().addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully deleted!"))
                                                .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
                                    }cancelDialog.cancel();
                                }
                            }).addOnFailureListener(e -> {
                                Toast.makeText(OwnerBookToolsActivity.this,"Не удалось загрузить файл",Toast.LENGTH_LONG).show();

                            });
                        }

                        db.collection("Book").document(idBook).update("collChapter",collChapter+arrayList.size())
                                .addOnSuccessListener(aVoid ->Log.d(TAG, "DocumentSnapshot successfully updated!"))
                                .addOnFailureListener(e ->Log.w(TAG, "Error updating document", e));
                    }
                }
            });

        });
    }


    @SuppressLint({"InflateParams", "RestrictedApi"})
    private void showMenu(){
        Dialog dialog = new Dialog(OwnerBookToolsActivity.this);
        MenuBuilder menuBuilder =new MenuBuilder(this);
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(tool_book, menuBuilder);
        if (!privacy){
            menuBuilder.add(0,3,0,R.string.post).setIcon(R.drawable.post);
        }
        MenuPopupHelper optionsMenu = new MenuPopupHelper(this, menuBuilder, view);
        optionsMenu.setForceShowIcon(true);


        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem items) {
                switch (items.getItemId()){
                    case R.id.edit: {
                        dialogEdit(dialog);
                        dialog.show();
                    }return true;
                    case R.id.add_chapter: {
                        dialogAdd(dialog);
                        dialog.show();
                    }return true;
                    case 3:{
                        new MaterialAlertDialogBuilder(OwnerBookToolsActivity.this).setTitle("Опубликовать").setMessage("Хотите опубликовать книгу")
                                .setNegativeButton(R.string.no, (dialog1, which) -> {
                                    dialog1.cancel();
                                }).setPositiveButton(R.string.yes, (dialog1, which) -> {
                                    db.collection("Book").document(idBook).update("privacyLevel",true)
                                            .addOnSuccessListener(aVoid -> Log.d(TAG,"Good Update !!!"))
                                            .addOnFailureListener(e -> Log.e(TAG,e.getMessage()));
                                    information(idBook);
                                }).show();
                    }
//                    case R.id.delete: {
//                        new MaterialAlertDialogBuilder(OwnerBookToolsActivity.this).setTitle("Удалть книгу")
//                                .setMessage("Вы действительно хотите удалить книгу?")
//                                .setPositiveButton(R.string.Yes,(dialog, which) -> {
//                                    dialog.cancel();
//                                    Intent intent = new Intent(OwnerBookToolsActivity.this, ArchivivesActivity.class);
//                                    intent.putExtra("deleteIdBook", idBook);
//                                    startActivity(intent);
//                                    finish();
//                                }).setNegativeButton(R.string.No,(dialog, which) -> dialog.cancel()).show();
//                    }return true;
                    default: return false;
                }
            }
            @Override
            public void onMenuModeChange(@NonNull MenuBuilder menu) { }
        });

        optionsMenu.show();
    }


    private void LoadCoverArtBook(Uri uri){
        Picasso.with(this).load(uri).into(imageButton);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==GALLERY_REQUEST){
            Uri uriImageBook;
            if (data != null) {
                uriImageBook = data.getData();
                if (uriImageBook!=null){
                    Start_uCrop(uriImageBook);
                }
            }
        }else if (requestCode== UCrop.REQUEST_CROP){
            if (data!=null){
                Uri uriUCrop=UCrop.getOutput(data);
                uriImageBookLoad=UCrop.getOutput(data);
                if (uriUCrop!=null){
                    LoadCoverArtBook(uriUCrop);
                }
            }
        }
        if (requestCode==FILE_REQUEST_CODE && data!=null){
            ArrayList<ChipRecycler> chipRecyclerArrayList = new ArrayList<>();
            list = data.getParcelableArrayListExtra(FilePickerActivity.MEDIA_FILES);
            rv.setLayoutManager(new LinearLayoutManager(OwnerBookToolsActivity.this));
            AdapterRecyclerChips.OnClickCloseChips clickCloseChips= (cp, Position) -> {
            };
            for (MediaFile i: list) {
                chipRecyclerArrayList.add(new ChipRecycler(i.getName(),i.getMimeType()));
                Log.w(TAG,i.getMimeType());
            }
            AdapterRecyclerChips adapterRecyclerChips=new AdapterRecyclerChips(chipRecyclerArrayList,OwnerBookToolsActivity.this,clickCloseChips);
            rv.setAdapter(adapterRecyclerChips);
        }
    }


    private void Start_uCrop(Uri uri){
        Random random=new Random();

        String destinationFileName = SAMPLE_CROPPED_IMG_NAME+random.nextDouble();
        destinationFileName +=".jpg";
        LinkedList<String> b= new LinkedList<>();
        b.add(destinationFileName);
        Log.d(TAG,destinationFileName);
        Log.d(TAG,uri.toString());

        UCrop uCrop=UCrop.of(uri,Uri.fromFile(new File(this.getCacheDir(),b.getLast())));
        uCrop.withAspectRatio(7f,9f);
        uCrop.withOptions(getUCropOptions());
        uCrop.start(this);


    }
    protected String StringIdBook(){
        if (aBoolean && idBook!= null){
            return idBook;
        }
        return null;
    }


    private UCrop.Options getUCropOptions(){
        UCrop.Options options=new UCrop.Options();
        //options.setStatusBarColor(bar);
        options.withMaxResultSize(465,540);
        //options.setToolbarColor(bar);
        return options;
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