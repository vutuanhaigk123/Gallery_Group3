package com.example.model.photos;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.view.BuildConfig;

import java.io.File;
import java.io.Serializable;

public class Photo implements Serializable {
    private String path;
    private String dateAdded;
    private String milliseconds;
    private String mimeType;
    private String filename;
    private int index;
    private boolean isClicked = false;

    public Photo(String path, String dateAdded,
                 String mimeType, String filename,
                 int index) {
        this.path = path;
        this.milliseconds = dateAdded;
        this.dateAdded = dateAdded;
        this.mimeType = mimeType;
        this.filename = filename;
        this.index = index;

    }

    public boolean isClicked() {
        return isClicked;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    public String getMilliseconds() {
        return milliseconds;
    }

    public void setMilliseconds(String milliseconds) {
        this.milliseconds = milliseconds;
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
    public static void loadImage(SubsamplingScaleImageView view, String imageUrl){
//        Glide.with(imageView.getContext())
//                .load(imageUrl)
//                .into(imageView);
        Glide.with(view.getContext())
                .load(imageUrl)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        view.setImage(ImageSource.uri(imageUrl));
                    }
                });
    }

    @BindingAdapter("android:loadThumb")
    public static void loadThumb(ImageView imageView, String imageUrl){
        Glide.with(imageView.getContext())
                .load(imageUrl)
                .thumbnail(0.1f)
                .apply(RequestOptions.centerCropTransform())
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                .into(imageView);
    }

}
