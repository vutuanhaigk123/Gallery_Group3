package com.example.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.model.photos.Photo;
import com.example.model.photos.PhotoAdapter;
import com.example.model.photos.PhotoList;
import com.example.model.photos.PhotoSortByDateAdapter;
import com.example.view.databinding.FragmentPhotosBinding;

import java.util.ArrayList;
import java.util.Collections;

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
        photoList = new PhotoList(PhotoList.readMediaStore(getContext()));
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
        photoSortByAdapter.setOgPhotoList(photoList);
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


}