package com.example.model.photos;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class ViewHolder extends RecyclerView.ViewHolder {
    private androidx.databinding.ViewDataBinding binding;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public androidx.databinding.ViewDataBinding getBinding(){
        return binding;
    }
}
