package com.example.model.photos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.recyclerview.widget.RecyclerView;

import com.example.view.databinding.LayoutFullscreenPhotoBinding;
import com.example.view.databinding.LayoutPhotoThumbnailBinding;

public class PhotoAdapter extends RecyclerView.Adapter<ViewHolder> {
    private PhotoList photoList;
    private int mode;

    public final static int THUMBNAIL_MODE = 0;
    public final static int FULLSCREEN_MODE = 1;

    public PhotoAdapter(PhotoList photoList, int mode){
        this.photoList = photoList;
        this.mode = mode;
    }

    public PhotoList getPhotoList(){
        return photoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if(mode == THUMBNAIL_MODE) {
            LayoutPhotoThumbnailBinding photoRowBinding = LayoutPhotoThumbnailBinding.inflate(
                    layoutInflater, parent, false
            );
            return new PhotoViewHolder(photoRowBinding, photoList);
        }
        else if(mode == FULLSCREEN_MODE){
            LayoutFullscreenPhotoBinding fullscreenPhotoBinding = LayoutFullscreenPhotoBinding.inflate(
                    layoutInflater, parent, false
            );
            return new PhotoFullscreenViewHolder(fullscreenPhotoBinding, photoList);
        }

        return new PhotoViewHolder(null, photoList);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Photo photo = photoList.get(position);
        if(mode == THUMBNAIL_MODE){
            LayoutPhotoThumbnailBinding binding =
                    ((LayoutPhotoThumbnailBinding)holder.getBinding());
            binding.setPhoto(photo);
            binding.executePendingBindings();
        }
        else if(mode == FULLSCREEN_MODE){
            LayoutFullscreenPhotoBinding binding =
                    ((LayoutFullscreenPhotoBinding)holder.getBinding());
            binding.setPhoto(photo);
            binding.executePendingBindings();
        }
    }

    @Override
    public int getItemCount() {
        return photoList.size();
    }
}
