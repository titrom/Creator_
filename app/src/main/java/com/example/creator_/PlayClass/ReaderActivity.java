package com.example.creator_.PlayClass;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.InputDevice;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.creator_.InsideBooks.FragmentsContentsBook.RecyclerChapter.AdapterChapter;
import com.example.creator_.InsideBooks.FragmentsContentsBook.RecyclerChapter.ChapterClass;
import com.example.creator_.R;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnDrawListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageScrollListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.listener.OnTapListener;
import com.github.barteksc.pdfviewer.util.FitPolicy;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


import java.io.File;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.lang.System.currentTimeMillis;


public class ReaderActivity extends AppCompatActivity {
    private final static String TAG = "ReaderActivity";
    private PDFView pdfView;
    private Integer savePage;
    private final ArrayList<ChapterClass> ccList = new ArrayList<>();;
    private MaterialToolbar readerBar;
    private String idBook;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseUser user = mAuth.getCurrentUser();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    boolean touch = false;
    private static long back_pressed;

    //dialog Chapters
    private RecyclerView rv;
    private MaterialButton close;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        pdfView = findViewById(R.id.pdf_view);
        readerBar = findViewById(R.id.reader_bar);
        readerBar.setVisibility(View.INVISIBLE);

        Bundle arg = getIntent().getExtras();
        if (arg != null && user != null) {
            idBook = arg.get("idBook").toString();
            readerBar.setNavigationOnClickListener(v -> {
                finish();
            });
            Dialog dialog = new Dialog(ReaderActivity.this);
            dialogChapters(dialog);
            readerBar.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()){
                    case R.id.chapters: {
                        dialog.show();
                        return true;
                    }
                    default: return false;
                }
            });
            loadRead(idBook);
        }
    }
    private void dialogChapters(Dialog dialog){
        dialog.setContentView(R.layout.dialog_chapters);
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rv = dialog.findViewById(R.id.dialog_chapters_rv);
        close = dialog.findViewById(R.id.close_rv_chapter);
        close.setOnClickListener(v -> {
            dialog.cancel();
        });
        if (ccList.size()!= 0){
            rv.setAdapter(null);
            ccList.clear();
        }
         File bookDir = new File(getExternalFilesDir(null)+"/Books/"+idBook);
        if (!bookDir.exists()){
            if (bookDir.mkdir()){
                Log.d(TAG,"Good!!");
            }
        }else if (user!=null){
            db.collection("Book").document(idBook).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if (Objects.requireNonNull(snapshot).exists()){
                        int collChapter = Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(snapshot.getData()).get("collChapter")).toString());
                        for (int i=1;i<=collChapter;i++){
                            File localFile = new File(bookDir+ "/" + "Глава"+ i + ".pdf");
                            if (!localFile.exists()){
                                ccList.add(new ChapterClass("Глава"+i,false));
                            }else {
                                ccList.add(new ChapterClass("Глава"+i,true));
                            }
                            AdapterChapter.OnClickChapter oCC= (cc, position) -> {
                                db.collection("UserProfile").document(user.getUid())
                                        .update(idBook+".chapter",position+1)
                                        .addOnSuccessListener(aVoid -> Log.d(TAG,"Good update !!! "))
                                        .addOnFailureListener(e -> Log.e(TAG,e.getMessage()));
                                db.collection("UserProfile").document(user.getUid())
                                        .update(idBook+".page",0)
                                        .addOnSuccessListener(aVoid -> Log.d(TAG,"Good update !!! "))
                                        .addOnFailureListener(e -> Log.e(TAG,e.getMessage()));
                                pdfView.fromFile(null).load();
                                loadRead(idBook);
                                dialog.cancel();
                            };
                            rv.setLayoutManager(new LinearLayoutManager(ReaderActivity.this));
                            AdapterChapter adapter = new AdapterChapter(ccList,oCC,ReaderActivity.this);
                            rv.setAdapter(adapter);
                        }
                    }
                }
            });
        }


    }
    @Override
    protected void onStop() {
        super.onStop();
        if (savePage!=null){
            db.collection("UserProfile").document(user.getUid()).update(idBook+".page",savePage)
                    .addOnSuccessListener(aVoid -> Log.d(TAG,"Good update !!! "))
                    .addOnFailureListener(e -> Log.e(TAG,e.getMessage()));
            finish();
        }
    }
    @Override
    public void onBackPressed() {
        if (back_pressed+2000> currentTimeMillis()) {
            finish();
        }
        else {
            Toast.makeText(this,"Нажмите еще раз, чтобы выйти!",Toast.LENGTH_SHORT).show();
        }
        back_pressed= currentTimeMillis();
    }
    private void loadRead(String idBook){
        db.collection("UserProfile").document(user.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                DocumentSnapshot snapshot = task.getResult();
                if (Objects.requireNonNull(snapshot).exists()){
                    if (Objects.requireNonNull(snapshot.getData()).get(idBook)!= null){
                        HashMap<String, Long> chapterAndPage = (HashMap<String, Long>) snapshot.getData().get(idBook);
                        int page = ((Long) Objects.requireNonNull(Objects.requireNonNull(chapterAndPage).get("page"))).intValue();
                        int chapter = ((Long) Objects.requireNonNull(chapterAndPage.get("chapter"))).intValue();
                        readerBar.setTitle("Глава " + chapter);
                        File file = new File(getExternalFilesDir(null)+"/Books/"+idBook+"/Глава"+chapter+".pdf");
                        pdfView.fromFile(file).defaultPage(page).swipeHorizontal(true).pageSnap(true).autoSpacing(true)
                                .pageFling(true).pageFitPolicy(FitPolicy.WIDTH).onTap(e -> {
                                    if (touch){
                                        readerBar.setVisibility(View.INVISIBLE);
                                        touch =false;
                                    }
                                    else {
                                        readerBar.setVisibility(View.VISIBLE);
                                        touch = true;
                                    }
                                    return false;
                                })
                                .onPageChange((page1, pageCount) -> savePage = page1)
                                .load();
                    }else {
                        Log.d(TAG,"Good!!");
                        HashMap<String,Integer> pageAndChapter = new HashMap<>();
                        pageAndChapter.put("chapter",1);
                        pageAndChapter.put("page",0);
                        db.collection("UserProfile").document(user.getUid()).update(idBook,pageAndChapter)
                                .addOnSuccessListener(aVoid -> Log.d(TAG,"Good update !!!"))
                                .addOnFailureListener(e -> Log.e(TAG,e.getMessage()));
                    }

                }
            }
        });
    }
}
