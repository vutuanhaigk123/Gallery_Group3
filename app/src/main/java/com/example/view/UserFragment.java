package com.example.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.view.databinding.FragmentUserBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    private FragmentUserBinding binding;
    private String userEmail;
    private SharedPreferences sharedPreferences;
    private SharedPreferences firebasePreferences;
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public UserFragment(String userEmail) {
        this.userEmail = userEmail;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1) {
        System.out.println(param1);
        UserFragment fragment = new UserFragment(param1);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
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
        sharedPreferences = getContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        firebasePreferences = getContext().getSharedPreferences("Firebase", Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.binding = FragmentUserBinding.inflate(inflater,container,false);
        binding.userEmail.setText(userEmail);
        binding.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        System.out.println(userEmail);
        return binding.getRoot();
    }

    private void logout(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        SharedPreferences.Editor editorFirebase = firebasePreferences.edit();
        editor.putBoolean("isLogin", false);
        editor.remove("email");
        editor.remove("password");
        editorFirebase.putBoolean("isUploadedOnFireBase", false);
        editor.commit();
        editorFirebase.commit();
        Intent intent = new Intent(getContext(),LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}