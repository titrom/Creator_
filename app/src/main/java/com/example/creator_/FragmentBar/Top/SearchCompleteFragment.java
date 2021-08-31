package com.example.creator_.FragmentBar.Top;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.creator_.Adapters.AdapterViewPager2;
import com.example.creator_.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class SearchCompleteFragment extends Fragment {
    private static final String TAG = "SearchCompleteFragment";
    private SearchBookFragment searchBook;
    private SearchUserFragment searchUser;
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_complete, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        setViewPager2();


    }


    private void setViewPager2(){
        searchBook = new SearchBookFragment();
        searchUser = new SearchUserFragment();
        ArrayList<Fragment> fragments = new ArrayList<>();
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Книги");
        arrayList.add("Творцы");
        fragments.add(searchBook);
        fragments.add(searchUser);
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