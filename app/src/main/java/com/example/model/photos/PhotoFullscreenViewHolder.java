package com.example.model.photos;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.example.view.databinding.LayoutFullscreenPhotoBinding;

public class PhotoFullscreenViewHolder extends ViewHolder{
    LayoutFullscreenPhotoBinding fullscreenPhotoBinding;

    @Override
    public ViewDataBinding getBinding() {
        return fullscreenPhotoBinding;
    }

    public PhotoFullscreenViewHolder(@NonNull LayoutFullscreenPhotoBinding fullscreenPhotoBinding) {
        super(fullscreenPhotoBinding.getRoot());
        this.fullscreenPhotoBinding = fullscreenPhotoBinding;
        fullscreenPhotoBinding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideNavBar();
            }
        });
    }
    private void hideNavBar(){

        View decorView = ((Activity) itemView.getContext()).getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);
    }


}
