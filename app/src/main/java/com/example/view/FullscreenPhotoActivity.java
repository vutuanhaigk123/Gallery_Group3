package com.example.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.ObservableArrayList;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.example.model.albums.Album;
import com.example.model.albums.AlbumRoute;
import com.example.model.albums.CustomAlbumDialogAdapter;
import com.example.model.albums.SingleAlbumCustom;
import com.example.model.photos.Photo;
import com.example.model.photos.PhotoAdapter;
import com.example.model.photos.PhotoList;

import com.example.view.databinding.ActivityFullscreenPhotoBinding;
import com.example.view.databinding.LayoutInfomationImageBinding;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class FullscreenPhotoActivity extends AppCompatActivity {

    private ActivityFullscreenPhotoBinding binding;
    private PhotoAdapter photoAdapter;
    public static ActionBar actionBar;
    private PhotoList photoList;
    private int newImageIndex;
    public static int EDIT_PHOTO_CODE = 202;


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
        newImageIndex = -1;


        binding.viewPager.setAdapter(photoAdapter);
        jumpToPosition(pos);
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
            case R.id.mnuEdit2:
                editImage2();
                break;
//            case R.id.mnuDel:
//                delImage();
//                break;
//            case R.id.mnuRename:
//                //renameImage();
//                break;
            case R.id.mnuLike:
                addToFavoriteAlbum();
                break;
            case R.id.mnuAddToAlbum:
                addToAlbum();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void editImage2() {
        Intent dsPhotoEditorIntent = new Intent(this, DsPhotoEditorActivity.class);
        dsPhotoEditorIntent.setData(getCurrentPhoto().getUri(this));
        dsPhotoEditorIntent.putExtras(new Bundle());
        startActivityForResult(dsPhotoEditorIntent, EDIT_PHOTO_CODE);
    }

    private void infoImage() {

        Photo photo = getCurrentPhoto();
        String date = photoAdapter.getPhotoList().get(binding.viewPager.getCurrentItem()).getMilliseconds();

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(date) * 1000);
        date = formatter.format(calendar.getTime());

        LayoutInfomationImageBinding layoutInfomationImageBinding = LayoutInfomationImageBinding.inflate(getLayoutInflater());
        Dialog dialog = new Dialog(binding.getRoot().getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);

        layoutInfomationImageBinding.typeImage.setText(photoAdapter.getPhotoList().get(binding.viewPager.getCurrentItem()).getMimeType());
        layoutInfomationImageBinding.filenameImage.setText(photoAdapter.getPhotoList().get(binding.viewPager.getCurrentItem()).getFilename());
        layoutInfomationImageBinding.dateImage.setText(date);



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
            checkImageAfterCrop(resultCode, data);

        } else if(requestCode == EDIT_PHOTO_CODE){
            checkImageAfterEdit(resultCode);
        }
    }

    private void checkImageAfterCrop(int resultCode, @Nullable Intent data){
        if(resultCode == RESULT_OK && data != null){
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

            Photo photo = new Photo(resultUri.toString(),strDate,"img/png",split[split.length - 1],photoList.size());

            //thêm ảnh sau chỉnh sửa vào list
            photoList.getPhotoList().add(0, photo);//thêm vào đầu
            photoAdapter.notifyDataSetChanged();
            newImageIndex = 0;
            try {
                saveToGallery(resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(photo == null){
                Toast.makeText(this, "photo is null", Toast.LENGTH_SHORT).show();

            }
        } else if (resultCode == UCrop.RESULT_ERROR){
            final Throwable cropError = UCrop.getError(data);
        }
    }

    private void checkImageAfterEdit(int resultCode){
        if(resultCode == RESULT_OK){
            newImageIndex = 0;
            Toast.makeText(this, "Photo was edited", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateAdapterData() {
        Intent intent = getIntent();
        photoList = (PhotoList) intent.getSerializableExtra("photoList");
        photoAdapter.setPhotoList(photoList);
        Log.d("debug=", photoList.size() + "");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        updateAdapterData();
        if(newImageIndex != -1){
            jumpToPosition(newImageIndex);
            newImageIndex = -1;
        }
    }

    private void jumpToPosition(int pos){
        binding.viewPager.post(new Runnable() {
            @Override
            public void run() {
                binding.viewPager.setCurrentItem(pos);
            }
        });
    }
    public static int deleteFileFromMediaStore(final ContentResolver contentResolver, final File file) {
        String canonicalPath;
        try {
            canonicalPath = file.getCanonicalPath();
        } catch (IOException e) {
            canonicalPath = file.getAbsolutePath();
        }
        final Uri uri = MediaStore.Files.getContentUri("external");
        final int result = contentResolver.delete(uri,
                MediaStore.Files.FileColumns.DATA + "=?", new String[]{canonicalPath});
        if (result == 0) {
            final String absolutePath = file.getAbsolutePath();
            if (!absolutePath.equals(canonicalPath)) {
                int deletedRow = contentResolver.delete(uri,
                        MediaStore.Files.FileColumns.DATA + "=?", new String[]{absolutePath});
                return deletedRow;
            }
        } else return result;
        return result;
    }
    private void delImage(){
        final AlertDialog.Builder deleteDialog = new AlertDialog.Builder(FullscreenPhotoActivity.this);
        deleteDialog.setTitle("Xóa ảnh");
        deleteDialog.setMessage("Bạn có chắc chắn muốn xóa ảnh này không?");
        deleteDialog.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Photo current = getCurrentPhoto();
                String path = current.getPath();
                File file = new File(path);
                Toast.makeText(FullscreenPhotoActivity.this, deleteFileFromMediaStore(getContentResolver(), file) + "", Toast.LENGTH_SHORT).show();

//                File file = new File(path);
//                Toast.makeText(FullscreenPhotoActivity.this, file.getPath(), Toast.LENGTH_SHORT).show();
//                if(file.delete())
//                    Toast.makeText(FullscreenPhotoActivity.this, "Đã xóa!", Toast.LENGTH_SHORT).show();
//                else Toast.makeText(FullscreenPhotoActivity.this, "Xóa thất bại!", Toast.LENGTH_SHORT).show();
//                Context context = FullscreenPhotoActivity.this;
//                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(path))));

            }
        });
        deleteDialog.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        deleteDialog.show();
    }
    private void addToFavoriteAlbum(){
        Photo photo = getCurrentPhoto();
        int id_album = 1;//id_album yeu thisch = 1
        int id_photo = AlbumRoute.findIdByNamePhotos(photo.getFilename());
        if(id_photo == -1){
            AlbumRoute.addToPhoto(photo);// thêm photo vào bảng photos trước khi đưa vào album_photo
            AlbumRoute.addPhotoToAlbum(AlbumRoute.findIdByNamePhotos(photo.getFilename())
                    ,id_album);
            Toast.makeText(FullscreenPhotoActivity.this, "Đã yêu thích ", Toast.LENGTH_SHORT).show();
        }
        else{
            boolean isPhotoInAlbum = AlbumRoute.isPhotoInAlbum(id_photo,id_album);
            if(isPhotoInAlbum == true){
                Toast.makeText(FullscreenPhotoActivity.this, "Đã tồn tại", Toast.LENGTH_SHORT).show();
            }
            else {
                AlbumRoute.addPhotoToAlbum(AlbumRoute.findIdByNamePhotos(photo.getFilename())
                        ,id_album);
                Toast.makeText(FullscreenPhotoActivity.this, "Đã yêu thích ", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private ObservableArrayList<SingleAlbumCustom> getInfoAlbum(){
        ObservableArrayList<SingleAlbumCustom> result = new ObservableArrayList<>();
        ObservableArrayList<Album> albums = AlbumRoute.getAlbumList();
        int index = 0;
        for(int i = 0; i < albums.size(); i++){
            int id_album = albums.get(i).getId();

            int quan = AlbumRoute.getNumberOfPhotoInAlbum(id_album);
            int id_photo = AlbumRoute.getFirstPhotoInAlbum(id_album);
            Photo photo = AlbumRoute.getPhotoById(id_photo);
            Uri uri;
            if(photo != null ){
                uri = photo.getUri(this);
            }
            else uri = null;
            if(id_album!=2 && id_album != 1 ){
                SingleAlbumCustom albumCustom = new SingleAlbumCustom(albums.get(i).getName(),uri,quan, index++);
                result.add(albumCustom);
            }
        }
        return result;
    }
    private void openAlbumDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.custom_album_listview,null);
        ListView lvAlbum = (ListView) row.findViewById(R.id.lvAlbumCustom);
        Button btnCancel = (Button) row.findViewById(R.id.btnCancelAddAlbum);
        ObservableArrayList<SingleAlbumCustom> infoAlbum = getInfoAlbum();
        CustomAlbumDialogAdapter adapter = new CustomAlbumDialogAdapter(this,infoAlbum,R.layout.row_album);
        lvAlbum.setAdapter(adapter);
        builder.setView(row);
        AlertDialog dialog = builder.create();
        dialog.show();
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        lvAlbum.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(FullscreenPhotoActivity.this, infoAlbum.get(i).getName(), Toast.LENGTH_SHORT).show();
                String name_album = infoAlbum.get(i).getName();
                int id_album = AlbumRoute.findIdByNameAlbum(name_album);
                Photo photo = getCurrentPhoto();
                int id_photo = AlbumRoute.findIdByNamePhotos(photo.getFilename());
                if(id_photo == -1){
                    AlbumRoute.addToPhoto(photo);// thêm photo vào bảng photos trước khi đưa vào album_photo
                    AlbumRoute.addPhotoToAlbum(AlbumRoute.findIdByNamePhotos(photo.getFilename())
                            ,id_album);
                    Toast.makeText(FullscreenPhotoActivity.this, "Đã thêm vào " + name_album, Toast.LENGTH_SHORT).show();
                }
                else{
                    boolean isPhotoInAlbum = AlbumRoute.isPhotoInAlbum(id_photo,id_album);
                    if(isPhotoInAlbum == true){
                        Toast.makeText(FullscreenPhotoActivity.this, "Đã tồn tại trong " + name_album, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        AlbumRoute.addPhotoToAlbum(AlbumRoute.findIdByNamePhotos(photo.getFilename())
                                ,id_album);
                        Toast.makeText(FullscreenPhotoActivity.this, "Đã thêm vào " + name_album, Toast.LENGTH_SHORT).show();
                    }
                }
                dialog.dismiss();
            }
        });
    }
    private void addToAlbum(){
        openAlbumDialog();
    }
}