package com.example.model.albums;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.example.model.photos.PhotoList;
import com.example.model.photos.ViewHolder;
import com.example.view.PhotosActivity;
import com.example.view.databinding.LayoutAlbumThumbnailBinding;

public class AlbumViewHolder extends ViewHolder {
    LayoutAlbumThumbnailBinding albumRowBinding;
    AlbumList albumList;
    public AlbumViewHolder(@NonNull LayoutAlbumThumbnailBinding albumRowBinding, AlbumList albumList) {
        super(albumRowBinding.getRoot());
        this.albumList = albumList;
        this.albumRowBinding = albumRowBinding;
        albumRowBinding.imgAvtAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToScreenPhoto();
            }
        });
    }

    @Override
    public ViewDataBinding getBinding() {
        return albumRowBinding;
    }
    private void switchToScreenPhoto(){
        Intent intent = new Intent(albumRowBinding.getRoot().getContext(), PhotosActivity.class);
        int pos = albumRowBinding.getPhoto().getIndex()-1;
        PhotoList photoList = AlbumRoute.getPhotoListByAlbum(albumList.get(pos).getId());

        intent.putExtra("photoListOfAlbum",photoList);
        intent.putExtra("nameOfAlbum",albumList.get(pos).getName());
        albumRowBinding.getRoot().getContext().startActivity(intent);

    }


}
