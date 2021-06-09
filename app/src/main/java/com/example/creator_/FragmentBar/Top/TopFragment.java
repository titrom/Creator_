package com.example.creator_.FragmentBar.Top;

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
import com.example.creator_.InsideBooks.FragmentsContentsBook.UserLib.OwnerBookToolsActivity;
import com.example.creator_.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class TopFragment extends Fragment {
    private final static String TAG = "TopFragment";
    private ViewPager2 vp2;
    private TabLayout tabLayout;
    private final FragmentTopLibrary fragmentTopLib = new FragmentTopLibrary();
    private final FragmentTopAudioLib fragmentTopAudioLib = new FragmentTopAudioLib();
    private final FragmentTopGallery fragmentTopGallery = new FragmentTopGallery();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.top_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        viewPager2();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void init(View view){

        vp2 = view.findViewById(R.id.vp2);
        tabLayout = view.findViewById(R.id.tab_layout);
    }


    private void viewPager2(){
        ArrayList<Fragment> fragments = new ArrayList<>();
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Книги");
        arrayList.add("Музыка");
        arrayList.add("Картины");
        fragments.add(fragmentTopLib);
        fragments.add(fragmentTopAudioLib);
        fragments.add(fragmentTopGallery);
        FragmentStateAdapter adapter=new AdapterViewPager2((FragmentActivity) getContext(),fragments);
        vp2.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, vp2, (tab, position) -> {
            switch (position){
                case 0 : tab.setText("Книги"); break;
                case 1 : tab.setText("Музыка"); break;
                case 2 : tab.setText("Картины"); break;
                default: break;
            }
        }).attach();
    }

}
