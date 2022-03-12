package com.example.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.model.photos.Photo;
import com.example.model.photos.PhotoAdapter;
import com.example.model.photos.PhotoList;

import com.example.view.databinding.ActivityFullscreenPhotoBinding;
import com.example.view.databinding.LayoutInfomationImageBinding;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.zip.Inflater;

public class FullscreenPhotoActivity extends AppCompatActivity {

    private ActivityFullscreenPhotoBinding binding;
    private PhotoAdapter photoAdapter;
    public static ActionBar actionBar;
    private PhotoList photoList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.makeFullScreen(getWindow());
        binding = ActivityFullscreenPhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        int pos = intent.getIntExtra("pos", 0);
        photoList = (PhotoList) intent.getSerializableExtra("photoList");
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
                break;
            case R.id.mnuInfo:
                infoImage();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void infoImage() {

        Photo photo = getCurrentPhoto();

        LayoutInfomationImageBinding layoutInfomationImageBinding = LayoutInfomationImageBinding.inflate(getLayoutInflater());
        Dialog dialog = new Dialog(binding.getRoot().getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        layoutInfomationImageBinding.typeImage.setText(photoAdapter.getPhotoList().get(binding.viewPager.getCurrentItem()).getMimeType());
        layoutInfomationImageBinding.filenameImage.setText(photoAdapter.getPhotoList().get(binding.viewPager.getCurrentItem()).getFilename());
        layoutInfomationImageBinding.dateImage.setText(photoAdapter.getPhotoList().get(binding.viewPager.getCurrentItem()).getDateAdded());



        dialog.setContentView(layoutInfomationImageBinding.getRoot());

        dialog.show();
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
        Photo currentPhoto = getCurrentPhoto();
        Uri result  = currentPhoto.getUri(FullscreenPhotoActivity.this);
        String dest_url = new StringBuilder(UUID.randomUUID().toString()).append(".png").toString();
        UCrop.Options options = new UCrop.Options();
        UCrop.of(result, Uri.fromFile(new File(getCacheDir(),dest_url)))
                .withOptions(options).withAspectRatio(0,0)
                .withMaxResultSize(2000,2000).start(FullscreenPhotoActivity.this);
    }
    private void saveToGallery(Uri uri) throws IOException {


        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "newImg" , "After Edit");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode==UCrop.REQUEST_CROP &&data!=null){
            Uri resultUri = UCrop.getOutput(data);
            String []split = resultUri.toString().split("/");
            String filename = "";//lấy tên file
            for(int i = 0;i<split.length - 1;i++){
                filename+=split[i] + "/";
            }
            // lấy ngày hiện tại để lưu ảnh
            Date date = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String strDate = formatter.format(date);
            Toast.makeText(this, strDate, Toast.LENGTH_SHORT).show();

            Photo photo = new Photo(resultUri.toString(),"08/03/2022","img/png",split[split.length - 1],photoList.size());

            //thêm ảnh sau chỉnh sửa vào list
            photoList.getPhotoList().add(0, photo);//thêm vào đầu
            photoAdapter.notifyDataSetChanged();
            binding.viewPager.post(new Runnable() {
                @Override
                public void run() {
                    binding.viewPager.setCurrentItem(0 );
                }//hiển thị ảnh đầu
            });
            try {
                saveToGallery(resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(photo == null){
                Toast.makeText(this, "photo is null", Toast.LENGTH_SHORT).show();

            }

        }
        else if(resultCode == UCrop.RESULT_ERROR){
            final Throwable cropError = UCrop.getError(data);
        }
    }


}