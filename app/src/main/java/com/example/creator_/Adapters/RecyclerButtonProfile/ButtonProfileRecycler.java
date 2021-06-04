package com.example.creator_.Adapters.RecyclerButtonProfile;

public class ButtonProfileRecycler {
    private String nameButton;
    private int idImageButtonProfileRecycler;

    public ButtonProfileRecycler(String nameButton, int idImageButtonProfileRecycler) {
        this.nameButton = nameButton;
        this.idImageButtonProfileRecycler = idImageButtonProfileRecycler;
    }

    public String getNameButton() {
        return nameButton;
    }

    public void setNameButton(String nameButton) {
        this.nameButton = nameButton;
    }

    public int getIdImageButtonProfileRecycler() {
        return idImageButtonProfileRecycler;
    }

    public void setIdImageButtonProfileRecycler(int idImageButtonProfileRecycler) {
        this.idImageButtonProfileRecycler = idImageButtonProfileRecycler;
    }
}
