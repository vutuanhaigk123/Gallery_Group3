package com.example.model.albums;

import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;

import com.example.model.photos.PhotoList;
import com.example.model.photos.ViewHolder;
import com.example.view.AlbumsFragment;
import com.example.view.PhotosActivity;
import com.example.view.databinding.LayoutAlbumThumbnailBinding;

public class AlbumViewHolder extends ViewHolder {
    LayoutAlbumThumbnailBinding albumRowBinding;
    AlbumList albumList;
    public AlbumViewHolder(@NonNull LayoutAlbumThumbnailBinding albumRowBinding, AlbumList albumList) {
        super(albumRowBinding.getRoot());
        this.albumList = albumList;
        this.albumRowBinding = albumRowBinding;
//        albumRowBinding.imgAvtAlbum.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(AlbumsFragment.isEnable){
//                    if(!AlbumAdapter.isSystemAlbum(albumRowBinding.getAlbum())){
//                        ClickItem(albumRowBinding);
//                    }
//                }
//                else{
//                    switchToScreenPhoto();
//                }
//            }
//        });
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
        intent.putExtra("isAlbum",true);
        albumRowBinding.getRoot().getContext().startActivity(intent);

    }

    public void ClickItem(LayoutAlbumThumbnailBinding layoutAlbumThumbnailBinding) {
//        Photo photoTemp = layoutPhotoByDateAddedBinding.getPhoto().getPhotoSortByDateList().getPhotoList().get(position);
        Album albumTemp = layoutAlbumThumbnailBinding.getAlbum();
        View icCheck = layoutAlbumThumbnailBinding.icCheck;
        View icUnCheck = layoutAlbumThumbnailBinding.icUnCheck;
        if(icCheck.getVisibility() == View.GONE){
            // Khi hình ảnh chưa được chọn
            layoutAlbumThumbnailBinding.getAlbum().setClicked(true);
            icCheck.setVisibility(View.VISIBLE);
            icUnCheck.setVisibility(View.GONE);
            AlbumsFragment.selectedList.add(albumTemp);
        }
        else{
            layoutAlbumThumbnailBinding.getAlbum().setClicked(false);
            // Khi hình ảnh đã được chọn
            icCheck.setVisibility(View.GONE);
            icUnCheck.setVisibility(View.VISIBLE);
            AlbumsFragment.selectedList.remove(albumTemp);
        }
        for (int i=0; i < AlbumsFragment.selectedList.size();i++){
            System.out.println(AlbumsFragment.selectedList.get(i).getName());
        }
        System.out.println("-------------------------");
        //Set text
    }

}
