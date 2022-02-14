package com.example.model.photos;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.example.view.FullscreenPhotoActivity;
import com.example.view.databinding.LayoutPhotoThumbnailBinding;

public class PhotoViewHolder extends ViewHolder {
    LayoutPhotoThumbnailBinding photoRowBinding;
    public PhotoViewHolder(@NonNull LayoutPhotoThumbnailBinding photoRowBinding) {
        super(photoRowBinding.getRoot());
        this.photoRowBinding = photoRowBinding;
        photoRowBinding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToFullscreenPhoto();
            }
        });
    }

    @Override
    public ViewDataBinding getBinding() {
        return photoRowBinding;
    }

    private void switchToFullscreenPhoto(){
        Intent intent = new Intent(photoRowBinding.getRoot().getContext(), FullscreenPhotoActivity.class);
        intent.putExtra("pos", photoRowBinding.getPhoto().getIndex());
        photoRowBinding.getRoot().getContext().startActivity(intent);

    }

}
