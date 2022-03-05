package com.example.model.photos;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.example.view.FullscreenPhotoActivity;
import com.example.view.databinding.LayoutFullscreenPhotoBinding;

public class PhotoFullscreenViewHolder extends ViewHolder{
    LayoutFullscreenPhotoBinding fullscreenPhotoBinding;
    PhotoList photoList;


    @Override
    public ViewDataBinding getBinding() {
        return fullscreenPhotoBinding;
    }

    public PhotoFullscreenViewHolder(@NonNull LayoutFullscreenPhotoBinding fullscreenPhotoBinding,
                                     PhotoList photoList) {
        super(fullscreenPhotoBinding.getRoot());
        this.fullscreenPhotoBinding = fullscreenPhotoBinding;
        this.photoList = photoList;
        final boolean[] isClicked = {true};
        hideNavBar();
        fullscreenPhotoBinding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isClicked[0] == false){
                    hideNavBar();
                    FullscreenPhotoActivity.actionBar.hide();
                    isClicked[0] = true;
                }
                else{
                    showNavBar();
                    FullscreenPhotoActivity.actionBar.show();
                    isClicked[0] = false;
                }
            }
        });
    }
    public void hideNavBar(){
        View decorView = ((Activity) itemView.getContext()).getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }
    public void showNavBar(){
        View decorView = ((Activity) itemView.getContext()).getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
    }

}
