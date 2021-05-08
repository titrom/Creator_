package UserFirestore;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class BookClass {
    private final String nameBook;
    private final String description;
    private final String UserId;
    private final boolean privacyLevel;
    private final Timestamp dateBook;
    private final int subColl;
    private final ArrayList<String> subId;

    public BookClass(Timestamp dateBook, String description,String nameBook, String userId,boolean privacyLevel,int subColl,ArrayList<String> subId) {
        this.nameBook = nameBook;
        this.description = description;
        UserId = userId;
        this.dateBook=dateBook;
        this.privacyLevel=privacyLevel;
        this.subColl=subColl;
        this.subId=subId;
    }

    public int getSubColl() {
        return subColl;
    }

    public ArrayList<String> getSubId() {
        return subId;
    }

    public String getNameBook() {
        return nameBook;
    }

    public String getDescription() {
        return description;
    }

    public String getUserId() {
        return UserId;
    }


    public boolean isPrivacyLevel() {
        return privacyLevel;
    }

    public Timestamp getDateBook() {
        return dateBook;
    }
}
