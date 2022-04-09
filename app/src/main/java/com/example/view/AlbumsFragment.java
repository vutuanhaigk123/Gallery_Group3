package com.example.view;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.model.albums.Album;
import com.example.model.albums.AlbumAdapter;
import com.example.model.albums.AlbumList;
import com.example.model.photos.PhotoList;
import com.example.view.databinding.FragmentAlbumsBinding;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlbumsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlbumsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static FragmentAlbumsBinding binding;
    private PhotoList screenshotsAlbum;
    private AlbumList albumList;
    public static AlbumAdapter adapter;
    public static boolean isEnable = false;
    public static boolean isSelectAll = false;
    public static ObservableArrayList<Album> selectedList = new ObservableArrayList<>();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AlbumsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlbumsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlbumsFragment newInstance(String param1, String param2) {
        AlbumsFragment fragment = new AlbumsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        screenshotsAlbum = new PhotoList(PhotoList.readSceenshotPhotos(getContext()));
        albumList = new AlbumList(AlbumList.readAlbumList());
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.option_menu_albums,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.mnuAddAlbums:
                addAlbum();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.binding = FragmentAlbumsBinding.inflate(inflater, container, false);
        binding.rvAlbums.setLayoutManager( new LinearLayoutManager(
                container.getContext(),
                RecyclerView.VERTICAL,false));
        adapter = new AlbumAdapter(
                container.getContext(),
                this.albumList.getAlbumList(),
                0, R.layout.layout_album_thumbnail);
        binding.rvAlbums.setLayoutManager(new GridLayoutManager(container.getContext(),3));
        binding.rvAlbums.setAdapter(adapter);

        return binding.getRoot();
    }
    private ArrayList<String> readNameOfAlbums(){
        ArrayList<String>albums = new ArrayList<>();
        Cursor cursor = MainActivity.database.query("albums",
                null,null,null,null,null,null);
        String id = "";
        while(cursor.moveToNext()){
            albums.add(cursor.getString(1) );
            id += cursor.getInt(0) + " ";
        }
        cursor.close();
        return albums;
    }
    private void addAlbum(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Tạo Album");
        final EditText input = new EditText(getContext());
        input.setHint("Tên album");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Tạo", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String name = input.getText().toString();
                ArrayList<String> albumName = readNameOfAlbums();
                if(albumName.contains(name)){
                    Toast.makeText(getContext(), "Album đã tồn tại!", Toast.LENGTH_SHORT).show();
                }
                else{
                    ContentValues cv = new ContentValues();
                    cv.put("id", (Integer) null);
                    cv.put("name",name);
                    cv.put("pwd", (String) null);
                    MainActivity.database.insert("albums",null,cv);
                }
                albumName.clear();
                albumName = readNameOfAlbums();
                albumList = new AlbumList(AlbumList.readAlbumList());

                adapter.setAlbumList(albumList);

            }
        });
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();


    }
}