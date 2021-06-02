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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.creator_.R;

public class TopFragment extends Fragment {
    private final static String TAG = "TopFragment";
    protected SwipeRefreshLayout updateTop;
    private final FragmentTopLibrary fragmentTopLib = new FragmentTopLibrary();;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.top_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateTop = view.findViewById(R.id.updateTop);
        getChildFragmentManager().beginTransaction().add(R.id.containerTop, fragmentTopLib).show(fragmentTopLib).commit();
        update();
    }
    private void update(){
        fragmentTopLib.BooksRv();
        updateTop.setOnRefreshListener(() -> {
            Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
            fragmentTopLib.BooksRv();
        });
        updateTop.setColorSchemeResources(R.color.ItemColor);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {
            Log.i(TAG, "Refresh menu item selected");
            fragmentTopLib.BooksRv();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
