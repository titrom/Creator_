package com.example.creator_.FragmentBar.Top;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.creator_.Adapters.AdapterSearchBook.AdapterSearchBook;
import com.example.creator_.Adapters.AdapterSearchBook.SearchItemClass;
import com.example.creator_.R;

import java.util.ArrayList;

public class SearchBookFragment extends Fragment {

    private SearchCompleteFragment searchCompleteFragment;
    private ArrayList<SearchItemClass> sICList;
    private RecyclerView rvSearch;
    private AdapterSearchBook.OnClickSearchBookItem oCSBI;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_book, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        TopFragment topFragment = new TopFragment();
        if (topFragment.sICList!= null ) sICList.addAll(topFragment.sICList);
        rvSearch.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        if (sICList != null){
            oCSBI = (sIC, position) -> { };
            AdapterSearchBook adapterSearchBook = new AdapterSearchBook(sICList,oCSBI ,getContext());
            rvSearch.setAdapter(adapterSearchBook);
            sICList.forEach(searchItemClass -> Log.d("Tag", searchItemClass.getNameBookS()));
        }
    }




    private void init(View view){
        rvSearch = view.findViewById(R.id.searchBooksRv);
    }
}