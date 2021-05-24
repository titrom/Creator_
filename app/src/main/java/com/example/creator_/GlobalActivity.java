package com.example.creator_;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.creator_.FragmentBar.FragmentBar;
import com.example.creator_.FragmentBar.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

@RequiresApi(api = Build.VERSION_CODES.O)
public class GlobalActivity extends AppCompatActivity{
    private BottomNavigationView barNavigation;
    private static long back_pressed;
    ProfileFragment profileFragment=new ProfileFragment();;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global);
        FragmentBar fragmentBar=new FragmentBar();

        getSupportFragmentManager().beginTransaction().add(R.id.conteinerFragment,profileFragment).hide(profileFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.conteinerFragment,fragmentBar).commit();
        barNavigation=findViewById(R.id.bottomNavigationBar);
        barNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.topHot: getSupportFragmentManager().beginTransaction().hide(profileFragment).show(fragmentBar).commit(); return true;
                    case R.id.favorite: getSupportFragmentManager().beginTransaction().hide(fragmentBar).hide(profileFragment).commit(); return true;
                    case R.id.home:getSupportFragmentManager().beginTransaction().hide(fragmentBar).show(profileFragment).commit(); return true;
                    default: return false;
                }
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
