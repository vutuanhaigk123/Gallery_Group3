package com.example.model.albums;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ObservableArrayList;
import androidx.recyclerview.widget.RecyclerView;

import com.example.model.photos.Photo;
import com.example.model.photos.PhotoList;
import com.example.model.photos.ViewHolder;
import com.example.view.PhotosActivity;
import com.example.view.databinding.LayoutAlbumThumbnailBinding;
import com.example.view.databinding.LayoutEnterPasswordBinding;
import com.example.view.databinding.LayoutSetPasswordBinding;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class AlbumAdapter extends  RecyclerView.Adapter<ViewHolder> {
    public static AlbumList albumList;
    private int mode = 0;
    private Context context;
    private int layout;

    public AlbumAdapter(Context context, ObservableArrayList<Album> albums,int mode,  int layout) {
        this.mode = mode;
        this.context = context;
        this.albumList = new AlbumList((ObservableArrayList<Album>) albums.clone());
        this.layout = layout;
    }
    public void setAlbumList( AlbumList albumList){
        this.albumList = albumList;
        notifyDataSetChanged();
    }
    public class AlbumViewHolder extends RecyclerView.ViewHolder{
        private TextView dateAdded;
        private RecyclerView rvPhotoGrid;
        private LayoutAlbumThumbnailBinding binding;
        public AlbumViewHolder(@NonNull LayoutAlbumThumbnailBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
//            dateAdded = itemView.findViewById(R.id.dateAdded);
//            rvPhotoGrid = itemView.findViewById(R.id.rvPhotoGrid);
        }

        public LayoutAlbumThumbnailBinding getBinding(){
            return binding;
        }

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutAlbumThumbnailBinding photoRowBinding = (LayoutAlbumThumbnailBinding) LayoutAlbumThumbnailBinding.inflate(
                layoutInflater, parent, false
        );
        photoRowBinding.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(AlbumRoute.getPassword(photoRowBinding.getPhoto().getIndex()) != null) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(photoRowBinding.getRoot().getContext());
                    builder.setTitle("Password");
                    LayoutEnterPasswordBinding layoutEnterPasswordBinding = LayoutEnterPasswordBinding.inflate(layoutInflater);
                    EditText password = layoutEnterPasswordBinding.passwordAlbum;
                    builder.setView(layoutEnterPasswordBinding.getRoot());
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            BCrypt.Result result = BCrypt.verifyer().verify(password.getText().toString().toCharArray(), AlbumRoute.getPassword(photoRowBinding.getPhoto().getIndex()));
                            if( result.verified) {
                                //switch to screen photo
                                switchToScreenPhoto(photoRowBinding);
                            }
                            else
                                Toast.makeText(photoRowBinding.getRoot().getContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.show();
                }
                else {
                    switchToScreenPhoto(photoRowBinding);
                }
            }
        });
        return new com.example.model.albums.AlbumViewHolder(photoRowBinding, albumList);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        LayoutAlbumThumbnailBinding binding =
                ((LayoutAlbumThumbnailBinding)holder.getBinding());
        String name_album = albumList.get(position).getName();
        int id_album = AlbumRoute.findIdByNameAlbum(name_album);
        int id_photo = AlbumRoute.getFirstPhotoInAlbum(id_album);
        if(id_photo != -1){
            Photo photo = AlbumRoute.getPhotoByIdAndAddIndex(id_photo,id_album);//id album để khi getIndexAlbum ở AlbumViewHolder index sẽ là vị trí của album(trick)
            binding.setPhoto(photo);
        }
        else{
            Photo photo =  new Photo("drawable/empty_album.png","01/01/1970",
                    "image/png","empty_album.png",id_album);
            binding.setPhoto(photo);
        }
        binding.executePendingBindings();
        binding.tvAlbumName.setText(name_album);
        binding.tvPhotoNum.setText(AlbumRoute.getNumberOfPhotoInAlbum(id_album) + "");
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    private void switchToScreenPhoto(LayoutAlbumThumbnailBinding albumThumbnailBinding){
        Intent intent = new Intent(albumThumbnailBinding.getRoot().getContext(), PhotosActivity.class);
        int pos = albumThumbnailBinding.getPhoto().getIndex()-1;
        PhotoList photoList = AlbumRoute.getPhotoListByAlbum(albumList.get(pos).getId());

        intent.putExtra("photoListOfAlbum",photoList);
        intent.putExtra("nameOfAlbum",albumList.get(pos).getName());
        intent.putExtra("isAlbum",true);
        albumThumbnailBinding.getRoot().getContext().startActivity(intent);

    }

}
