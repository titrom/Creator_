package com.example.creator_.FragmentBar.Favorite;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.creator_.Adapters.AdapterViewPager2;
import com.example.creator_.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Objects;

public class FavoriteFragment extends Fragment {
    private final static String TAG = "FavoriteFragment";
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private FavoriteCreations favoriteCreations;
    private  FavoriteCreators favoriteCreators;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorite_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        setViewPager2();
    }
    private void setViewPager2(){
        favoriteCreations = new FavoriteCreations();
        favoriteCreators = new FavoriteCreators();
        ArrayList<Fragment> fragments = new ArrayList<>();
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Творения");
        arrayList.add("Творцы");
        fragments.add(favoriteCreations);
        fragments.add(favoriteCreators);
        FragmentStateAdapter adapter=new AdapterViewPager2((FragmentActivity) requireContext(),fragments);
        viewPager2.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
            if (position==0){
                tab.setText("Творения");
            }else if (position==1){
                tab.setText("Творцы");
            }
        }).attach();

    }
    private void init(View view){
        viewPager2 = view.findViewById(R.id.vp2);
        tabLayout = view.findViewById(R.id.tab_layout_favorite);
    }
}
