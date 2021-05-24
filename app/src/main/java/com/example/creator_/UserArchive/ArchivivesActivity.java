package com.example.creator_.UserArchive;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.creator_.UserArchive.FragmentArchive.MyLibraryFragment;
import com.example.creator_.R;

import java.io.File;

public class ArchivivesActivity extends AppCompatActivity {
    private File booksDirt;
    private static final String TAG = "ArchivivesActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archivives);
        booksDirt = new File(getExternalFilesDir(null)+"/Books");
        if (!booksDirt.exists()){
            if (booksDirt.mkdir()){
                Log.d(TAG,"Create OK");
            }
        }
        MyLibraryFragment myLibraryFragment=new MyLibraryFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.ArchiveFragment,myLibraryFragment).commit();


    }
}