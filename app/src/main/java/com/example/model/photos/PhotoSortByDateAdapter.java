package com.example.model.photos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.view.PhotosFragment;
import com.example.view.databinding.LayoutPhotoByDateAddedBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class PhotoSortByDateAdapter extends RecyclerView.Adapter<PhotoSortByDateAdapter.PhotoSortByDateViewHolder> {
    private Context context;
    private ObservableArrayList<PhotoSortByDate> photoSortByDateList;
    public static PhotoList ogPhotoList;
    private int layout; // 0: sắp xếp theo ngày, 1: sắp xếp theo tháng, 2: sắp xếp theo năm
    private int mode;

    public PhotoSortByDateAdapter(Context context, ObservableArrayList<Photo> photoList, int mode, int layout) {
        this.context = context;
        this.layout = layout;
        PhotoSortByDateAdapter.ogPhotoList = new PhotoList((ObservableArrayList<Photo>) photoList.clone());
        createPhotoSortByDateList(photoList);
        this.mode = mode;
    }

    private void initPhotoAdapter(ObservableArrayList<Photo> photoList){
        createPhotoSortByDateList(photoList);
        Collections.reverse(ogPhotoList.getPhotoList());
    }

    public PhotoList getOgPhotoList() {
        return ogPhotoList;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setOgPhotoList(PhotoList ogPhotoList) {
        ObservableArrayList<Photo> photoList = ogPhotoList.getPhotoList();
        PhotoSortByDateAdapter.ogPhotoList = new PhotoList((ObservableArrayList<Photo>) photoList.clone());
        initPhotoAdapter(ogPhotoList.getPhotoList());
        notifyDataSetChanged();
    }

    public ObservableArrayList<PhotoSortByDate> getPhotoList() {
        return photoSortByDateList;
    }

    // Hàm đổi millisecond sang dd/mm/yyyy, mm/yyyy, yyyy
    public void parseMillisecondToDate(ObservableArrayList<Photo> photoList, int layout){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String now = simpleDateFormat.format(calendar.getTime());
        if(layout == PhotosFragment.LAYOUT_SORT_BY_DATE){
            simpleDateFormat =
                    new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            now = simpleDateFormat.format(calendar.getTime());
        }
        else if(layout == PhotosFragment.LAYOUT_SORT_BY_MONTH){
            simpleDateFormat =
                    new SimpleDateFormat("MM/yyyy", Locale.getDefault());
            now = simpleDateFormat.format(calendar.getTime());

        }
        else{
                simpleDateFormat =
                        new SimpleDateFormat("yyyy", Locale.getDefault());
                now = simpleDateFormat.format(calendar.getTime());
        }
        for(int i = 0 ; i < photoList.size(); i++){
            calendar.setTimeInMillis(Long.parseLong(photoList.get(i).getMilliseconds()) * 1000);
            String date = simpleDateFormat.format(calendar.getTime());
            photoList.get(i).setDateAdded(date);
            if(photoList.get(i).getDateAdded().equals(now)){
                if(layout == PhotosFragment.LAYOUT_SORT_BY_DATE){
                    photoList.get(i).setDateAdded("Hôm nay");
                }
                else if(layout == PhotosFragment.LAYOUT_SORT_BY_MONTH){
                    photoList.get(i).setDateAdded("Tháng này");
                }
                else{
                    photoList.get(i).setDateAdded("Năm nay");
                }

            }
        }
    }

    // Hàm sắp xếp danh sách các danh sách ảnh theo ngày, theo tháng, theo năm
    public void sortListPhotoByDate(ObservableArrayList<PhotoSortByDate> photoSortByDateList, int layout){
        if(layout == 0){
            Collections.sort(photoSortByDateList, new Comparator<PhotoSortByDate>() {
                @Override
                public int compare(PhotoSortByDate t1, PhotoSortByDate t2) {
                    if(!t1.getDateAdded().equals("Hôm nay") &&
                            !t2.getDateAdded().equals("Hôm nay")){
                        Date d1 = null, d2 = null;
                        try {
                            d1 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                    .parse(t1.getDateAdded());
                            d2 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                    .parse(t2.getDateAdded());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return d1.compareTo(d2);

                    }
                    else if(t1.getDateAdded().equals("Hôm nay") &&
                            t2.getDateAdded().equals("Hôm nay")){
                        return 0;
                    }
                    else if(t1.getDateAdded().equals("Hôm nay")){
                        return 1;
                    }
                    else{
                        return -1;
                    }

                }
            });
        }
        else if(layout == 1){
            Collections.sort(photoSortByDateList, new Comparator<PhotoSortByDate>() {
                @Override
                public int compare(PhotoSortByDate t1, PhotoSortByDate t2) {
                    if(!t1.getDateAdded().equals("Tháng này") &&
                            !t2.getDateAdded().equals("Tháng này")){
                        Date d1 = null, d2 = null;
                        try {
                            d1 = new SimpleDateFormat("MM/yyyy", Locale.getDefault())
                                    .parse(t1.getDateAdded());
                            d2 = new SimpleDateFormat("MM/yyyy", Locale.getDefault())
                                    .parse(t2.getDateAdded());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return d1.compareTo(d2);

                    }
                    else if(t1.getDateAdded().equals("Tháng này") &&
                            t2.getDateAdded().equals("Tháng này")){
                        return 0;
                    }
                    else if(t1.getDateAdded().equals("Tháng này")){
                        return 1;
                    }
                    else{
                        return -1;
                    }

                }
            });
        }
        else{
            Collections.sort(photoSortByDateList, new Comparator<PhotoSortByDate>() {
                @Override
                public int compare(PhotoSortByDate t1, PhotoSortByDate t2) {
                    if(!t1.getDateAdded().equals("Năm nay") &&
                            !t2.getDateAdded().equals("Năm nay")){
                        Date d1 = null, d2 = null;
                        try {
                            d1 = new SimpleDateFormat("yyyy", Locale.getDefault())
                                    .parse(t1.getDateAdded());
                            d2 = new SimpleDateFormat("yyyy", Locale.getDefault())
                                    .parse(t2.getDateAdded());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return d1.compareTo(d2);

                    }
                    else if(t1.getDateAdded().equals("Năm nay") &&
                            t2.getDateAdded().equals("Năm nay")){
                        return 0;
                    }
                    else if(t1.getDateAdded().equals("Năm nay")){
                        return 1;
                    }
                    else{
                        return -1;
                    }

                }
            });
        }
    }

    // Hàm đảo ngược các danh sách con đã sắp xếp theo date trong danh sách hình ảnh để hình
    // được thêm sau cùng trong date đó xuất hiện đầu tiên xong SlideView
    public void reversePhotoList(ObservableArrayList<PhotoSortByDate> photoSortByDateList){
        for (int i = 0; i < photoSortByDateList.size(); i++){
            Collections.reverse(photoSortByDateList.get(i).getPhotoSortByDateList().getPhotoList());
        }
    }

    // Hàm tạo danh sách hình ảnh được sắp xếp theo ngày dựa vào danh sách hình ảnh lấy được ban đầu
    public void createPhotoSortByDateList(ObservableArrayList<Photo> photoList){
        ObservableArrayList<PhotoSortByDate> photoSortByDateList =
                new ObservableArrayList<PhotoSortByDate>();
        String dateAdded = "";
        parseMillisecondToDate(photoList, this.layout);
        for(int i = 0; i < photoList.size(); i++){
            if(!dateAdded.equals(photoList.get(i).getDateAdded())){
                dateAdded = photoList.get(i).getDateAdded();
                // Danh sách tạm chứa các ảnh có cùng ngày
                ObservableArrayList<Photo> tempList = new ObservableArrayList<Photo>();
                while(i < photoList.size() && dateAdded.equals(photoList.get(i).getDateAdded())){
                    tempList.add(photoList.get(i));
                    i++;
                }
                i--;
                PhotoList photoListTemp = new PhotoList(tempList);
                // Danh sách tạm chứa các danh sách đã sắp xếp theo ngày
                PhotoSortByDate temp = new PhotoSortByDate(dateAdded,photoListTemp);
                photoSortByDateList.add(temp);
            }
        }
        sortListPhotoByDate(photoSortByDateList,this.layout);
        Collections.reverse(photoSortByDateList);
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
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LayoutPhotoByDateAddedBinding binding = LayoutPhotoByDateAddedBinding.inflate(
                layoutInflater, parent, false);
//        View view = LayoutInflater.from(parent.getContext()).inflate(
//                R.layout.layout_photo_by_date_added,parent,false);
        return new PhotoSortByDateViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoSortByDateViewHolder holder, int position) {
        PhotoSortByDate photoSortByDate = photoSortByDateList.get(position);
        if(photoSortByDate == null){
            return;
        }
        LayoutPhotoByDateAddedBinding binding = holder.getBinding();
        binding.setPhoto(photoSortByDate);
        binding.executePendingBindings();
        binding.rvPhotoGrid.setLayoutManager(new GridLayoutManager(context,4));
        PhotoAdapter photoAdapter = new PhotoAdapter(
                photoSortByDate.getPhotoSortByDateList(), this.mode);
        binding.rvPhotoGrid.setAdapter(photoAdapter);

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
        private LayoutPhotoByDateAddedBinding binding;
        public PhotoSortByDateViewHolder(@NonNull LayoutPhotoByDateAddedBinding binding) {
            super(binding.getRoot());
            dateAdded = binding.dateAdded;
            rvPhotoGrid = binding.rvPhotoGrid;
            this.binding = binding;
//            dateAdded = itemView.findViewById(R.id.dateAdded);
//            rvPhotoGrid = itemView.findViewById(R.id.rvPhotoGrid);
        }

        public LayoutPhotoByDateAddedBinding getBinding(){
            return binding;
        }

    }
}
