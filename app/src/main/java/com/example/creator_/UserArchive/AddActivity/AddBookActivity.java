package com.example.creator_.UserArchive.AddActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;
import com.example.creator_.R;
import com.example.creator_.RecyclerChipsAndAdapter.AdapterRecyclerChips;
import com.example.creator_.RecyclerChipsAndAdapter.ChipRecycler;
import com.example.creator_.UserArchive.ArchivivesActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Random;
import UserFirestore.BookClass;
import static java.lang.System.currentTimeMillis;


public class AddBookActivity extends AppCompatActivity {
    private final static String  SAMPLE_CROPPED_IMG_NAME = "SampleCropImg";
    private final int GALLERY_REQUEST = 2;
    private final int  FILE_REQUEST_CODE=3;
    private int H;
    private ImageButton coverArt;
    private Uri uriImageBookLoad;
    private static long back_pressed;
    private TextInputLayout inputLayoutNameBookEdit,inputLayoutDescriptionBook;
    private ArrayList<MediaFile> list= new ArrayList<>();
    private final String TAG="AddBookActivity";
    private boolean checkErrorStart;
    private final FirebaseAuth mAuth=FirebaseAuth.getInstance();
    private final FirebaseUser user= mAuth.getCurrentUser();
    private final FirebaseStorage storage=FirebaseStorage.getInstance();
    private final FirebaseFirestore db=FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        H=0;
        checkErrorStart=false;
        coverArt=findViewById(R.id.bookImage);
        MaterialToolbar tabLayout = findViewById(R.id.check_bar);
        tabLayout.setNavigationOnClickListener(v -> {
            Intent intent=new Intent(AddBookActivity.this, ArchivivesActivity.class);
            startActivity(intent);
            finish();
        });
        tabLayout.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.trueButton) {
                ErrorStartCheck();
                if (uriImageBookLoad != null && !list.isEmpty() && checkErrorStart) {
                    new MaterialAlertDialogBuilder(AddBookActivity.this).setTitle("Публикация")
                            .setMessage("Опубликовать или оставить как черновик?")
                            .setNegativeButton(R.string.Draft, (dialog, which) -> {
                                AddBook(list, uriImageBookLoad,false);
                                dialog.cancel();
                            }).setPositiveButton(R.string.Post, (dialog, which) -> {
                                AddBook(list, uriImageBookLoad,true);
                                dialog.cancel();
                            }).show();


                } else if (uriImageBookLoad == null && list.isEmpty()) {
                    Toast.makeText(AddBookActivity.this, "Добавьте обложку для книги и файлы книги в хронологическом порядке", Toast.LENGTH_LONG).show();
                } else if (uriImageBookLoad == null) {
                    Toast.makeText(AddBookActivity.this, "Добавьте обложку для книги", Toast.LENGTH_LONG).show();
                } else if (list.isEmpty()) {
                    Toast.makeText(AddBookActivity.this, "Добавьте файлы книги в хронологическом порядке", Toast.LENGTH_LONG).show();
                }
            }
            return false;
        });
        MaterialButton addFile = findViewById(R.id.addButtonFileBook);
        inputLayoutNameBookEdit=findViewById(R.id.nameBookInput);
        inputLayoutDescriptionBook=findViewById(R.id.description);
        ErrorControl();
        addFile.setOnClickListener(v -> {
            Intent intent = new Intent(AddBookActivity.this, FilePickerActivity.class);
            intent.putExtra(FilePickerActivity.CONFIGS, new Configurations.Builder()
                    .setCheckPermission(true)
                    .enableImageCapture(true)
                    .setMaxSelection(3)
                    .setShowImages(false)
                    .setShowVideos(false)
                    .setShowFiles(true)
                    .setSkipZeroSizeFiles(true)
                    .setSuffixes("pdf")
                    .build());
            startActivityForResult(intent, FILE_REQUEST_CODE);
        });
        coverArt.setOnClickListener(v -> {
            Intent galUserImage=new Intent(Intent.ACTION_PICK);
            galUserImage.setType("image/*");
            startActivityForResult(galUserImage,GALLERY_REQUEST);
        });
    }


    private void AddBook(ArrayList<MediaFile> list,Uri uriBookImage,boolean privacy){
        String nameBook= Objects.requireNonNull(inputLayoutNameBookEdit.getEditText()).getText().toString();
        String description= Objects.requireNonNull(inputLayoutDescriptionBook.getEditText()).getText().toString();
        if (user!=null){
            DocumentReference docRef=db.collection("UserProfile").document(user.getUid());
            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot snapshot=task.getResult();
                    if (Objects.requireNonNull(snapshot).exists()){
                        ArrayList<String> listBook= (ArrayList<String>) Objects.requireNonNull(snapshot.getData()).put("listIdBook",null);
                        Timestamp timestamp= new Timestamp(Calendar.getInstance().getTime());
                        BookClass book=new BookClass(timestamp,description,nameBook,user.getUid(),privacy,0, new ArrayList<>(),list.size());
                        Log.d(TAG, "DocumentSnapshot data: " + snapshot.getData());
                        int xpUser=Integer.parseInt(Objects.requireNonNull(snapshot.getData().put("xpUser", 0)).toString());

                        db.collection("Book").add(book).addOnSuccessListener(documentReference -> {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                            StorageReference storageRef=storage.getReference();
                            if (listBook != null) {
                                listBook.add(documentReference.getId());
                                StorageReference ImageRef=storageRef.child(user.getUid()+ "/" + "Book/" + documentReference.getId()+ "/" + "coverArt" + ".jpg");
                                UploadTask uploadTaskImageBook=ImageRef.putFile(uriBookImage);
                                uploadTaskImageBook.addOnFailureListener(e -> {
                                }).addOnSuccessListener(taskSnapshot -> {
                                });
                                for (MediaFile i:list)
                                    if (i.getSize() != 0) {
                                        StorageReference FileRef = storageRef.child(user.getUid()+ "/" + "Book/" + documentReference.getId() + "/" + "Глава" + H + ".pdf");
                                        H += 1;
                                        Uri uri = i.getUri();
                                        UploadTask uploadTask = FileRef.putFile(uri);
                                        uploadTask.addOnFailureListener(e -> {
                                        }).addOnSuccessListener(taskSnapshot -> {
                                            Intent intent=new Intent(AddBookActivity.this, ArchivivesActivity.class);
                                            startActivity(intent);
                                            finish();
                                        });
                                    } else {
                                        Toast.makeText(AddBookActivity.this, "Файл " + i.getName() + ".pdf пустой, не будет сохранён", Toast.LENGTH_LONG).show();
                                    }

                                docRef.update("listIdBook",listBook).addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!")).addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
                            }

                        }).addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
                        docRef.update("xpUser",xpUser+15).addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!")).addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));

                    }else {
                        Log.d(TAG, "No such document");
                    }
                }else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            });
        }
    }


    @Override
    public void onBackPressed() {
        if (back_pressed+2000> currentTimeMillis()) {
            Intent intent=new Intent(AddBookActivity.this, ArchivivesActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(this,"Нажмите еще раз, чтобы выйти!",Toast.LENGTH_SHORT).show();
        }
        back_pressed= currentTimeMillis();
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


    private UCrop.Options getUCropOptions(){
        UCrop.Options options=new UCrop.Options();
        //options.setStatusBarColor(bar);
        options.withMaxResultSize(465,540);
        //options.setToolbarColor(bar);
        return options;
    }


    private void LoadCoverArtBook(Uri uri){
        Picasso.with(this).load(uri).into(coverArt);
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
        }else if (requestCode==UCrop.REQUEST_CROP){
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
            RecyclerView listFile = findViewById(R.id.RecyclerListFile);
            listFile.setLayoutManager(new LinearLayoutManager(AddBookActivity.this));

            AdapterRecyclerChips.OnClickCloseChips clickCloseChips= (cp, Position) -> {
            };
            for (MediaFile i:list) {
                chipRecyclerArrayList.add(new ChipRecycler(i.getName(),i.getMimeType()));
                Log.w(TAG,i.getMimeType());
            }
            AdapterRecyclerChips adapterRecyclerChips=new AdapterRecyclerChips(chipRecyclerArrayList,AddBookActivity.this,clickCloseChips);
            listFile.setAdapter(adapterRecyclerChips);


        }
    }


    private void ErrorStartCheck(){
        if (Objects.requireNonNull(inputLayoutNameBookEdit.getEditText()).getText().toString().trim().isEmpty()) inputLayoutNameBookEdit.setError("Это поле надо заполнить");
        else inputLayoutNameBookEdit.setError(null);
        if (Objects.requireNonNull(inputLayoutDescriptionBook.getEditText()).getText().toString().trim().isEmpty()) inputLayoutDescriptionBook.setError("Это поле надо заполнить");
        else inputLayoutDescriptionBook.setError(null);
        checkErrorStart = (!inputLayoutDescriptionBook.getEditText().getText().toString().trim().isEmpty()
                && !inputLayoutNameBookEdit.getEditText().getText().toString().trim().isEmpty());
    }


    private void ErrorControl(){
        Objects.requireNonNull(inputLayoutNameBookEdit.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                inputLayoutNameBookEdit.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (inputLayoutNameBookEdit.getEditText().getText().toString().trim().isEmpty()){
                    inputLayoutNameBookEdit.setError("Это поле надо заполнить");

                }

            }
        });
        Objects.requireNonNull(inputLayoutDescriptionBook.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                inputLayoutDescriptionBook.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (inputLayoutDescriptionBook.getEditText().getText().toString().trim().isEmpty()){
                    inputLayoutDescriptionBook.setError("Это поле надо заполнить");

                }

            }
        });
    }
}
