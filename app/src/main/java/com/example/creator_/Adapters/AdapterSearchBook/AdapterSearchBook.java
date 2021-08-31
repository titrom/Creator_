package com.example.creator_.Adapters.AdapterSearchBook;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.creator_.Adapters.AdapterBooks.AllBooksClass;
import com.example.creator_.InsideBooks.FragmentsContentsBook.Books.BookToolsActivity;
import com.example.creator_.InsideBooks.FragmentsContentsBook.UserLib.OwnerBookToolsActivity;
import com.example.creator_.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class AdapterSearchBook extends RecyclerView.Adapter<AdapterSearchBook.HolderSearchBook> {
    public interface OnClickSearchBookItem{
        void onClickItem(SearchItemClass sIC, int position);
    }
    private final ArrayList<SearchItemClass> sICList;
    private final OnClickSearchBookItem oCSBI;
    private final Context context;
    private LayoutInflater layoutInflater;

    private final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

    public AdapterSearchBook(ArrayList<SearchItemClass> sICList, OnClickSearchBookItem oCSBI, Context context){
        this.sICList =sICList;
        this.oCSBI = oCSBI;
        this.context = context;
        if (context != null){
            layoutInflater = LayoutInflater.from(context);
        }

    }

    @NonNull
    @Override
    public HolderSearchBook onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.recycler_my_book, parent, false);
        return new HolderSearchBook(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderSearchBook holder, int position) {
        SearchItemClass sIC = sICList.get(position);
        holder.nameBook.setText(sIC.getNameBookS());
        Picasso.with(context).load(sIC.getImageBookS()).into(holder.imageBook);
        holder.card.setOnClickListener(v -> {
            if (Objects.requireNonNull(user).getUid().equals(sIC.getUserIdS())){
                Intent intent = new Intent(context, OwnerBookToolsActivity.class);
                intent.putExtra("idBook", sIC.getIdBookS());
                context.startActivity(intent);
            }else {
                Intent intent = new Intent(context, BookToolsActivity.class);
                intent.putExtra("idBook", sIC.getIdBookS());
                intent.putExtra("userId",sIC.getUserIdS());
                context.startActivity(intent);
            }
            oCSBI.onClickItem(sIC,position);
        });
    }

    @Override
    public int getItemCount() {
        return sICList.size();
    }

    public static class HolderSearchBook extends RecyclerView.ViewHolder {
        MaterialCardView card;
        ImageView imageBook;
        TextView nameBook;
        public HolderSearchBook(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.cardBook);
            imageBook = itemView.findViewById(R.id.My_bookImage);
            nameBook =imageBook.findViewById(R.id.NameBook);
        }
    }
}
