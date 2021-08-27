package com.example.creator_.Adapters.AdapterFavoriteUser;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.creator_.R;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterFavoriteUser extends RecyclerView.Adapter<AdapterFavoriteUser.HolderFavoriteUser> {

    public interface OnClickFavoriteUser{
        void setOnClickItemFavoriteUser(FavoriteUserClass fUC, int position);
    }
    private final ArrayList<FavoriteUserClass> fUCs;
    private final OnClickFavoriteUser oCFU;
    private final Context context;
    private LayoutInflater layoutInflater;

    public AdapterFavoriteUser(ArrayList<FavoriteUserClass> fUCs, OnClickFavoriteUser oCFU, Context context) {
        this.fUCs = fUCs;
        this.oCFU = oCFU;
        this.context = context;
        if (context!= null){
            layoutInflater = LayoutInflater.from(context);
        }
    }

    @NonNull
    @Override
    public HolderFavoriteUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.adapter_favorite_user,parent,false);
        return new HolderFavoriteUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderFavoriteUser holder, int position) {
        FavoriteUserClass fUC = fUCs.get(position);
        if (holder.imageFavUser != null){
            Picasso.with(context).load(fUC.getImageUser()).into(holder.imageFavUser);
        }else{
            Picasso.with(context).load(R.drawable.ic_action_add).into(holder.imageFavUser);
        }
        holder.nicknameFavUser.setText(fUC.getNicknameFavoriteWriter());
        holder.cardView.setOnClickListener(v -> oCFU.setOnClickItemFavoriteUser(fUC,position));
    }

    @Override
    public int getItemCount() {
        return fUCs.size();
    }

    public static class HolderFavoriteUser extends RecyclerView.ViewHolder{
        MaterialCardView cardView;
        TextView nicknameFavUser;
        ImageView imageFavUser;
        public HolderFavoriteUser(@NonNull View itemView) {
            super(itemView);
            nicknameFavUser = itemView.findViewById(R.id.nicknameFavoriteUser);
            imageFavUser = itemView.findViewById(R.id.imageFavoriteUser);
            cardView = itemView.findViewById(R.id.cardInformation);
        }
    }
}
