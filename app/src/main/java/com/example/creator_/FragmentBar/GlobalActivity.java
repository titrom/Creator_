package com.example.creator_.FragmentBar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.creator_.FragmentBar.Favorite.FavoriteFragment;
import com.example.creator_.FragmentBar.Top.TopFragment;
import com.example.creator_.FragmentBar.Profile.ProfileFragment;
import com.example.creator_.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GlobalActivity extends AppCompatActivity{
    private static final String TAG = "GlobalActivity";
    private BottomNavigationView barNavigation;
    private static long back_pressed;
    private final  ProfileFragment profileFragment=new ProfileFragment();
    private final TopFragment topFragment =new TopFragment();
    private final FavoriteFragment favoriteFragment = new FavoriteFragment();
    private FragmentContainerView favoriteContainer;
    @SuppressLint("NonConstantResourceId")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global);
        File booksDirt = new File(getExternalFilesDir(null)+"/Books");
        if (!booksDirt.exists()){
            if (booksDirt.mkdir()){
                Log.d(TAG,"Create OK");
            }
        }
        favoriteContainer = findViewById(R.id.favorite_fragment);
        getSupportFragmentManager().beginTransaction().add(favoriteContainer.getId(),favoriteFragment).hide(favoriteFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.profileFragment,profileFragment).hide(profileFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.conteinerFragment, topFragment).commit();
        barNavigation=findViewById(R.id.bottomNavigationBar);
        barNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.topHot: {
                    favoriteContainer.setVisibility(View.INVISIBLE);
                    getSupportFragmentManager().beginTransaction().hide(favoriteFragment).hide(profileFragment).show(topFragment).commit();
                } return true;
                case R.id.favorite: {
                    favoriteContainer.setVisibility(View.VISIBLE);
                    getSupportFragmentManager().beginTransaction().hide(topFragment).hide(profileFragment).show(favoriteFragment).commit();
                } return true;
                case R.id.home:{
                    favoriteContainer.setVisibility(View.INVISIBLE);
                    getSupportFragmentManager().beginTransaction().hide(favoriteFragment).hide(topFragment).show(profileFragment).commit();
                } return true;
                default: return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (back_pressed+2000>System.currentTimeMillis()) super.onBackPressed();
        else {
            Toast.makeText(this,"Нажмите еще раз, чтобы выйти!",Toast.LENGTH_SHORT).show();
        }
        back_pressed=System.currentTimeMillis();
    }


}
