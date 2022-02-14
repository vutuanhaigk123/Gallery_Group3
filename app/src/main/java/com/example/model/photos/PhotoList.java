package com.example.model.photos;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import androidx.databinding.ObservableArrayList;
import androidx.loader.content.CursorLoader;

public class PhotoList {
    private static ObservableArrayList<Photo> photoList;

    public static ObservableArrayList<Photo> getPhotoList() {
        return photoList;
    }

    public static ObservableArrayList<Photo> readMediaStore(Context context){
        ObservableArrayList<Photo> photoList = null;
        String[] projection = {
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.DATE_ADDED,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.DATA
        };
        CursorLoader loader = new CursorLoader( context,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int index = 0;
        if(cursor != null){
            photoList = new ObservableArrayList<Photo>();
            while(cursor.moveToNext()){
                photoList.add(new Photo(cursor.getString(3),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(0), index)
                );
                index++;
            }
            cursor.close();
        }
        PhotoList.photoList = photoList;
        return photoList;
    }

    public static Photo get(int index){
        return photoList.get(index);
    }
}
