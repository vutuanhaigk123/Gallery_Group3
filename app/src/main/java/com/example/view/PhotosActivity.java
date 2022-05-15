package com.example.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.model.albums.AlbumAdapter;
import com.example.model.albums.AlbumList;
import com.example.model.albums.AlbumRoute;
import com.example.model.photos.Photo;
import com.example.model.photos.PhotoAdapter;
import com.example.model.photos.PhotoList;
import com.example.model.photos.PhotoSortByDateAdapter;
import com.example.view.databinding.ActivityPhotosBinding;
import com.example.view.databinding.LayoutSetPasswordBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Collections;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class PhotosActivity extends AppCompatActivity {

    private ActivityPhotosBinding binding;
    private PhotoList photoList;
    public static PhotoSortByDateAdapter photoSortByAdapter;
    public static final int LAYOUT_SORT_BY_DATE = 0;
    public static final int LAYOUT_SORT_BY_MONTH = 1;
    public static final int LAYOUT_SORT_BY_YEAR = 2;
    private int currentLayout;

    public static boolean isAlbum = false;
    public static String nameOfAlbum;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);
        this.binding = ActivityPhotosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        photoList = (PhotoList)  intent.getSerializableExtra("photoListOfAlbum");
        nameOfAlbum = intent.getStringExtra("nameOfAlbum");
        isAlbum = intent.getBooleanExtra("isAlbum", false);
        if(photoList.getPhotoList().size() == 0){
            binding.noPhotos.setVisibility(View.VISIBLE);
        }
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
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(nameOfAlbum);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        if(isAlbum) {
            inflater.inflate(R.menu.option_menu_album_in_layout,menu);
        }
        else
            inflater.inflate(R.menu.option_menu_photos,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int layout = -1;
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.photosmnu_sortByDate:
                layout = PhotosFragment.LAYOUT_SORT_BY_DATE;
                break;
            case R.id.photosmnu_sortByMonth:
                layout = PhotosFragment.LAYOUT_SORT_BY_MONTH;
                break;
            case R.id.photosmnu_sortByYear:
                layout = PhotosFragment.LAYOUT_SORT_BY_YEAR;
                break;
            case R.id.album_set_password:
                setPassword();
                break;
            case R.id.album_delete:
                deleteAlbum();
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

    private void deleteAlbum() {
        final AlertDialog.Builder deleteDialog = new AlertDialog.Builder(binding.getRoot().getContext());
        deleteDialog.setTitle("Xóa album");
        deleteDialog.setMessage("Bạn có chắc chắn muốn xóa album này không?");
        deleteDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int id = AlbumRoute.findIdByNameAlbum(nameOfAlbum);
                if (id <= 3){
                    Toast.makeText(PhotosActivity.this, "Không thể xóa album mặc định", Toast.LENGTH_SHORT).show();
                    return;
                }
                int check = AlbumRoute.deleteAlbum(id);
                if(check > 0 ){
                    Toast.makeText(binding.getRoot().getContext(),
                            "Xoá thành công",
                            Toast.LENGTH_SHORT).show();
                    AlbumAdapter.albumList = new AlbumList(AlbumList.readAlbumList());
                    AlbumsFragment.adapter.setAlbumList(AlbumAdapter.albumList);
                    finish();
                }
                else {
                    Toast.makeText(binding.getRoot().getContext(),
                            "Xoá thất bại",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteDialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        deleteDialog.show();
    }

    private void setPassword() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(binding.getRoot().getContext());
        builder.setTitle("Create Password");
        LayoutSetPasswordBinding layoutSetPasswordBinding = LayoutSetPasswordBinding.inflate(getLayoutInflater());
        EditText confirmPassword = layoutSetPasswordBinding.confirmPasswordAlbum;
        EditText password = layoutSetPasswordBinding.passwordAlbum;
        builder.setView(layoutSetPasswordBinding.getRoot());
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if( password.getText().toString().equals(confirmPassword.getText().toString())) {
                    String bcryptHashString = BCrypt.withDefaults().hashToString(12, password.getText().toString().toCharArray());
                    AlbumRoute.addPasswordOfAlbum(AlbumRoute.findIdByNameAlbum(nameOfAlbum), bcryptHashString);
                    Toast.makeText(binding.getRoot().getContext(), "Password created success", Toast.LENGTH_SHORT).show();

                    System.out.println(bcryptHashString);
                    String s = AlbumRoute.getPassword(AlbumRoute.findIdByNameAlbum(nameOfAlbum));
                    System.out.println(s);
                }
                else
                    Toast.makeText(binding.getRoot().getContext(), "Password does not match the confirm password", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }
}