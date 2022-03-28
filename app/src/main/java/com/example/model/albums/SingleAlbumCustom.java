package com.example.model.albums;

import android.net.Uri;

public class SingleAlbumCustom {
    String name;
    Uri uri;
    int quantity;
    int index;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public SingleAlbumCustom(String name, Uri uri, int quantity, int index) {
        this.name = name;
        this.uri = uri;
        this.quantity = quantity;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
