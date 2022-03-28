package com.example.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.model.photos.Photo;
import com.sealstudios.multiimageview.MultiImageView;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class CollageImage extends AppCompatActivity {
    private Context context;
    private TextView txtMsg;
    private ViewGroup scrollViewgroup;
    private ImageView icon;
    private TextView caption;
    private int numOfImg;
    private View currentFrame;
    private ActionBar actionBar;
    public static MultiImageView multiImageView;
    private ArrayList<Photo> selectedList;

    String [] items = {"None","Radius","Circle"};
    Integer [][] thumbnails = {{R.drawable.layout_collage_2_1, R.drawable.layout_collage_2_2, R.drawable.layout_collage_2_3},
                                {R.drawable.layout_collage_3_1, R.drawable.layout_collage_3_2, R.drawable.layout_collage_3_3},
                                {R.drawable.layout_collage_4_1, R.drawable.layout_collage_4_2, R.drawable.layout_collage_4_3}};
    Integer [] thumbnails_2 = {R.drawable.layout_collage_2_1, R.drawable.layout_collage_2_2, R.drawable.layout_collage_2_3};
    Integer [] thumbnails_3 = {R.drawable.layout_collage_3_1, R.drawable.layout_collage_3_2, R.drawable.layout_collage_3_3};
    Integer [] thumbnails_4 = {R.drawable.layout_collage_4_1, R.drawable.layout_collage_4_2, R.drawable.layout_collage_4_3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_create_collage_image);
        actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Create collage image");
        }
        multiImageView = (MultiImageView) findViewById(R.id.iv);
        scrollViewgroup = (ViewGroup) findViewById(R.id.viewgroup);
        Intent intentCollageImage = getIntent();
        numOfImg = intentCollageImage.getIntExtra("numOfImg",0);
        selectedList = (ArrayList<Photo>) intentCollageImage.getSerializableExtra("selectedList");
        for(int i =0; i < selectedList.size();i++){
            CollageImage.multiImageView.addImage(BitmapFactory.decodeFile(selectedList.get(i).getPath()));
        }
        showLargeImage(numOfImg);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu_collage_image,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                new AlertDialog.Builder(this)
                        .setTitle("")
                        .setMessage("Are you sure want to leave?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
            case R.id.ci_save:
                multiImageView.buildDrawingCache();
                multiImageView.getDrawingCache();
                saveToGallery(multiImageView.getDrawingCache());
                Toast.makeText(this, "Create collage image successfully", Toast.LENGTH_SHORT).show();
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showLargeImage(int numOfImg) {
        for(int i=0; i< thumbnails[numOfImg-2].length;i++){
            final View singleFrame = getLayoutInflater().inflate(R.layout.frame_icon_caption, null);
            singleFrame.setId(i);
            if(singleFrame.getId() == 0){
                currentFrame = singleFrame;
                singleFrame.setBackgroundColor(Color.YELLOW);
            }
            TextView caption = (TextView) singleFrame.findViewById(R.id.caption);
            ImageView icon = (ImageView) singleFrame.findViewById((R.id.layoutOption));
            icon.setImageResource(thumbnails[numOfImg-2][i]);
            caption.setText(items[i]);
            caption.setBackgroundColor(Color.GRAY);
            scrollViewgroup.addView(singleFrame);
            singleFrame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    currentFrame.setBackgroundColor(Color.DKGRAY);
                    singleFrame.setBackgroundColor(Color.YELLOW);
                    currentFrame = singleFrame;
                    if(singleFrame.getId() == 0){
                        multiImageView.setShape(MultiImageView.Shape.NONE);
                    }
                    if(singleFrame.getId() == 1){
                        multiImageView.setShape(MultiImageView.Shape.RECTANGLE);
                    }
                    if(singleFrame.getId() == 2){
                        multiImageView.setShape(MultiImageView.Shape.CIRCLE);
                    }
                }
            });
        }
    }

    private  void saveToGallery(Bitmap bitmap){
        Uri resultUri = saveToSDCard(bitmap);
        String []split = resultUri.toString().split("/");
        String filename = "";//lấy tên file
        for(int i = 0;i<split.length - 1;i++){
            filename+=split[i] + "/";
        }
        // lấy ngày hiện tại để lưu ảnh
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String strDate = formatter.format(date);
        Photo photo = new Photo(resultUri.toString(),strDate,"img/png",split[split.length - 1],PhotosFragment.photoList.size());
        PhotosFragment.photoList.getPhotoList().add(0, photo);//thêm vào đầu
    }

    private Uri saveToSDCard(Bitmap inImage) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        LocalDateTime now = LocalDateTime.now();
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), inImage, "collageImg" + now.toString(), "New collage image");
        return Uri.parse(path);
    }

//    private void saveToGallery(Drawable drawable){
//        Bitmap bitmap =((BitmapDrawable)drawable).getBitmap();
//        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "newImg" , "After Edit");
//    }
}
