package com.example.view;

import android.os.Bundle;

import androidx.databinding.ObservableArrayList;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.model.photos.Photo;
import com.example.model.photos.PhotoAdapter;
import com.example.model.photos.PhotoList;
import com.example.model.photos.PhotoSortByDateAdapter;
import com.example.view.databinding.FragmentPhotosBinding;

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
    private PhotoList photoList;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.binding = FragmentPhotosBinding.inflate(inflater, container, false);
        binding.recyclerView.setLayoutManager( new LinearLayoutManager(container.getContext(), RecyclerView.VERTICAL,false));
        PhotoSortByDateAdapter photoSortByDateAdapter = new PhotoSortByDateAdapter(container.getContext(),photoList.getPhotoList(), PhotoAdapter.THUMBNAIL_MODE);
        binding.recyclerView.setAdapter(photoSortByDateAdapter);
//        binding.recyclerView.addItemDecoration(
//                new DividerItemDecoration(container.getContext(),
//                        DividerItemDecoration.VERTICAL));

        return binding.getRoot();
    }

}