package com.example.model.albums;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.view.R;

import java.util.ArrayList;

public class CustomAlbumDialogAdapter extends BaseAdapter {
    Context context;
    ArrayList<SingleAlbumCustom> arrayList;
    int layout;

    public CustomAlbumDialogAdapter(Context context, ArrayList<SingleAlbumCustom> arrayList, int layout) {
        this.context = context;
        this.arrayList = arrayList;
        this.layout = layout;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
    private class ViewHolder{
        TextView txtName;
        TextView txtQuantity;
        ImageView Avatar;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout,null);
            //anh xa
            holder.txtName = (TextView) view.findViewById(R.id.txtNameAlbumCustom);
            holder.txtQuantity = (TextView) view.findViewById(R.id.txtQuantityPhotoCustom);
            holder.Avatar = (ImageView) view.findViewById(R.id.imgAvtAlbumCustom);
            view.setTag(holder);
        }
        else{
            holder = (ViewHolder) view.getTag();
        }

        //gan gia tri
        SingleAlbumCustom album = arrayList.get(i);
        holder.txtName.setText(album.getName());
        if(album.getUri() == null){
            holder.Avatar.setImageResource(R.drawable.empty_album);
            holder.txtQuantity.setText(0 + "");
        }
        else{
            holder.Avatar.setImageURI(album.getUri());
            holder.txtQuantity.setText(album.getQuantity()  + "");
        }
//        Animation animation = AnimationUtils.loadAnimation(context,R.anim.scale_list);
//        view.startAnimation(animation);
        return view;
    }
}
