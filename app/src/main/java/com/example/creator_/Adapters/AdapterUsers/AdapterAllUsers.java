package com.example.creator_.Adapters.AdapterUsers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.creator_.Adapters.AdapterBooks.AdapterAllBooksRv;
import com.example.creator_.Adapters.AdapterBooks.AllBooksClass;
import com.example.creator_.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class AdapterAllUsers extends RecyclerView.Adapter<AdapterAllUsers.HolderAllUsers> {
    public interface OnClickUsers{
        void onClickUser(AllUsers au, int position);
    }

    private final ArrayList<AllUsers> auList;
    private final OnClickUsers oCUs;
    private final Context context;
    private LayoutInflater layoutInflater;
    private final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

    public AdapterAllUsers(ArrayList<AllUsers> auList, OnClickUsers oCUs, Context context){
        this.auList = auList;
        this.oCUs = oCUs;
        this.context = context;
        if (context != null){
            layoutInflater = LayoutInflater.from(context);
        }
    }

    @NonNull
    @Override
    public HolderAllUsers onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.users_item, parent, false);
        return new HolderAllUsers(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderAllUsers holder, int position) {
        AllUsers au = auList.get(position);
        if (Objects.requireNonNull(user).getUid().equals(au.getIdUser())){
            holder.userName.setText("Вы");
        }else holder.userName.setText(au.getNameUser());
        Picasso.with(context).load(au.getImageUser()).into(holder.userImage);
        holder.materialCardView.setOnClickListener(v -> {
            oCUs.onClickUser(au,position);
        });
    }

    @Override
    public int getItemCount() {
        return auList.size();
    }

    public static class HolderAllUsers extends RecyclerView.ViewHolder {
        TextView userName;
        ImageButton userImage;
        MaterialCardView materialCardView;
        public HolderAllUsers(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userImage = itemView.findViewById(R.id.imageUser);
            materialCardView = itemView.findViewById(R.id.cardUsersItem);
        }
    }
}
