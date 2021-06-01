package com.example.creator_.InsideBooks.FragmentsContentsBook.RecyclerChapter;

public class ChapterClass {
    private  String nameChapter;
    private boolean downloadCheck;

    public ChapterClass(String nameChapter, boolean downloadCheck) {
        this.nameChapter = nameChapter;
        this.downloadCheck = downloadCheck;
    }

    public boolean isDownloadCheck() {
        return downloadCheck;
    }

    public void setDownloadCheck(boolean downloadCheck) {
        this.downloadCheck = downloadCheck;
    }

    public String getNameChapter() {
        return nameChapter;
    }

    public void setNameChapter(String nameChapter) {
        this.nameChapter = nameChapter;
    }
}
