package com.dev_4wardwv.android.audiblecodetest;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Android1 on 2/25/2015.
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder {

    public ImageView topImage, bottomImage, largeImage;


    public RecyclerViewHolder(View itemView) {
        super(itemView);
        topImage = (ImageView) itemView.findViewById(R.id.topImg);
        bottomImage = (ImageView) itemView.findViewById(R.id.bottomImg);
        largeImage = (ImageView) itemView.findViewById(R.id.largeImg);
    }
}
