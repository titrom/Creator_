package com.example.creator_.Adapters.RecyclerMyBook;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.creator_.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdapterRecyclerMyBook extends RecyclerView.Adapter<AdapterRecyclerMyBook.HolderMyBook> {
    public interface OnClickBookRec{
        void OnClickBookItem(MyBookClass mBC,int position);
    }

    private final List<MyBookClass> listMyBook;
    private final OnClickBookRec oCBR;
    private final Context context;
    private LayoutInflater layoutInflater;
    private final FirebaseFirestore db=FirebaseFirestore.getInstance();
    public AdapterRecyclerMyBook(List<MyBookClass> listMyBook, Context context, OnClickBookRec bookRec) {
        this.listMyBook = listMyBook;
        this.context = context;
        if (context!=null){
            layoutInflater = LayoutInflater.from(context);
        }
        this.oCBR=bookRec;

    }

    @NonNull
    @Override
    public HolderMyBook onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=layoutInflater.inflate(R.layout.recycler_my_book,parent,false);
        return new HolderMyBook(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderMyBook holder, int position) {
        MyBookClass myBC=listMyBook.get(position);
        File bookDir = new File(context.getExternalFilesDir(null)+"/Books/"+myBC.getIdBook());
        if (!bookDir.exists()){
            holder.imageView.setVisibility(View.INVISIBLE);
        }else {
            db.collection("Book").document(myBC.getIdBook()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if (Objects.requireNonNull(snapshot).exists()){
                        int collChapter = Integer.parseInt(Objects.requireNonNull
                                (Objects.requireNonNull(snapshot.getData())
                                        .get("collChapter")).toString());
                        ArrayList<File> fileList= new ArrayList<>();
                        for (int i=1;i<=collChapter;i++){
                            File localFile = new File(bookDir+ "/" + "Глава"+ i + ".pdf");
                            if (!localFile.exists()){
                                fileList.add(localFile);
                                if (fileList.size()==collChapter){
                                    holder.imageView.setVisibility(View.INVISIBLE);
                                }else if (fileList.size()<collChapter){
                                    holder.imageView.setVisibility(View.VISIBLE);
                                    holder.imageView.setImageResource(R.drawable.new_download);
                                }
                            }
                            else {
                                holder.imageView.setVisibility(View.VISIBLE);
                                holder.imageView.setImageResource(R.drawable.download);
                            }
                        }
                    }
                }
            });
        }
        if (!myBC.isPrivacy()){
            holder.privacy.setText("Черновик");
        }
        Picasso.with(context).load(myBC.getImageBook()).into(holder.imageViewBook);
        holder.BookName.setText(myBC.getNameBook());
        holder.materialCardView.setOnClickListener(v -> oCBR.OnClickBookItem(myBC,position));
    }

    @Override
    public int getItemCount() {
        return listMyBook.size();
    }


    public static class HolderMyBook extends RecyclerView.ViewHolder{
        ImageView imageViewBook;
        TextView privacy;
        TextView BookName;
        MaterialCardView materialCardView;
        ImageView imageView;
        public HolderMyBook(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.downloadIndex);
            privacy=itemView.findViewById(R.id.privacyText);
            imageViewBook=itemView.findViewById(R.id.My_bookImage);
            BookName=itemView.findViewById(R.id.NameBook);
            materialCardView=itemView.findViewById(R.id.cardBook);
        }
    }
}
