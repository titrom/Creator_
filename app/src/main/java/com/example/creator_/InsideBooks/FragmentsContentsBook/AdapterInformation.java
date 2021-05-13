package com.example.creator_.InsideBooks.FragmentsContentsBook;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class AdapterInformation extends FragmentStateAdapter {
    private final ArrayList<Fragment> fragments;

    public AdapterInformation(@NonNull FragmentActivity fragmentActivity,ArrayList<Fragment> fragments) {
        super(fragmentActivity);
        this.fragments=fragments;

    }




    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
