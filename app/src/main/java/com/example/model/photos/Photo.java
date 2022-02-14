package com.example.model.photos;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import androidx.core.content.FileProvider;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.view.BuildConfig;

import java.io.File;
import java.io.Serializable;

public class Photo implements Serializable {
    private String path;
    private String dateAdded;
    private String mimeType;
    private String filename;
    private int index;

    public Photo(String path, String dateAdded,
                 String mimeType, String filename,
                 int index) {
        this.path = path;
        this.dateAdded = dateAdded;
        this.mimeType = mimeType;
        this.filename = filename;
        this.index = index;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(String dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Uri getUri(Context context){
        return FileProvider.getUriForFile( context,
                BuildConfig.APPLICATION_ID + ".provider", new File(path));
    }

    @BindingAdapter("android:loadImage")
    public static void loadImage(ImageView imageView,String imageUrl){
        Glide.with(imageView.getContext())
                .load(imageUrl)
                .into(imageView);
    }

    @BindingAdapter("android:loadThumb")
    public static void loadThumb(ImageView imageView, String imageUrl){
        Glide.with(imageView.getContext())
                .load(imageUrl)
                .thumbnail(0.1f)
                .apply(RequestOptions.centerCropTransform())
                .into(imageView);
    }
}
