package com.example.model.albums;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.databinding.ObservableArrayList;

import com.example.model.photos.Photo;
import com.example.model.photos.PhotoList;
import com.example.view.MainActivity;

import java.util.ArrayList;

public class AlbumRoute {
    public static int findIdByNamePhotos(String name){
        ArrayList<String> albums = new ArrayList<>();
        Cursor cursor = MainActivity.database.query("photos",
                null,null,null,null,null,null);
        while(cursor.moveToNext()){
            if(name.equals(cursor.getString(2)))
            return cursor.getInt(0);
        }
        cursor.close();
        return -1;
    }
    public static int findIdByNameAlbum(String name){
        ArrayList<String> albums = new ArrayList<>();
        Cursor cursor = MainActivity.database.query("albums",
                null,null,null,null,null,null);
        while(cursor.moveToNext()){
            if(name.equals(cursor.getString(1)))
                return cursor.getInt(0);
        }
        cursor.close();
        return -1;
    }
    public static int getFirstPhotoInAlbum(int id_album){

        Cursor cursor = MainActivity.database.rawQuery("select  * from album_photo", null);
        int id = -1;
        while(cursor.moveToNext()){
            int idAlbum = cursor.getInt(0);
            int idPhoto = cursor.getInt(1);
            if(idAlbum == id_album){
                id = idPhoto;
            }
        }
        cursor.close();
        return  id;
    }
    public static Photo getPhotoByIdAndAddIndex(int id_photo,int id_album){
        Photo photo;
        Cursor cursor = MainActivity.database.

                rawQuery("select * from photos where id = ?", new String[]{String.valueOf(id_photo)});
        cursor.moveToNext();
        String path = cursor.getString(1);
        String filename = cursor.getString(2);
        String dateAdded = cursor.getString(3);
        String mimeType = cursor.getString(4);
        photo = new Photo(path,dateAdded,mimeType,filename,id_album);
        cursor.close();
        return  photo;
    }
    public static Photo getPhotoById(int id_photo){
        Photo photo;
        Cursor cursor = MainActivity.database.

                rawQuery("select * from photos where id = ?", new String[]{String.valueOf(id_photo)});
        while(cursor.moveToNext()){
            String path = cursor.getString(1);
            String filename = cursor.getString(2);
            String dateAdded = cursor.getString(3);
            String mimeType = cursor.getString(4);
            photo = new Photo(path,dateAdded,mimeType,filename, -1);
            return  photo;
        }
        cursor.close();
        return null;
    }
    public static ObservableArrayList<Album> getAlbumList(){
        ObservableArrayList<Album> albumList = new ObservableArrayList<>();
        Cursor cursor = MainActivity.database.query("albums",
                null,null,null,null,null,null);
        int index = 0;
        while(cursor.moveToNext()){
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String pwd = cursor.getString(2);
            Album album = new Album(id,name,pwd,index);
            albumList.add(album);
            index++;
        }
        cursor.close();
        return albumList;
    }
    public static PhotoList getPhotoListByAlbum(int id){
        ObservableArrayList<Photo> listIdPhoto = new ObservableArrayList<>();
        Cursor cursor = MainActivity.database.rawQuery("select  * from album_photo", null);

        while(cursor.moveToNext()){
            int idAlbum = cursor.getInt(0);
            int idPhoto = cursor.getInt(1);
            if(idAlbum == id){
                listIdPhoto.add(getPhotoById(idPhoto));

            }
        }
        cursor.close();
        return  new PhotoList(listIdPhoto);
    }
    public static void addPhotoToAlbum(int id_photo, int id_album){
        ContentValues cv = new ContentValues();
        cv.put("id_album", id_album);
        cv.put("id_photo",id_photo);
        MainActivity.database.insert("album_photo",null,cv);
    }
    public static void addToPhoto(Photo photo){
        ContentValues cv = new ContentValues();
        cv.put("id", (Integer) null);
        cv.put("path",photo.getPath());
        cv.put("mimeType",photo.getMimeType());
        cv.put("filename",photo.getFilename());
        cv.put("dateAdded",photo.getDateAdded());
        cv.put("pwd", (String) null);
        MainActivity.database.insert("photos",null,cv);
    }
    public static int getNumberOfPhotoInAlbum(int id_album){
        int result = 0;
        Cursor cursor = MainActivity.database.

                rawQuery("select * from album_photo where id_album = ?", new String[]{String.valueOf(id_album)});
        result = cursor.getCount();
        return result;
    }
    public static boolean isPhotoInAlbum(int id_photo, int id_album){
        Cursor c = MainActivity.database.rawQuery("SELECT * FROM album_photo", null);

        if(c.moveToFirst()){
            do{
                int idAlbum = c.getInt(0);
                int idPhoto = c.getInt(1);
                if(idAlbum == id_album && idPhoto == id_photo){
                    return true;
                }

            }while(c.moveToNext());
        }
        c.close();
        return false;
    }
}
