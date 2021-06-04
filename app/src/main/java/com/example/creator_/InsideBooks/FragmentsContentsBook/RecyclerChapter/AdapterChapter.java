package com.example.creator_.InsideBooks.FragmentsContentsBook.RecyclerChapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.creator_.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AdapterChapter extends RecyclerView.Adapter<AdapterChapter.HolderChapter> {
    public interface OnClickChapter{
        void OnClickItem(ChapterClass cc, int position);
    }
    private final List<ChapterClass> listChapter;
    private final AdapterChapter.OnClickChapter oCC;
    private final Context context;
    private LayoutInflater layoutInflater;

    public AdapterChapter(List<ChapterClass> listChapter,AdapterChapter.OnClickChapter oCC, Context context) {
        this.listChapter = listChapter;
        this.oCC = oCC;
        this.context = context;
        if (context!=null){
            layoutInflater = LayoutInflater.from(context);
        }
    }

    @NonNull
    @Override
    public HolderChapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.recycler_chapter,parent,false);
        return new HolderChapter(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull HolderChapter holder, int position) {
        ChapterClass cc= listChapter.get(position);
        if (cc.isDownloadCheck()){
            holder.nameChapter.setText(cc.getNameChapter());
            holder.nameChapter.setClickable(true);
            holder.nameChapter.setTextColor(R.color.black);
            holder.nameChapter.setOnClickListener(v -> oCC.OnClickItem(cc, position));
        }else {
            holder.nameChapter.setText(cc.getNameChapter());
            holder.nameChapter.setClickable(false);
            holder.nameChapter.setTextColor(R.color.grey);
        }

    }

    @Override
    public int getItemCount() {
        return listChapter.size();
    }

    public static class HolderChapter extends RecyclerView.ViewHolder{
        MaterialButton nameChapter;
        public HolderChapter(@NonNull View itemView) {
            super(itemView);
            nameChapter = itemView.findViewById(R.id.nameChapter);

        }
    }
}
