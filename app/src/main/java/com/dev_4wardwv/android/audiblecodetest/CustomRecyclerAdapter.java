package com.dev_4wardwv.android.audiblecodetest;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Android1 on 2/25/2015.
 */
public class CustomRecyclerAdapter
        extends RecyclerView.Adapter<RecyclerViewHolder> implements View.OnClickListener{

    private ArrayList<Data> mData = new ArrayList<>();

    public CustomRecyclerAdapter() {
        // Pass context or other static stuff that will be needed.
    }

    public void updateList(ArrayList<Data> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.tri_images_layout, viewGroup, false);
        return new RecyclerViewHolder(itemView);
    }

    public void onClick(View V){

        ImageView clicked = (ImageView) V;

        Picasso.with(MainActivity.context)
                .load(clicked.getTag().toString())
                .resize(640, 640)
                .centerCrop()
                .into(MainActivity.fullView);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder viewHolder, int position) {

        Picasso.with(MainActivity.context)
                .load(mData.get(position).largeImg)
                .resize(310, 310)
                .centerCrop()
                .into(viewHolder.largeImage);

        Picasso.with(MainActivity.context)
                .load(mData.get(position).topImg)
                .resize(150, 150)
                .centerCrop()
                .into(viewHolder.topImage);

        Picasso.with(MainActivity.context)
                .load(mData.get(position).bottomImg)
                .resize(150, 150)
                .centerCrop()
                .into(viewHolder.bottomImage);

        viewHolder.largeImage.setOnClickListener(this);
        viewHolder.largeImage.setTag(mData.get(position).largeImg);
        viewHolder.topImage.setOnClickListener(this);
        viewHolder.topImage.setTag(mData.get(position).topImg);
        viewHolder.bottomImage.setOnClickListener(this);
        viewHolder.bottomImage.setTag(mData.get(position).bottomImg);

        //stove values in cache based on current drawable id and original url
        //MainActivity.urlCache.put(mData.get(position).largeImg, mData.get(position).largeImg);
        //MainActivity.urlCache.put(String.valueOf(viewHolder.topImage.getId()), mData.get(position).topImg);
        //MainActivity.urlCache.put(String.valueOf(viewHolder.bottomImage.getId()), mData.get(position).bottomImg);

    }

    public void addItem(int position, Data data) {

//        if(position > 0 || position < mData.size()-1) {
//            mData.add(position, data);
//            notifyItemInserted(position);
//        }
//        else {
//            mData.add(data);
//            notifyDataSetChanged();
//        }

        mData.add(data);
        notifyDataSetChanged();

    }

    public void removeItem(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

}
