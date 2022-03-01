package com.example.view;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.view.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private ProgressBar pgBar;
    private ActivityMainBinding binding;
    private AlbumsFragment albumsFragment;
    private PhotosFragment photosFragment;
    private SearchFragment searchFragment;
    private int currentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
        addControls();
        addEvents();
        requestReadPermission();
        replaceFragment(photosFragment, R.id.btnPhotos);
    }

    private void addEvents() {
        binding.bottomNavigationView2.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int id = item.getItemId();
                        if(id != currentButton){
                            switch (id){
                                case R.id.btnPhotos:
                                    replaceFragment(photosFragment, R.id.btnPhotos);
                                    break;
                                case R.id.btnAlbums:
                                    replaceFragment(albumsFragment, R.id.btnAlbums);
                                    break;
                                case R.id.btnSearch:
                                    replaceFragment(searchFragment, R.id.btnSearch);
                                    break;
                            }
                        }


                        return true;
                    }
                }
        );
    }

    private void replaceFragment(Fragment f, int currentButtonNav){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.constraint_layout, f);
        fragmentTransaction.commit();
        currentButton = currentButtonNav;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(checkReadPermission() == PackageManager.PERMISSION_GRANTED){
            replaceFragment(photosFragment, R.id.btnPhotos);
            getAlbum();
        }
    }

    private void requestReadPermission(){
        final int READ_EXTERNAL_STORAGE_CODE = 101;
        if(checkReadPermission() != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_CODE);
        } else {
            getAlbum();
        }

    }

    private int checkReadPermission(){
        return ContextCompat.checkSelfPermission(
                MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private void addControls(){
        pgBar = findViewById(R.id.pgBar);
        albumsFragment = new AlbumsFragment();
        photosFragment = new PhotosFragment();
        searchFragment = new SearchFragment();
    }

    private void getAlbum(){

        pgBar.setVisibility(View.GONE);
    }
}