package com.example.model.photos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import androidx.databinding.ObservableArrayList;
import androidx.loader.content.CursorLoader;

import com.example.model.albums.AlbumRoute;
import com.example.view.MainActivity;

import java.io.Serializable;

public class PhotoList implements Serializable {
    private ObservableArrayList<Photo> photoList;

    public ObservableArrayList<Photo> getPhotoList() {
        return photoList;
    }

    public PhotoList(ObservableArrayList<Photo> photoList) {
        this.photoList = photoList;
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
                if(cursor.getString(1) == null){
                    continue;
                }
                photoList.add(new Photo(cursor.getString(3),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(0), index)
                );
                index++;
            }
            cursor.close();
        }
        return photoList;
    }

    public static ObservableArrayList<Photo> readSceenshotPhotos(Context context){
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
                if(cursor.getString(1) == null){
                    continue;
                }
                String path = cursor.getString(3);
                String dateAdded = cursor.getString(1);
                String mimeType = cursor.getString(2);
                String filename = cursor.getString(0);

                if(path.contains("Screenshots")){
                    ContentValues cv = new ContentValues();
                    cv.put("id", (Integer) null);
                    cv.put("path",path);
                    cv.put("mimeType",mimeType);
                    cv.put("filename",filename);
                    cv.put("dateAdded",dateAdded);
                    cv.put("pwd", (String) null);
                    MainActivity.database.insert("photos",null,cv);
                    int id = AlbumRoute.findIdByNamePhotos(filename);
                    if(id != - 1){
                        AlbumRoute.addPhotoToAlbum(id,3);
//                        cv = new ContentValues();
//                        cv.put("id_album", 3);
//                        cv.put("id_photo",id);
//                        MainActivity.database.insert("album_photo",null,cv);
                    }
                    photoList.add(new Photo(cursor.getString(3),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getString(0), index)
                    );
                    index++;
                }

            }
            cursor.close();
        }
        return photoList;
    }
    public int size(){
        return photoList.size();
    }

    public Photo get(int index){
        return photoList.get(index);
    }
}
