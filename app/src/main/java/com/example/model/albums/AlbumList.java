package com.example.model.albums;

import android.database.Cursor;

import androidx.databinding.ObservableArrayList;

import com.example.view.MainActivity;

import java.io.Serializable;

public class AlbumList implements Serializable {

    private ObservableArrayList<Album> albumList;

    public AlbumList(ObservableArrayList<Album> clone) {
        this.albumList = clone;
    }

    public ObservableArrayList<Album> getAlbumList() {
        return albumList;
    }
    public int size(){
        return albumList.size();
    }
    public Album get(int index){
        return albumList.get(index);
    }
    public static ObservableArrayList<Album> readAlbumList(){
        ObservableArrayList<Album> albumList = new ObservableArrayList<>();
        Cursor cursor = MainActivity.database.query("albums",
                null,null,null,null,null,null);
        int index = 0;
        while(cursor.moveToNext()){
            int id_album = cursor.getInt(0);
            String name_album = cursor.getString(1);
            String pwd_album = cursor.getString(2);
            Album album = new Album(id_album, name_album, pwd_album,index);
            System.out.println(id_album + " " + name_album + " " + pwd_album);
            albumList.add(album);
            index+=1;
        }
        cursor.close();
        return albumList;
    }
}
