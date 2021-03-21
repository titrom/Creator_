package com.example.creator_;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import java.io.FileOutputStream;

public class GlobalActivity extends AppCompatActivity {
    private BottomNavigationView barNavigation;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global);
//        Bundle argument=getIntent().getExtras();
//        nick=argument.get("nicknameLog").toString();
        FragmentBar fragmentBar=new FragmentBar();
        ProfileFragment profileFragment=new ProfileFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.conteinerFragment,profileFragment).hide(profileFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.conteinerFragment,fragmentBar).commit();
        barNavigation=findViewById(R.id.bottomNavigationBar);
        barNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.topHot: getSupportFragmentManager().beginTransaction().hide(profileFragment).show(fragmentBar).commit(); break;
                    case R.id.favorite: getSupportFragmentManager().beginTransaction().hide(fragmentBar).hide(profileFragment).commit();break;
                    case R.id.home:getSupportFragmentManager().beginTransaction().hide(fragmentBar).show(profileFragment).commit();break;
                    default: return false;
                }
                return false;
            }
        });
    }
}
