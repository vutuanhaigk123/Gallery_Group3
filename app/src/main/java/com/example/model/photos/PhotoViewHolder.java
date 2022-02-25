package com.example.model.photos;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.example.view.FullscreenPhotoActivity;
import com.example.view.databinding.LayoutPhotoThumbnailBinding;

import java.util.Collections;

public class PhotoViewHolder extends ViewHolder {
    LayoutPhotoThumbnailBinding photoRowBinding;
    PhotoList photoList;
    public PhotoViewHolder(@NonNull LayoutPhotoThumbnailBinding photoRowBinding, PhotoList photoList) {
        super(photoRowBinding.getRoot());
        Collections.reverse(PhotoSortByDateAdapter.ogPhotoList.getPhotoList()); // Đảo ngược danh sách các hình ảnh ban đầu để hình được thêm gần nhất xuất hiện đầu tiên trong danh sách FullScreenView
        this.photoRowBinding = photoRowBinding;
        this.photoList = PhotoSortByDateAdapter.ogPhotoList;
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
        intent.putExtra("pos",photoList.size() - 1 - photoRowBinding.getPhoto().getIndex() ); // Đảo ngược index để hình được thêm gần nhất xuất hiện đầu tiên trong danh sách FullScreenView
        intent.putExtra("photoList", PhotoSortByDateAdapter.ogPhotoList);
        photoRowBinding.getRoot().getContext().startActivity(intent);

    }

}
