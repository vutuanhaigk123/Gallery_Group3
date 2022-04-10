package com.example.model.albums;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.databinding.ObservableArrayList;

import com.example.model.photos.Photo;
import com.example.model.photos.PhotoList;
import com.example.view.MainActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class AlbumRoute {
    public static int ID_ALBUM_FAVORITE = 1;
    public static int ID_ALBUM_DELETED = 2;
    public static int ID_ALBUM_SCREENSHOT = 3;

    public static int findIdByNamePhotos(String name){
        String[] selections = {"id"};
        String whereCondition = "filename = ?";
        String[] whereConditionArgs = {name};
        Cursor cursor = MainActivity.database.query("photos",
                selections, whereCondition, whereConditionArgs,
                null,null,null);
        int id = -1;
        if(cursor.moveToNext()){
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }

    public static int findIdByPhotoPath(String path){
        String[] selections = {"id"};
        String whereCondition = "path = ?";
        String[] whereConditionArgs = {path};
        Cursor cursor = MainActivity.database.query("photos",
                selections, whereCondition, whereConditionArgs,
                null,null,null);
        int id = -1;
        if(cursor.moveToNext()){
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }

    public static int findIdByNameAlbum(String name){
        String[] selections = {"id"};
        String whereCondition = "name = ?";
        String[] whereConditionArgs = {name};
        ArrayList<String> albums = new ArrayList<>();
        Cursor cursor = MainActivity.database.query("albums",
                selections, whereCondition, whereConditionArgs,
                null,null,null);
        int id = -1;
        if(cursor.moveToNext()){
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }
    public static int getFirstPhotoInAlbum(int id_album){

        Cursor cursor = MainActivity.database
                .rawQuery("select  * from album_photo " +
                        "where id_album = " + id_album, null);
        int id = -1;
        if(cursor.moveToNext()){
            cursor.moveToLast();
            id = cursor.getInt(1);
//            int idAlbum = cursor.getInt(0);
//            int idPhoto = cursor.getInt(1);
//            if(idAlbum == id_album){
//                id = idPhoto;
//            }
        }
        cursor.close();
        return  id;
    }
    public static int getNumberOfPhotoInAlbum(int id_album){
        int result = 0;
        Cursor cursor = MainActivity.database.

                rawQuery("select * from album_photo where id_album = ?",
                        new String[]{String.valueOf(id_album)});
        result = cursor.getCount();
        return result;
    }
    public static Photo getPhotoByIdAndAddIndex(int id_photo,int id_album){
        Photo photo;
        Cursor cursor = MainActivity.database.
                rawQuery("select * from photos where id = ?",
                        new String[] {String.valueOf(id_photo)});
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
                rawQuery("select * from photos where id = ?",
                        new String[] {String.valueOf(id_photo)});
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
//        int index = 0;
//        while(cursor.moveToNext()){
//            int idAlbum = cursor.getInt(0);
//            int idPhoto = cursor.getInt(1);
//            if(idAlbum == id){
//                listIdPhoto.add(getPhotoByIdAndAddIndex(idPhoto, index++));
//            }
//        }
        if(id==3){
            int index = 0;
            while(cursor.moveToNext()){
                int idAlbum = cursor.getInt(0);
                int idPhoto = cursor.getInt(1);
                if(idAlbum == id){
                    listIdPhoto.add(getPhotoByIdAndAddIndex(idPhoto, index++));
                }
            }
        }
        else{
            int index = getNumberOfPhotoInAlbum(id) - 1;
            while(cursor.moveToNext()){
                int idAlbum = cursor.getInt(0);
                int idPhoto = cursor.getInt(1);
                if(idAlbum == id){
                    listIdPhoto.add(getPhotoByIdAndAddIndex(idPhoto, index--));
                }
            }
            Collections.reverse(listIdPhoto);
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
        cv.put("dateAdded",photo.getMilliseconds());
        cv.put("pwd", (String) null);
        MainActivity.database.insert("photos",null,cv);
    }
    public static boolean isPhotoInAlbum(int id_photo, int id_album){
        Cursor c = MainActivity.database.rawQuery(
                "SELECT count(*) FROM album_photo " +
                        "where id_photo = " + id_photo + " and id_album = " + id_album,
                null);

        if(c.moveToFirst()){
            if(c.getInt(0) != 0){
                c.close();
                return true;
            }
//            do{
//                int idAlbum = c.getInt(0);
//                int idPhoto = c.getInt(1);
//                if(idAlbum == id_album && idPhoto == id_photo){
//                    return true;
//                }
//
//            }while(c.moveToNext());
        }
        c.close();
        return false;
    }

    public static void addPasswordOfAlbum(int id_album, String hashPassword){
        ContentValues updValues = new ContentValues();
        updValues.put("pwd", hashPassword);

        int recAffected = MainActivity.database.update( "albums", updValues,
                "id = ?", new String[]{String.valueOf(id_album)} );

    }

    public static String getPassword(int id_album) {
        Cursor cursor = MainActivity.database
                .rawQuery("Select pwd from albums where id = ?",
                        new String[]{String.valueOf(id_album)});
        cursor.moveToFirst();
        return cursor.getString(0);
    }

    public static int deleteAlbum(int id_album) {
        int recAffected = MainActivity.database.delete("albums",
                "id = ?",
                new String[]{String.valueOf(id_album)});

        PhotoList photoList = getPhotoListByAlbum(id_album);

        if(photoList.size() != 0) {
            int recAffected2 = MainActivity.database.delete("album_photo",
                    "id_album = ?",
                    new String[]{String.valueOf(id_album)});
        }
        return recAffected;
    }
    public static void removePhotoInAlbum(int id_photo, int id_album){
        MainActivity.database.delete("album_photo",
                "id_album = ? and id_photo = ?",
                new String[]{String.valueOf(id_album),String.valueOf(id_photo)});
    }
}
