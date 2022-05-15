package com.example.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableArrayList;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.model.photos.Photo;
import com.example.model.photos.PhotoAdapter;
import com.example.model.photos.PhotoList;
import com.example.model.photos.PhotoSortByDate;
import com.example.model.photos.PhotoSortByDateAdapter;
import com.example.view.databinding.FragmentPhotosBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PhotosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PhotosFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentPhotosBinding binding;
    public static PhotoList photoList;
    public static PhotoSortByDateAdapter photoSortByAdapter;
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReferenceFromUrl("gs://galleryapp-5dd1d.appspot.com");
    private DatabaseReference mData;
    private SharedPreferences sharedPreferences;
    public static final int LAYOUT_SORT_BY_DATE = 0;
    public static final int LAYOUT_SORT_BY_MONTH = 1;
    public static final int LAYOUT_SORT_BY_YEAR = 2;
    private int currentLayout;
    public static SwipeRefreshLayout swipeRefreshLayout;
    public static boolean isEnable = false;
    public static boolean isSelectAll = false;
    public static ObservableArrayList<Photo> selectedList = new ObservableArrayList<>();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PhotosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PhotosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PhotosFragment newInstance(String param1, String param2) {
        PhotosFragment fragment = new PhotosFragment();
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
        mData = FirebaseDatabase.getInstance().getReference();
//        Intent intent = new Intent(getActivity().getApplicationContext(),DownloadService.class);
//        getActivity().startService(intent);
//        new DownLoadImage().execute();
        photoList = new PhotoList(PhotoList.readMediaStore(getContext()));
//        loadPhotosOnFirebase();
        sharedPreferences = getContext().getSharedPreferences("Firebase", Context.MODE_PRIVATE);
        Boolean isUploadedOnFireBase = sharedPreferences.getBoolean("isUploadedOnFireBase", false);
        if(!isUploadedOnFireBase){
            savePhotosOnFirebase(storageReference,photoList);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.binding = FragmentPhotosBinding.inflate(inflater, container, false);
        binding.recyclerView.setLayoutManager( new LinearLayoutManager(
                container.getContext(),
                RecyclerView.VERTICAL,false));
        currentLayout = LAYOUT_SORT_BY_DATE;
        photoSortByAdapter = new PhotoSortByDateAdapter(
                container.getContext(),
                photoList.getPhotoList(),
                PhotoAdapter.THUMBNAIL_MODE, currentLayout);
        Collections.reverse(PhotoSortByDateAdapter.ogPhotoList.getPhotoList());
        swipeRefreshLayout = binding.swipeRefresh;
        binding.recyclerView.setAdapter(photoSortByAdapter);
//        binding.recyclerView.addItemDecoration(
//                new DividerItemDecoration(container.getContext(),
//                        DividerItemDecoration.VERTICAL));

        binding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateAdapterData();
            }
        });
        return binding.getRoot();
    }

    private void updateAdapterData(){
        photoList = new PhotoList(PhotoList.readMediaStore(getContext()));
        loadPhotosOnFirebase(MainActivity.userEmail);
        photoSortByAdapter.setOgPhotoList(photoList);
        savePhotosOnFirebase(storageReference,photoList);
        binding.swipeRefresh.setRefreshing(false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.option_menu_photos,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int layout = -1;
        switch (item.getItemId()){
            case R.id.photosmnu_sortByDate:
                layout = PhotosFragment.LAYOUT_SORT_BY_DATE;
                break;
            case R.id.photosmnu_sortByMonth:
                layout = PhotosFragment.LAYOUT_SORT_BY_MONTH;
                break;
            case R.id.photosmnu_sortByYear:
                layout = PhotosFragment.LAYOUT_SORT_BY_YEAR;
                break;
        }

        if(layout != -1 && layout != currentLayout){
            currentLayout = layout;
            photoSortByAdapter = new PhotoSortByDateAdapter(
                    getContext(),
                    photoList.getPhotoList(),
                    PhotoAdapter.THUMBNAIL_MODE, layout);
            binding.recyclerView.swapAdapter(photoSortByAdapter,false);
            Collections.reverse(PhotoSortByDateAdapter.ogPhotoList.getPhotoList());
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateAdapterData();

    }

    private void loadPhotosOnFirebase(String userEmail){
//        mData.child("Images_User").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
//                    String downloadUrl = map.get("imgUrl");
//                    System.out.println(downloadUrl);
//                    String fileName = map.get("fileName");
//                    try {
//                        downloadPhoto(downloadUrl,fileName);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        mData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                    if(map.get("user").equals(userEmail)){
                        String downloadUrl = map.get("imgUrl");
                        String dateAdded = map.get("dateAdded");
                        String fileName = map.get("fileName");
                        Photo photo = new Photo(downloadUrl,dateAdded,fileName);
                        if(!checkIfPhotoIsExistInDevice(photoList,photo)){
                            photo.setIndex(photoList.size());
                            photoList.getPhotoList().add(photo);
                        }
                    }
                }
                photoSortByAdapter.setOgPhotoList(photoList);
                photoSortByAdapter.allocateIndexOfOgListByMilliseconds();
                for (Photo photo : photoSortByAdapter.getOgPhotoList().getPhotoList()){
                    System.out.println(photo.getFilename() + " index: " + photo.getIndex() + " " + photo.getDateAdded());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void savePhotosOnFirebase(StorageReference storageReference, PhotoList photoList){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for(Photo photo : photoList.getPhotoList()){
            System.out.println("Luu");
            saveSinglePhotoOnFirebaseStorage(storageReference,photo);
        }
        editor.putBoolean("isUploadedOnFireBase", true);
        editor.commit();
    }

    private void saveSinglePhotoOnFirebaseStorage(StorageReference storageReference, Photo photo){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Uri file = Uri.fromFile(new File(photo.getPath()));
        StorageReference riversRef = storageReference.child("images/"+photo.getFilename());
        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // if the File is exist on Firebase storage
                // Save with another user (detect duplicate user in below function)
                saveSinglePhotoInRealtimeDB(uri,photo);
                System.out.println("Exist");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // if the File is not exist on Firebase storage
                UploadTask uploadTask = riversRef.putFile(file);

                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(getActivity().getApplicationContext(),"Lỗi trong quá trình upload ảnh " + photo.getFilename(), Toast.LENGTH_SHORT).show();
                        editor.putBoolean("isUploadedOnFireBase", false);
                        editor.commit();
                        return;
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        // ...
                        Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                        firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri downloadUrl = uri;
                                Toast.makeText(getActivity().getApplicationContext(),"Upload ảnh " + photo.getFilename() +" thành công", Toast.LENGTH_SHORT).show();
                                System.out.println("Not Exist");
                                // Create node in firebase db
                                saveSinglePhotoInRealtimeDB(downloadUrl,photo);
                            }
                        });
                    }
                });
            }
        });
    }

    private void saveSinglePhotoInRealtimeDB(Uri downloadUrl, Photo photo){
        // Create node in firebase db
        Map<String, String> imgs_user = new HashMap<String, String>();
        imgs_user.put("dateAdded", photo.getMilliseconds().toString());
        imgs_user.put("fileName", photo.getFilename());
        imgs_user.put("imgUrl", downloadUrl.toString());
        imgs_user.put("user", MainActivity.userEmail);
        checkIfImagesUserIsExist(photo, MainActivity.userEmail, new FirebaseCallback() {
            @Override
            public void onCallbackCheckExist(boolean check) {
                System.out.println(check + " check");
                if(!check){
                    mData.child("Images_User").push().setValue(imgs_user);
                }
            }
        });
    }


    private boolean checkIfPhotoIsExistInDevice(PhotoList photoList,Photo photo){
        if(photoList.getPhotoList().size() == 0){
            return false;
        }
        for (Photo photo1 : photoList.getPhotoList()){
            if(photo.getFilename().equals(photo1.getFilename())){
                if(photo.getDateAdded().equals(photo1.getMilliseconds())){
                    return true; // Photo exist in device memory
                }
            }
        }
        return false; // Photo is not exist in device memory
    }

    private void checkIfImagesUserIsExist(Photo photoInDevice, String userEmail, FirebaseCallback firebaseCallback){
        mData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(!snapshot.hasChildren()){
                    firebaseCallback.onCallbackCheckExist(false);
                }
                else{
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        Map<String, String> map = (Map<String, String>) dataSnapshot.getValue();
                        String imgUrl = map.get("imgUrl");
                        String dateAdded = map.get("dateAdded");
                        String fileName = map.get("fileName");
                        String userEmailFromFb = map.get("user");
                        Photo photo = new Photo(imgUrl,dateAdded,fileName);
                        if(photoInDevice.getFilename().equals(photo.getFilename())){
                            if(photoInDevice.getMilliseconds().equals(photo.getDateAdded())){
                                System.out.println(userEmail+ " " + userEmailFromFb);
                                if(userEmail.equals(userEmailFromFb)){
                                    System.out.println("true");
                                    firebaseCallback.onCallbackCheckExist(true);
                                    return;
                                }
                            }
                        }
                    }
                    firebaseCallback.onCallbackCheckExist(false);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private interface FirebaseCallback{
        void onCallbackCheckExist(boolean check);
    }

}