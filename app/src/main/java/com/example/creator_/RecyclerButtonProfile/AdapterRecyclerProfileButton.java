package com.example.creator_.RecyclerButtonProfile;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.creator_.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import org.w3c.dom.Text;

import java.lang.annotation.Annotation;
import java.util.List;

public class AdapterRecyclerProfileButton extends RecyclerView.Adapter<AdapterRecyclerProfileButton.ButtonRecyclerHolder> {
    private List<ButtonProfileRecycler> listProfBut;
    private Context context;
    private LayoutInflater layoutInflater;

    public AdapterRecyclerProfileButton(List<ButtonProfileRecycler> listProfBut, Context context) {
        this.listProfBut = listProfBut;
        this.context = context;
        layoutInflater=LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ButtonRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=layoutInflater.inflate(R.layout.button_recycler_profile,parent,false);
        return new ButtonRecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ButtonRecyclerHolder holder, int position) {
        ButtonProfileRecycler bPR=listProfBut.get(position);
        holder.button.setText(bPR.getNameButton());
        holder.button.setIconResource(bPR.getIdImageButtonProfileRecycler());
    }

    @Override
    public int getItemCount() {
        return listProfBut.size();
    }
    public class ButtonRecyclerHolder extends RecyclerView.ViewHolder{
        MaterialButton button;

        public ButtonRecyclerHolder(@NonNull View itemView) {
            super(itemView);
            button=itemView.findViewById(R.id.buttonProfileRecycler);
        }
    }
}
