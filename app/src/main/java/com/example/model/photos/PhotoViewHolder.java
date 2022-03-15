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

        photoRowBinding.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(!PhotosFragment.isEnable) {
                    ActionMode.Callback callback = new ActionMode.Callback() {
                        @Override
                        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                            MenuInflater menuInflater = actionMode.getMenuInflater();
                            menuInflater.inflate(R.menu.option_menu_long_click, menu);

                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                            PhotosFragment.isEnable = true;
                            ClickItem(photoRowBinding);

                            return true;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                            int id = menuItem.getItemId();

                            switch (id) {
                                case R.id.test:
//                                    System.out.println(PhotosFragment.selectList);
                                    break;
                            }
                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode actionMode) {

                            for (int i =0 ; i < PhotosFragment.binding.recyclerView.getChildCount();i++){
                                View viewItem = PhotosFragment.binding.recyclerView.getLayoutManager().findViewByPosition(i);
                                RecyclerView recyclerView= viewItem.findViewById(R.id.rvPhotoGrid);
                                for (int j = 0 ; j < recyclerView.getChildCount();j++){
                                    recyclerView.getLayoutManager().getChildAt(j).findViewById(R.id.iv_check_box).setVisibility(View.GONE);
                                }
                            }
                            PhotosFragment.isEnable = false;
                            PhotosFragment.selectList.clear();
                        }
                    };
                    ((AppCompatActivity) view.getContext()).startActionMode(callback);
                }
                return true;
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

    private void ClickItem(LayoutPhotoThumbnailBinding binding) {
        String s = Integer.toBinaryString(binding.getPhoto().getIndex());

        if(binding.ivCheckBox.getVisibility() == View.GONE) {
            binding.ivCheckBox.setVisibility(View.VISIBLE);
            PhotosFragment.selectList.add(s);
        }
        else {
            binding.ivCheckBox.setVisibility(View.GONE);
            binding.ivCheckBox.setBackgroundColor(Color.TRANSPARENT);
            PhotosFragment.selectList.remove(s);
        }
    }
}
