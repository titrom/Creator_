package com.example.creator_.RecyclerChipsAndAdapter;

public class ChipRecycler {
    private String textChip;
    private String typeFile;

    public ChipRecycler(String textChip, String typeFile) {
        this.textChip = textChip;
        this.typeFile = typeFile;
    }

    public String getTextChip() {
        return textChip;
    }

    public void setTextChip(String textChip) {
        this.textChip = textChip;
    }

    public String getTypeFile() {
        return typeFile;
    }

    public void setTypeFile(String typeFile) {
        this.typeFile = typeFile;
    }
}
