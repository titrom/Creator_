package com.example.creator_.RecyclerButtonProfile;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.creator_.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textview.MaterialTextView;

import org.w3c.dom.Text;

import java.lang.annotation.Annotation;
import java.util.List;

import static com.example.creator_.R.color.grey;

public class AdapterRecyclerProfileButton extends RecyclerView.Adapter<AdapterRecyclerProfileButton.ButtonRecyclerHolder> {
    public interface OnClickListenerBPR{
        void onProfileButtonRec(ButtonProfileRecycler bPR,int position);
    }
    private List<ButtonProfileRecycler> listProfBut;
    private Context context;
    private final OnClickListenerBPR onClickListenerBPR;
    private LayoutInflater layoutInflater;

    public AdapterRecyclerProfileButton(List<ButtonProfileRecycler> listProfBut, Context context, OnClickListenerBPR onClickListener) {
        this.listProfBut = listProfBut;
        this.context = context;
        layoutInflater=LayoutInflater.from(context);
        this.onClickListenerBPR=onClickListener;
    }

    @NonNull
    @Override
    public ButtonRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=layoutInflater.inflate(R.layout.button_recycler_profile,parent,false);
        return new ButtonRecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ButtonRecyclerHolder holder, int position) {
        ButtonProfileRecycler bProfileR=listProfBut.get(position);
        holder.button.setText(bProfileR.getNameButton());
        holder.button.setIconResource(bProfileR.getIdImageButtonProfileRecycler());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListenerBPR.onProfileButtonRec(bProfileR,position);
            }
        });

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
