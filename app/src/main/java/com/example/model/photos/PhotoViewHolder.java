package com.example.model.photos;

import android.content.Intent;
import android.graphics.Color;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.example.view.PhotosFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.example.view.FullscreenPhotoActivity;
import com.example.view.R;
import com.example.view.databinding.LayoutPhotoThumbnailBinding;

import java.util.ArrayList;
import java.util.Collections;

public class PhotoViewHolder extends ViewHolder {
    LayoutPhotoThumbnailBinding photoRowBinding;
    PhotoList photoList;


    public PhotoViewHolder(@NonNull LayoutPhotoThumbnailBinding photoRowBinding, PhotoList photoList) {
        super(photoRowBinding.getRoot());
        // Đảo ngược danh sách các hình ảnh ban đầu để hình được thêm
        // gần nhất xuất hiện đầu tiên trong danh sách FullScreenView
//        Collections.reverse(PhotoSortByDateAdapter.ogPhotoList.getPhotoList());
        this.photoRowBinding = photoRowBinding;
        this.photoList = photoList;
        photoRowBinding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(PhotosFragment.isEnable) {
                    ClickItem(photoRowBinding);
                }
                else {
                    switchToFullscreenPhoto();
                }
            }
        });
    }

    @Override
    public ViewDataBinding getBinding() {
        return photoRowBinding;
    }

    private void switchToFullscreenPhoto(){
        Intent intent = new Intent(photoRowBinding.getRoot().getContext(), FullscreenPhotoActivity.class);
        // Đảo ngược index để hình được thêm gần nhất xuất hiện đầu tiên trong danh sách FullScreenView
        //intent.putExtra("pos",photoList.getPhotoList().size() - 1 - photoRowBinding.getPhoto().getIndex());
        intent.putExtra("pos",PhotoSortByDateAdapter.ogPhotoList.size() - 1 - photoRowBinding.getPhoto().getIndex());
        intent.putExtra("photoList", PhotoSortByDateAdapter.ogPhotoList);
        photoRowBinding.getRoot().getContext().startActivity(intent);

    }

    public void ClickItem(LayoutPhotoThumbnailBinding layoutPhotoByDateAddedBinding) {
        Photo photoTemp = layoutPhotoByDateAddedBinding.getPhoto();
        if(photoRowBinding.icCheck.getVisibility() == View.GONE){
            // Khi hình ảnh chưa được chọn
            layoutPhotoByDateAddedBinding.getPhoto().setClicked(true);
            photoRowBinding.icCheck.setVisibility(View.VISIBLE);
            PhotosFragment.selectedList.add(photoTemp);
        }
        else{
            // Khi hình ảnh đã được chọn
            layoutPhotoByDateAddedBinding.getPhoto().setClicked(false);
            photoRowBinding.icCheck.setVisibility(View.GONE);
            PhotosFragment.selectedList.remove(photoTemp);
        }
        for (int i=0; i < PhotosFragment.selectedList.size();i++){
            System.out.println(PhotosFragment.selectedList.get(i).getFilename());
        }
        System.out.println("-------------------------");
        //Set text
    }
}
