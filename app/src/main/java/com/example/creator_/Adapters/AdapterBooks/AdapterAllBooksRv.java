package com.example.creator_.Adapters.AdapterBooks;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.creator_.InsideBooks.FragmentsContentsBook.Books.BookToolsActivity;
import com.example.creator_.InsideBooks.FragmentsContentsBook.UserLib.OwnerBookToolsActivity;
import com.example.creator_.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterAllBooksRv extends RecyclerView.Adapter<AdapterAllBooksRv.HolderBooksRv> {
    public interface OnClickAllBooksRv {
        void onClickBook(AllBooksClass bc, int position);
    }
    private final ArrayList<AllBooksClass> abcList;
    private final OnClickAllBooksRv oCABR;
    private final Context context;
    private LayoutInflater layoutInflater;
    private final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
    public AdapterAllBooksRv(ArrayList<AllBooksClass> abcList, OnClickAllBooksRv oCABR, Context context) {
        this.abcList = abcList;
        this.oCABR = oCABR;
        this.context = context;
        if (context!= null){
            layoutInflater = LayoutInflater.from(context);
        }
    }


    @NonNull
    @Override
    public HolderBooksRv onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.all_books_rv,parent,false);
        return new HolderBooksRv(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderBooksRv holder, int position) {
        AllBooksClass abc = abcList.get(position);
        holder.nameBook.setText(abc.getNameBook());
        Picasso.with(context).load(abc.getImageBook()).into(holder.imageBook);
        holder.cardView.setOnClickListener(v -> {
            if (user.getUid().equals(abc.getWriterId())){
                Intent intent = new Intent(context, OwnerBookToolsActivity.class);
                intent.putExtra("idBook", abc.getIdBook());
                context.startActivity(intent);
            }else {
                Intent intent = new Intent(context, BookToolsActivity.class);
                intent.putExtra("idBook", abc.getIdBook());
                intent.putExtra("userId",abc.getWriterId());
                context.startActivity(intent);
            }
            oCABR.onClickBook(abc,position);
        });
    }

    @Override
    public int getItemCount() {
        return abcList.size();
    }



    public static  class HolderBooksRv extends RecyclerView.ViewHolder{
        TextView nameBook;
        ImageView imageBook;
        MaterialCardView cardView;
        public HolderBooksRv(@NonNull View itemView) {
            super(itemView);
            nameBook = itemView.findViewById(R.id.nameBookCard);
            imageBook = itemView.findViewById(R.id.image_books);
            cardView = itemView.findViewById(R.id.cardBooks);
        }
    }
}
