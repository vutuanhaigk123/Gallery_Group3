package com.example.model.photos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ObservableArrayList;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.model.CustomImageView;
import com.example.view.MainActivity;
import com.example.view.PhotosFragment;
import com.example.view.R;
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
        binding.rvPhotoGrid.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                for(int i =0; i < binding.rvPhotoGrid.getChildCount();i++){
                    int finalI = i;
                    binding.rvPhotoGrid.getLayoutManager().getChildAt(i).findViewById(R.id.imageView).setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            if(!PhotosFragment.isEnable){
                                // Khi action mode chưa được khởi động
                                // Khởi tạo action mode
                                ActionMode.Callback callback = new ActionMode.Callback() {
                                    @Override
                                    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                                        MenuInflater menuInflater = actionMode.getMenuInflater();
                                        menuInflater.inflate(R.menu.action_mode_menu,menu);
                                        actionMode.setTitle("Choose your option");
                                        return true;
                                    }

                                    @Override
                                    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                                        // Khi action mode đã được chuẩn bị
                                        MainActivity.bottomNavigationView.setVisibility(View.GONE);
                                        PhotosFragment.isEnable = true;
                                        ClickItem(binding,finalI);
                                        notifyDataSetChanged();
                                        return false;
                                    }

                                    @Override
                                    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                                        // Khi click action mode item
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
                                                if(PhotosFragment.selectedList.size() == ogPhotoList.getPhotoList().size()){
                                                    // Khi tất cả hình ảnh đã được chọn
                                                    for (int i =0 ; i < photoSortByDateList.size();i++){
                                                        for (int j = 0 ; j < photoSortByDateList.get(i).getPhotoSortByDateList().size();j++){
                                                            photoSortByDateList.get(i).getPhotoSortByDateList().getPhotoList().get(j).setClicked(false);
                                                        }
                                                    }
                                                    PhotosFragment.isSelectAll = false;
                                                    // Bỏ tất cả ảnh đã chọn
                                                    PhotosFragment.selectedList.clear();
                                                    notifyDataSetChanged();
                                                }
                                                else{
                                                    // Khi tất cả hình ảnh chưa được chọn hết
                                                    for (int i =0 ; i < photoSortByDateList.size();i++){
                                                        for (int j = 0 ; j < photoSortByDateList.get(i).getPhotoSortByDateList().size();j++){
                                                            photoSortByDateList.get(i).getPhotoSortByDateList().getPhotoList().get(j).setClicked(true);
                                                        }
                                                    }
                                                    PhotosFragment.isSelectAll = true;
                                                    // Bỏ tất cả ảnh đã chọn
                                                    PhotosFragment.selectedList.clear();
                                                    // Thêm tất cả ảnh vào danh sách được chọn
                                                    PhotosFragment.selectedList.addAll(ogPhotoList.getPhotoList());
                                                    notifyDataSetChanged();
                                                }
                                                break;
                                        }
                                        return false;
                                    }

                                    @Override
                                    public void onDestroyActionMode(ActionMode actionMode) {
                                        // Khi action mode bị huỷ
                                        PhotosFragment.isEnable = false;
                                        PhotosFragment.isSelectAll = false;
                                        PhotosFragment.selectedList.clear();
                                        notifyDataSetChanged();
                                    }
                                };
                                ((AppCompatActivity)view.getContext()).startActionMode(callback);
                            }else{
                                // Khi action mode đã được bật
                                ClickItem(binding,finalI);
                                notifyDataSetChanged();
                            }
                            return true;
                        }
                    });
                }
                binding.rvPhotoGrid.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                if(PhotosFragment.isEnable){
                    for (int j = 0 ; j < binding.rvPhotoGrid.getChildCount();j++){
                        ((CustomImageView)binding.rvPhotoGrid.getLayoutManager().getChildAt(j).findViewById(R.id.imageView)).setColorFilter(Color.parseColor("#40000000"));
                        binding.rvPhotoGrid.getLayoutManager().getChildAt(j).findViewById(R.id.icUnCheck).setVisibility(View.VISIBLE);
                    }
                }
                if(PhotosFragment.isSelectAll){
                    for (int j = 0 ; j < binding.rvPhotoGrid.getChildCount();j++){
                        binding.rvPhotoGrid.getLayoutManager().getChildAt(j).findViewById(R.id.icCheck).setVisibility(View.VISIBLE);
                    }
                }
                else{
                    for (int j = 0 ; j < binding.rvPhotoGrid.getChildCount();j++){
                        binding.rvPhotoGrid.getLayoutManager().getChildAt(j).findViewById(R.id.icCheck).setVisibility(View.GONE);
                    }
                }
                if(PhotosFragment.isSelectAll){
                    for (int j = 0 ; j < binding.rvPhotoGrid.getChildCount();j++){
                        if(photoSortByDate.getPhotoSortByDateList().getPhotoList().get(j).isClicked()){
                            binding.rvPhotoGrid.getLayoutManager().getChildAt(j).findViewById(R.id.icCheck).setVisibility(View.VISIBLE);
                        }
                        else{
                            binding.rvPhotoGrid.getLayoutManager().getChildAt(j).findViewById(R.id.icCheck).setVisibility(View.GONE);
                        }
                    }
                }

                if(PhotosFragment.isEnable){
                    for (int i = 0; i < photoSortByDate.getPhotoSortByDateList().getPhotoList().size();i++){
                        if(photoSortByDate.getPhotoSortByDateList().getPhotoList().get(i).isClicked()){
                            binding.rvPhotoGrid.getLayoutManager().getChildAt(i).findViewById(R.id.icCheck).setVisibility(View.VISIBLE);
                        }
                    }
                }
                if(!PhotosFragment.isEnable){
                    for (int i = 0; i < photoSortByDate.getPhotoSortByDateList().getPhotoList().size();i++){
                        if(photoSortByDate.getPhotoSortByDateList().getPhotoList().get(i).isClicked()){
                            binding.rvPhotoGrid.getLayoutManager().getChildAt(i).findViewById(R.id.icCheck).setVisibility(View.GONE);
                            photoSortByDate.getPhotoSortByDateList().getPhotoList().get(i).setClicked(false);
                        }
                    }
                }
            }
        });

    }

    public void ClickItem( LayoutPhotoByDateAddedBinding layoutPhotoByDateAddedBinding, int position) {
//        Photo photoTemp = layoutPhotoByDateAddedBinding.getPhoto().getPhotoSortByDateList().getPhotoList().get(position);
        Photo photoTemp = layoutPhotoByDateAddedBinding.getPhoto().getPhotoSortByDateList().getPhotoList().get(position);
        View icCheck = layoutPhotoByDateAddedBinding.rvPhotoGrid.getLayoutManager().getChildAt(position).findViewById(R.id.icCheck);
        View icUnCheck = layoutPhotoByDateAddedBinding.rvPhotoGrid.getLayoutManager().getChildAt(position).findViewById(R.id.icUnCheck);
        if(icCheck.getVisibility() == View.GONE){
            // Khi hình ảnh chưa được chọn
            layoutPhotoByDateAddedBinding.getPhoto().getPhotoSortByDateList().getPhotoList().get(position).setClicked(true);
            icCheck.setVisibility(View.VISIBLE);
            icUnCheck.setVisibility(View.GONE);
            PhotosFragment.selectedList.add(photoTemp);
        }
        else{
            layoutPhotoByDateAddedBinding.getPhoto().getPhotoSortByDateList().getPhotoList().get(position).setClicked(false);
            // Khi hình ảnh đã được chọn
            icCheck.setVisibility(View.GONE);
            icUnCheck.setVisibility(View.VISIBLE);
            PhotosFragment.selectedList.remove(photoTemp);
        }
        for (int i=0; i < PhotosFragment.selectedList.size();i++){
            System.out.println(PhotosFragment.selectedList.get(i).getFilename());
        }
        System.out.println("-------------------------");
        //Set text
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
