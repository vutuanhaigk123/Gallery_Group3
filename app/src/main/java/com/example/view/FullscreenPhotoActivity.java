package com.example.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.model.photos.Photo;
import com.example.model.photos.PhotoAdapter;
import com.example.model.photos.PhotoList;

import com.example.view.databinding.ActivityFullscreenPhotoBinding;

public class FullscreenPhotoActivity extends AppCompatActivity {

    private ActivityFullscreenPhotoBinding binding;
    private PhotoAdapter photoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFullscreenPhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        int pos = intent.getIntExtra("pos", 0);
        PhotoList photoList = (PhotoList) intent.getSerializableExtra("photoList");
        photoAdapter = new PhotoAdapter( photoList,
                PhotoAdapter.FULLSCREEN_MODE);
        binding.viewPager.setAdapter(photoAdapter);
        binding.viewPager.post(new Runnable() {
            @Override
            public void run() {
                binding.viewPager.setCurrentItem(pos);
            }
        });
    }

    // Option Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.mnuShare:
                shareImage();
                break;
            case R.id.mnuSetImage:
                setImageAs();
                break;
            case R.id.mnuCopy:
                copyToClipboard();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void copyToClipboard() {
        ClipboardManager clipboard = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);
        Photo currentPhoto = getCurrentPhoto();
        ClipData clip = ClipData.newUri(getContentResolver(),
                currentPhoto.getFilename(),
                currentPhoto.getUri(FullscreenPhotoActivity.this));
        clipboard.setPrimaryClip(clip);
        Toast.makeText(FullscreenPhotoActivity.this,
                "Copy to clipboard successfully", Toast.LENGTH_SHORT).show();
    }

    private Photo getCurrentPhoto(){
        return photoAdapter.getPhotoList().get(binding.viewPager.getCurrentItem());
    }

    private void shareImage(){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        Photo currentPhoto = getCurrentPhoto();
        shareIntent.putExtra(Intent.EXTRA_STREAM, currentPhoto.getUri(FullscreenPhotoActivity.this));
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, "Share your image"));
    }

    private void setImageAs(){
        Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
        Photo currentPhoto = getCurrentPhoto();
        intent.setDataAndType( currentPhoto.getUri(FullscreenPhotoActivity.this), "image/*");
        intent.putExtra("mimeType", "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(
                intent, "Set image as:"));
    }

}