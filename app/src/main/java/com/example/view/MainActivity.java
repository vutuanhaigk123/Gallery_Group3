package com.example.view;

import static android.os.Build.VERSION.SDK_INT;
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
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.view.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@SuppressWarnings({"Convert2Lambda", "FieldCanBeLocal"})
public class MainActivity extends AppCompatActivity {
    private ProgressBar pgBar;
    private ActivityMainBinding binding;
    private AlbumsFragment albumsFragment;
    private PhotosFragment photosFragment;
    private UserFragment userFragment;
    public static String userEmail;
    private int currentButton;
    private final String DB_NAME = "DBGallery.db";
    private final String DB_PATH = "/databases/";// lưu trữ trong thư mục cài đặt gốc
    public  static SQLiteDatabase database = null;
    public static BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        userEmail = intent.getStringExtra("userEmail");
        //lần đầu thì copy
        copyDatabaseFromAssets();
        database = openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        bottomNavigationView = binding.bottomNavigationView2;
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
        addControls();
        addEvents();
        requestPermissionToManageAllFolder();
        requestReadPermission();
        replaceFragment(photosFragment, R.id.btnPhotos);
    }

    private void requestPermissionToManageAllFolder(){
        if(SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                Snackbar.make(findViewById(android.R.id.content), "Permission needed!", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Settings", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                try {
                                    Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
                                    startActivity(intent);
                                } catch (Exception ex) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                    startActivity(intent);
                                }
                            }
                        })
                        .show();
            }
        }
    }
    private void addEvents() {
        binding.bottomNavigationView2.setOnItemSelectedListener(
                new NavigationBarView.OnItemSelectedListener() {
                    @SuppressLint("NonConstantResourceId")
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        int id = item.getItemId();
                        if(id != currentButton){
                            switch (id){
                                case R.id.btnPhotos:
                                    PhotosActivity.isAlbum = false;
                                    replaceFragment(photosFragment, R.id.btnPhotos);
                                    break;
                                case R.id.btnAlbums:
                                    replaceFragment(albumsFragment, R.id.btnAlbums);
                                    break;
                                case R.id.btnUser:
                                    replaceFragment(userFragment, R.id.btnUser);
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
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.SET_WALLPAPER},
                    READ_EXTERNAL_STORAGE_CODE);
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
        userFragment = UserFragment.newInstance(userEmail);
    }

    private void getAlbum(){

        pgBar.setVisibility(View.GONE);
    }

    public static void makeFullScreen(Window window) {
        // window.requestFeature(Window.FEATURE_NO_TITLE);
        // window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //         WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void copyDatabase(){
        try {
            InputStream inputStream = getAssets().open(DB_NAME);
            String outputFileName = getApplicationInfo().dataDir + DB_PATH + DB_NAME;// nơi lưu trữ
            File file = new File(getApplicationInfo().dataDir + DB_PATH);
            if(!file.exists()){
                file.mkdir();
            }
            OutputStream outputStream = new FileOutputStream(outputFileName);

            //chép dữ liệu
            byte[] buffer = new byte[1024];
            int len;
            while((len = inputStream.read(buffer)) > 0){
                outputStream.write(buffer,0,len);
            }
            outputStream.flush();
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ERROR","Lỗi sao chép");
        }
    }
    private void copyDatabaseFromAssets(){
        File dbFile = getDatabasePath(DB_NAME);
        //nếu db chưa tồn tại thì sao chép vào, không thì thoi.
        if(!dbFile.exists()){
            copyDatabase();
            Toast.makeText(this, "Successfull copy!", Toast.LENGTH_SHORT).show();
        }
//        else{
//            dbFile.delete();
//            copyDatabase();
//        }
    }
}