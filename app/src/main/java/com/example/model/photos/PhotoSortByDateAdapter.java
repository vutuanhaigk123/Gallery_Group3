package com.example.model.photos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.view.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class PhotoSortByDateAdapter extends RecyclerView.Adapter<PhotoSortByDateAdapter.PhotoSortByDateViewHolder> {
    private Context context;
    private ObservableArrayList<PhotoSortByDate> photoSortByDateList;
    public static PhotoList ogPhotoList;
    private int mode;

    public PhotoSortByDateAdapter(Context context, ObservableArrayList<Photo> photoList, int mode) {
        this.context = context;
        this.ogPhotoList = new PhotoList(photoList);
        createPhotoSortByDateList(photoList);
        this.mode = mode;
    }

    public PhotoList getOgPhotoList() {
        return ogPhotoList;
    }

    public void setOgPhotoList(PhotoList ogPhotoList) {
        this.ogPhotoList = ogPhotoList;
    }

    public ObservableArrayList<PhotoSortByDate> getPhotoList() {
        return photoSortByDateList;
    }

    // Hàm đổi millisecond sang dd/mm/yyyy
    public void parseMillisecondToDate(ObservableArrayList<Photo> photoList){
        for(int i =0 ; i < photoList.size();i++){
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String now = simpleDateFormat.format(calendar.getTime());
            calendar.setTimeInMillis(Long.parseLong(photoList.get(i).getDateAdded()) * 1000);
            String date = simpleDateFormat.format(calendar.getTime());
            photoList.get(i).setDateAdded(date);
            if(photoList.get(i).getDateAdded().equals(now)){
                photoList.get(i).setDateAdded("Hôm nay");
            }
        }
    }

    // Hàm sắp xếp danh sách các danh sách ảnh theo ngày
    public void sortListPhotoByDate(ObservableArrayList<PhotoSortByDate> photoSortByDateList){
        Collections.sort(photoSortByDateList, new Comparator<PhotoSortByDate>() {
            @Override
            public int compare(PhotoSortByDate t1, PhotoSortByDate t2) {
                if(t1.getDateAdded().compareTo(t2.getDateAdded()) <= 0){
                    return 1;
                }
                else{
                    return -1;
                }
            }
        });
    }

    // Hàm đảo ngược các danh sách con đã sắp xếp theo date trong danh sách hình ảnh để hình được thêm sau cùng trong date đó xuất hiện đầu tiên xong SlideView
    public void reversePhotoList(ObservableArrayList<PhotoSortByDate> photoSortByDateList){
        for (int i =0; i < photoSortByDateList.size();i++){
            Collections.reverse(photoSortByDateList.get(i).getPhotoSortByDateList().getPhotoList());
        }
    }

    // Hàm tạo danh sách hình ảnh được sắp xếp theo ngày dựa vào danh sách hình ảnh lấy được ban đầu
    public void createPhotoSortByDateList(ObservableArrayList<Photo> photoList){
        ObservableArrayList<PhotoSortByDate> photoSortByDateList = new ObservableArrayList<>();
        String dateAdded = "";
        parseMillisecondToDate(photoList);
        for(int i= 0; i< photoList.size();i++){
            if(!dateAdded.equals(photoList.get(i).getDateAdded())){
                dateAdded = photoList.get(i).getDateAdded();
                ObservableArrayList<Photo> tempList = new ObservableArrayList<>(); // Danh sách tạm chứa các ảnh có cùng ngày
                while(i < photoList.size() && dateAdded.equals(photoList.get(i).getDateAdded())){
                    tempList.add(photoList.get(i));
                    i++;
                }
                i--;
                PhotoList photoListTemp = new PhotoList(tempList);
                PhotoSortByDate temp = new PhotoSortByDate(dateAdded,photoListTemp); // Danh sách tạm chứa các danh sách đã sắp xếp theo ngày
                photoSortByDateList.add(temp);
            }
        }
        sortListPhotoByDate(photoSortByDateList);
        reversePhotoList(photoSortByDateList);
        this.photoSortByDateList = photoSortByDateList;
//        for (int i =0 ; i < this.photoSortByDateList.size();i++){
//            System.out.println(i);
//            System.out.println(this.photoSortByDateList.get(i).getDateAdded());
//            for (int j = 0; j < this.photoSortByDateList.get(i).getPhotoSortByDateList().size();j++){
//                System.out.println(this.photoSortByDateList.get(i).getPhotoSortByDateList().get(j).getFilename());
//            }
//        }
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public PhotoSortByDateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_photo_by_date_added,parent,false);
        return new PhotoSortByDateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoSortByDateViewHolder holder, int position) {
        PhotoSortByDate photoSortByDate = photoSortByDateList.get(position);
        if(photoSortByDate == null){
            return;
        }
        holder.dateAdded.setText(photoSortByDate.getDateAdded());
        holder.rvPhotoGrid.setLayoutManager(new GridLayoutManager(context,4));
        PhotoAdapter photoAdapter = new PhotoAdapter(photoSortByDate.getPhotoSortByDateList(),this.mode);
        holder.rvPhotoGrid.setAdapter(photoAdapter);
    }

    @Override
    public int getItemCount() {
        if(photoSortByDateList != null)
            return photoSortByDateList.size();
        return 0;
    }

    public class PhotoSortByDateViewHolder extends RecyclerView.ViewHolder{
        private TextView dateAdded;
        private RecyclerView rvPhotoGrid;
        public PhotoSortByDateViewHolder(@NonNull View itemView) {
            super(itemView);
            dateAdded = itemView.findViewById(R.id.dateAdded);
            rvPhotoGrid = itemView.findViewById(R.id.rvPhotoGrid);
        }

    }
}
