package com.example.creator_.RecyclerChipsAndAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.creator_.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

public class AdapterRecyclerChips extends RecyclerView.Adapter<AdapterRecyclerChips.ChipsHolder> {
    public interface OnClickCloseChips{
        void onChipsClickCloseIcon(ChipRecycler cp,int Position);
    }
    private final List<ChipRecycler> listChips;
    private final OnClickCloseChips clickCloseChips;
    private final Context context;
    private final LayoutInflater layoutInflater;

    public AdapterRecyclerChips(List<ChipRecycler> listProfBut, Context context,OnClickCloseChips closeChips) {
        this.listChips= listProfBut;
        this.context = context;
        layoutInflater=LayoutInflater.from(context);
        this.clickCloseChips=closeChips;
    }

    @NonNull
    @Override
    public ChipsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=layoutInflater.inflate(R.layout.chips_layout_recycler,parent,false);
        return new ChipsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChipsHolder holder, int position) {
        ChipRecycler chipR=listChips.get(position);
        holder.chip.setText(chipR.getTextChip());
        holder.chip.setChipIconResource(R.drawable.ic_action_add_file_pdf);
        holder.chip.setOnCloseIconClickListener(v -> {
            holder.chipGroup.removeView(v);
            clickCloseChips.onChipsClickCloseIcon(chipR,position);
        });
    }
    @Override
    public int getItemCount() {
        return listChips.size();
    }


    public class ChipsHolder extends RecyclerView.ViewHolder {
        Chip chip;
        ChipGroup chipGroup;
        public ChipsHolder(@NonNull View itemView) {
            super(itemView);
            chipGroup=itemView.findViewById(R.id.chip_groupRec);
            chip=itemView.findViewById(R.id.chipRec);
        }
    }
}
