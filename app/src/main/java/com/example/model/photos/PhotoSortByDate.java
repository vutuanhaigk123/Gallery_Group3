package com.example.model.photos;

public class PhotoSortByDate {
    private String dateAdded;
    private PhotoList photoSortByDateList;

    public PhotoSortByDate(String dateAdded, PhotoList photoSortByDateList) {
        this.dateAdded = dateAdded;
        this.photoSortByDateList = photoSortByDateList;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public PhotoList getPhotoSortByDateList() {
        return photoSortByDateList;
    }

    public void setPhotoSortByDateList(PhotoList photoSortByDateList) {
        this.photoSortByDateList = photoSortByDateList;
    }
}
