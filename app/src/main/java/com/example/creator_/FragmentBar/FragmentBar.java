package com.example.creator_.FragmentBar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.creator_.R;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import static com.google.android.material.tabs.TabLayout.*;

public class FragmentBar extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bar_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        TabLayout tabLayout;
//        tabLayout=view.findViewById(R.id.location);
//        TabItem library=view.findViewById(R.id.Library);
//        tabLayout.addOnTabSelectedListener(new OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(Tab tab) {
//            }
//
//            @Override
//            public void onTabUnselected(Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(Tab tab) {
//
//            }
//        });


    }
}
