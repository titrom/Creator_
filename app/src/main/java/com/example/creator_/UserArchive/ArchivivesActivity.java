package com.example.creator_.UserArchive;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.creator_.UserArchive.FragmentArchive.MyLibraryFragment;
import com.example.creator_.R;

public class ArchivivesActivity extends AppCompatActivity {

//    private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archivives);
//        tabLayout= findViewById(R.id.Archives);
        MyLibraryFragment myLibraryFragment=new MyLibraryFragment();
//        MyMusicFragment myMusicFragment=new MyMusicFragment();
//        MyArtFragment myArtFragment=new MyArtFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.ArchiveFragment,myLibraryFragment).commit();
//        getSupportFragmentManager().beginTransaction().add(R.id.ArchiveFragment,myMusicFragment).hide(myMusicFragment).commit();
//        getSupportFragmentManager().beginTransaction().add(R.id.ArchiveFragment,myArtFragment).hide(myArtFragment).commit();

//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                switch (tab.getPosition()){
//                    case 0: getSupportFragmentManager().beginTransaction().hide(myArtFragment).hide(myMusicFragment).show(myLibraryFragment).commit();break;
//                    case 1: getSupportFragmentManager().beginTransaction().hide(myArtFragment).hide(myLibraryFragment).show(myMusicFragment).commit();break;
//                    case 2: getSupportFragmentManager().beginTransaction().hide(myLibraryFragment).hide(myMusicFragment).show(myArtFragment).commit();break;
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });

    }
}