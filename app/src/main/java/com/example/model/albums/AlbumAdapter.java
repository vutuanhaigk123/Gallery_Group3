package com.example.model.albums;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ObservableArrayList;
import androidx.recyclerview.widget.RecyclerView;

import com.example.model.CustomImageView;
import com.example.model.photos.Photo;
import com.example.model.photos.PhotoList;
import com.example.model.photos.ViewHolder;
import com.example.view.AlbumsFragment;
import com.example.view.MainActivity;
import com.example.view.PhotosActivity;
import com.example.view.R;
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
        Album album = albumList.get(position);
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
        binding.setAlbum(album);
        binding.executePendingBindings();
        binding.tvAlbumName.setText(name_album);
        binding.tvPhotoNum.setText(AlbumRoute.getNumberOfPhotoInAlbum(id_album) + "");
        AlbumsFragment.binding.rvAlbums.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.imgAvtAlbum.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if(!AlbumsFragment.isEnable){
                            ActionMode.Callback callback = new ActionMode.Callback() {
                                @Override
                                public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                                    MenuInflater menuInflater = actionMode.getMenuInflater();
                                    menuInflater.inflate(R.menu.action_mode_menu_album,menu);
                                    actionMode.setTitle("Choose your option");
                                    return true;
                                }

                                @Override
                                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                                    if(!AlbumsFragment.isEnable){
                                        MainActivity.bottomNavigationView.setVisibility(View.GONE);
                                        AlbumsFragment.isEnable = true;
                                        if(!isSystemAlbum(AlbumsFragment.binding.rvAlbums,position)){
                                            ClickItem(binding,position);
                                        }
                                        notifyDataSetChanged();
                                    }
                                    return false;
                                }

                                @Override
                                public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                                    switch (menuItem.getItemId()){
                                        case R.id.acm_delete:
//                                                for(Photo photo: PhotosFragment.selectedList){
//                                                    photoList.getPhotoList().remove(photo);
//                                                }
//                                                if(photoList.getPhotoList().size() == 0){
//                                                    // TO DO
//                                                }
                                            actionMode.finish();
                                            break;
                                        case R.id.acm_select_all:
                                            if(AlbumsFragment.selectedList.size() == albumList.getAlbumList().size() - 1){
                                                // Khi tất cả hình ảnh đã được chọn
                                                for (int i =0 ; i < albumList.size();i++){
                                                    albumList.get(i).setClicked(false);
                                                }
                                                AlbumsFragment.isSelectAll = false;
                                                // Bỏ tất cả ảnh đã chọn
                                                AlbumsFragment.selectedList.clear();
                                                notifyDataSetChanged();
                                            }
                                            else{
                                                // Khi tất cả hình ảnh chưa được chọn hết
                                                for (int i =0 ; i < albumList.size();i++){
                                                    if(!albumList.get(i).getName().equals("Yêu thích")
                                                    || !albumList.get(i).getName().equals("Yêu thích"))
                                                        albumList.get(i).setClicked(true);
                                                }
                                                AlbumsFragment.isSelectAll = true;
                                                // Bỏ tất cả ảnh đã chọn
                                                AlbumsFragment.selectedList.clear();
                                                // Thêm tất cả ảnh vào danh sách được chọn
                                                for (int i =0 ; i< albumList.size();i++){
                                                    if(!albumList.get(i).getName().equals("Yêu thích"))
                                                        AlbumsFragment.selectedList.add(albumList.get(i));
                                                }
                                                notifyDataSetChanged();
                                            }
                                            break;
                                    }
                                    return false;
                                }
                                @Override
                                public void onDestroyActionMode(ActionMode actionMode) {
                                    AlbumsFragment.isEnable = false;
                                    AlbumsFragment.isSelectAll = false;
                                    AlbumsFragment.selectedList.clear();
                                    MainActivity.bottomNavigationView.setVisibility(View.VISIBLE);
                                    notifyDataSetChanged();
                                }
                            };
                            ((AppCompatActivity)view.getContext()).startActionMode(callback);
                        }
                        else{
                            if(!isSystemAlbum(AlbumsFragment.binding.rvAlbums,position)){
                                ClickItem(binding,position);
                            }
                            notifyDataSetChanged();
                        }
                        return true;
                    }
                });
                AlbumsFragment.binding.rvAlbums.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                if(AlbumsFragment.isEnable){
                    for (int i =0 ;i < AlbumsFragment.binding.rvAlbums.getChildCount();i++){
                        if(!isSystemAlbum(AlbumsFragment.binding.rvAlbums,i)){
                            ((CustomImageView)AlbumsFragment.binding.rvAlbums.getLayoutManager().getChildAt(i).findViewById(R.id.imgAvtAlbum)).setColorFilter(Color.parseColor("#40000000"));
                            AlbumsFragment.binding.rvAlbums.getLayoutManager().getChildAt(i).findViewById(R.id.icUnCheck).setVisibility(View.VISIBLE);
                        }
                        else{
                            AlbumsFragment.binding.rvAlbums.getLayoutManager().getChildAt(i).findViewById(R.id.icCheck).setVisibility(View.GONE);
                        }
                    }
                }
                if(AlbumsFragment.isSelectAll){
                    for (int i = 0; i < AlbumsFragment.binding.rvAlbums.getChildCount();i++){
                        if(!isSystemAlbum(AlbumsFragment.binding.rvAlbums,i))
                            AlbumsFragment.binding.rvAlbums.getLayoutManager().getChildAt(i).findViewById(R.id.icCheck).setVisibility(View.VISIBLE);
                    }
                }
                else{
                    for (int i = 0; i < AlbumsFragment.binding.rvAlbums.getChildCount();i++){
                        if(!isSystemAlbum(AlbumsFragment.binding.rvAlbums,i))
                            AlbumsFragment.binding.rvAlbums.getLayoutManager().getChildAt(i).findViewById(R.id.icCheck).setVisibility(View.GONE);
                        else{
                            ((CustomImageView)AlbumsFragment.binding.rvAlbums.getLayoutManager().getChildAt(i).findViewById(R.id.imgAvtAlbum)).setColorFilter(null);
                            AlbumsFragment.binding.rvAlbums.getLayoutManager().getChildAt(i).findViewById(R.id.icUnCheck).setVisibility(View.GONE);
                        }
                    }
                }
                if(AlbumsFragment.isSelectAll){
                    for (int i = 0 ; i < albumList.size();i++){
                        if(isSystemAlbum(albumList.get(i))){
                            ((CustomImageView)AlbumsFragment.binding.rvAlbums.getLayoutManager().getChildAt(i).findViewById(R.id.imgAvtAlbum)).setColorFilter(null);
                            AlbumsFragment.binding.rvAlbums.getLayoutManager().getChildAt(i).findViewById(R.id.icCheck).setVisibility(View.GONE);
                            AlbumsFragment.binding.rvAlbums.getLayoutManager().getChildAt(i).findViewById(R.id.icUnCheck).setVisibility(View.GONE);
                            continue;
                        }
                        if(albumList.get(i).isClicked()){
                            AlbumsFragment.binding.rvAlbums.getLayoutManager().getChildAt(i).findViewById(R.id.icCheck).setVisibility(View.VISIBLE);
                        }
                        else{
                            AlbumsFragment.binding.rvAlbums.getLayoutManager().getChildAt(i).findViewById(R.id.icCheck).setVisibility(View.GONE);
                        }
                    }
                }

                if(AlbumsFragment.isEnable){
                    for (int i = 0; i < albumList.size(); i++){
                        if(isSystemAlbum(albumList.get(i)))
                            continue;
                        if(albumList.get(i).isClicked()){
                            AlbumsFragment.binding.rvAlbums.getLayoutManager().getChildAt(i).findViewById(R.id.icCheck).setVisibility(View.VISIBLE);
                        }
                    }
                }
                if(!AlbumsFragment.isEnable){
                    for (int i = 0; i < albumList.size();i++){
                        ((CustomImageView)AlbumsFragment.binding.rvAlbums.getLayoutManager().getChildAt(i).findViewById(R.id.imgAvtAlbum)).setColorFilter(null);
                        AlbumsFragment.binding.rvAlbums.getLayoutManager().getChildAt(i).findViewById(R.id.icUnCheck).setVisibility(View.GONE);
                        if(albumList.get(i).isClicked() || isSystemAlbum(album)){
                            AlbumsFragment.binding.rvAlbums.getLayoutManager().getChildAt(i).findViewById(R.id.icCheck).setVisibility(View.GONE);
                            albumList.get(i).setClicked(false);
                        }
                    }
                }
            }
        });

    }

    public void ClickItem(LayoutAlbumThumbnailBinding layoutAlbumThumbnailBinding, int position) {
//        Photo photoTemp = layoutPhotoByDateAddedBinding.getPhoto().getPhotoSortByDateList().getPhotoList().get(position);
        Album albumTemp = layoutAlbumThumbnailBinding.getAlbum();
        if(!isSystemAlbum(albumTemp)){
            View icCheck = layoutAlbumThumbnailBinding.icCheck;
            View icUnCheck = layoutAlbumThumbnailBinding.icUnCheck;
            if(icCheck.getVisibility() == View.GONE){
                // Khi hình ảnh chưa được chọn
                layoutAlbumThumbnailBinding.getAlbum().setClicked(true);
                icCheck.setVisibility(View.VISIBLE);
                icUnCheck.setVisibility(View.GONE);
                AlbumsFragment.selectedList.add(albumTemp);
            }
            else{
                layoutAlbumThumbnailBinding.getAlbum().setClicked(false);
                // Khi hình ảnh đã được chọn
                icCheck.setVisibility(View.GONE);
                icUnCheck.setVisibility(View.VISIBLE);
                AlbumsFragment.selectedList.remove(albumTemp);
            }
        }
        for (int i=0; i < AlbumsFragment.selectedList.size();i++){
            System.out.println(AlbumsFragment.selectedList.get(i).getName());
        }
        System.out.println("-------------------------");
        //Set text
    }

    private boolean isSystemAlbum(RecyclerView rv, int position){
        String name = ((TextView)rv.getLayoutManager().getChildAt(position).findViewById(R.id.tvAlbumName)).getText().toString();
        if(name.equals("Yêu thích") || name.equals("Đã xóa") || name.equals("Ảnh chụp màn hình")){
            return true;
        }
        else{
            return false;
        }
    }
    public static boolean isSystemAlbum(Album album){
        int id = album.getId();
        if(id == AlbumRoute.ID_ALBUM_FAVORITE || id == AlbumRoute.ID_ALBUM_DELETED || id == AlbumRoute.ID_ALBUM_SCREENSHOT){
            return true;
        }
        else{
            return false;
        }
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
