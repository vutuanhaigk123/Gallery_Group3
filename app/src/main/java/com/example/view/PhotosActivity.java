package com.example.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.model.albums.AlbumRoute;
import com.example.model.photos.PhotoAdapter;
import com.example.model.photos.PhotoList;
import com.example.model.photos.PhotoSortByDateAdapter;
import com.example.view.databinding.ActivityPhotosBinding;

import java.util.Collections;

public class PhotosActivity extends AppCompatActivity {

    private ActivityPhotosBinding binding;
    private PhotoList photoList;
    private PhotoSortByDateAdapter photoSortByAdapter;
    public static final int LAYOUT_SORT_BY_DATE = 0;
    public static final int LAYOUT_SORT_BY_MONTH = 1;
    public static final int LAYOUT_SORT_BY_YEAR = 2;
    private int currentLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        this.binding = ActivityPhotosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        photoList = (PhotoList)  intent.getSerializableExtra("photoListOfAlbum");
        String nameOfAlbum = intent.getStringExtra("nameOfAlbum");
        this.binding.recyclerPhotosView.setLayoutManager( new LinearLayoutManager(
                this,
                RecyclerView.VERTICAL,false));
        currentLayout = LAYOUT_SORT_BY_DATE;
        photoSortByAdapter = new PhotoSortByDateAdapter(
                this,
                photoList.getPhotoList(),
                PhotoAdapter.THUMBNAIL_MODE, currentLayout);
        Collections.reverse(PhotoSortByDateAdapter.ogPhotoList.getPhotoList());
        this.binding.recyclerPhotosView.setAdapter(photoSortByAdapter);
        //this.binding.tvNameOfAlbum.setText(nameOfAlbum);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(nameOfAlbum);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.option_menu_photos,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int layout = -1;
        switch (item.getItemId()){
            case R.id.photosmnu_sortByDate:
                layout = PhotosFragment.LAYOUT_SORT_BY_DATE;
                break;
            case R.id.photosmnu_sortByMonth:
                layout = PhotosFragment.LAYOUT_SORT_BY_MONTH;
                break;
            case R.id.photosmnu_sortByYear:
                layout = PhotosFragment.LAYOUT_SORT_BY_YEAR;
                break;
        }

        if(layout != -1 && layout != currentLayout){
            currentLayout = layout;
            photoSortByAdapter = new PhotoSortByDateAdapter(
                    this,
                    photoList.getPhotoList(),
                    PhotoAdapter.THUMBNAIL_MODE, layout);
            binding.recyclerPhotosView.swapAdapter(photoSortByAdapter,false);
            Collections.reverse(PhotoSortByDateAdapter.ogPhotoList.getPhotoList());
        }

        return super.onOptionsItemSelected(item);
    }
}