package com.example.model.photos;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ObservableArrayList;
import androidx.recyclerview.widget.RecyclerView;

import com.example.view.R;
import com.example.view.databinding.LayoutFullscreenPhotoBinding;
import com.example.view.databinding.LayoutPhotoThumbnailBinding;

import java.util.ArrayList;

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
        // mode == FULLSCREEN_MODE
        else {
            LayoutFullscreenPhotoBinding fullscreenPhotoBinding = LayoutFullscreenPhotoBinding.inflate(
                    layoutInflater, parent, false
            );
            return new PhotoFullscreenViewHolder(fullscreenPhotoBinding, photoList);
        }
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
//            binding.imageView.setImage(ImageSource.uri(photo.getPath()));
            binding.executePendingBindings();
        }
    }



    @Override
    public int getItemCount() {
        return photoList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setPhotoList(PhotoList photoList) {
        this.photoList = new PhotoList((ObservableArrayList<Photo>) photoList.getPhotoList().clone());
        notifyDataSetChanged();
    }
}
