package com.example.creator_.RecyclerMyBook;
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
import com.squareup.picasso.Picasso;
import java.util.List;
public class AdapterRecyclerMyBook extends RecyclerView.Adapter<AdapterRecyclerMyBook.HolderMyBook> {
    public interface OnClickBookRec{
        void OnClickBookItem(MyBookClass mBC,int position);
    }

    private final List<MyBookClass> listMyBook;
    private final OnClickBookRec oCBR;
    private final Context context;
    private LayoutInflater layoutInflater;


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
        public HolderMyBook(@NonNull View itemView) {
            super(itemView);
            privacy=itemView.findViewById(R.id.privacyText);
            imageViewBook=itemView.findViewById(R.id.My_bookImage);
            BookName=itemView.findViewById(R.id.NameBook);
            materialCardView=itemView.findViewById(R.id.cardBook);
        }
    }
}
