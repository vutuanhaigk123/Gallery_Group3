package com.example.view;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.model.CropperActivity;
import com.example.model.photos.Photo;
import com.example.model.photos.PhotoAdapter;
import com.example.model.photos.PhotoList;

import com.example.view.databinding.ActivityFullscreenPhotoBinding;

public class FullscreenPhotoActivity extends AppCompatActivity {

    private ActivityFullscreenPhotoBinding binding;
    private PhotoAdapter photoAdapter;
    public static ActionBar actionBar;
//    ActivityResultLauncher<String> mgetContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.makeFullScreen(getWindow());
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
        actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


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
            case android.R.id.home:
                finish();
                return true;
            case R.id.mnuShare:
                shareImage();
                break;
            case R.id.mnuSetImage:
                setImageAs();
                break;
            case R.id.mnuCopy:
                copyToClipboard();
                break;
            case R.id.mnuEdit:
                editImage();
//                mgetContent.launch("image/*");// lay anh
                break;
        }
//        mgetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
//            @Override
//            public void onActivityResult(Uri result) {
//                Intent intent = new Intent(FullscreenPhotoActivity.this, CropperActivity.class);
//                intent.putExtra("DATA",result.toString());
//                startActivityForResult(intent,101);
//            }
//        });
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
    private void editImage(){
        Intent intent = new Intent(FullscreenPhotoActivity.this, CropperActivity.class);
        Photo currentPhoto = getCurrentPhoto();
        Uri result  = currentPhoto.getUri(FullscreenPhotoActivity.this);
        intent.putExtra("DATA",result.toString());
        startActivityForResult(intent,101);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == -1 &&requestCode ==101){
            String result = data.getStringExtra("RESULT");
            Uri resultUri = null;
            if(result != null){
                resultUri = Uri.parse(result);
            }
            //imgHInh.setImageURI(resultUri);
        }
    }

}