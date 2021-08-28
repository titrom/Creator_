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
import androidx.fragment.app.FragmentContainerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.creator_.Adapters.AdapterViewPager2;
import com.example.creator_.InsideBooks.FragmentsContentsBook.UserLib.OwnerBookToolsActivity;
import com.example.creator_.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Objects;

public class TopFragment extends Fragment {
    private final static String TAG = "TopFragment";
    private final FragmentTopLibrary fragmentTopLib = new FragmentTopLibrary();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.top_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager2();
    }

    private void viewPager2(){
        getParentFragmentManager().beginTransaction().add(R.id.fragment_top,fragmentTopLib).commit();
    }

}
