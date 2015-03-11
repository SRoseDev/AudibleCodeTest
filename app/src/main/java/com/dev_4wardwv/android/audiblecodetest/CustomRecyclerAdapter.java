package com.dev_4wardwv.android.audiblecodetest;

import android.content.ClipData;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Android1 on 2/25/2015.
 */
public class CustomRecyclerAdapter
        extends RecyclerView.Adapter<RecyclerViewHolder> implements View.OnClickListener {

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

    public void onClick(View V) {

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
        viewHolder.largeImage.setOnTouchListener(new MyTouchListener());
        viewHolder.largeImage.setOnDragListener(new MyDragListener());
        viewHolder.topImage.setOnClickListener(this);
        viewHolder.topImage.setTag(mData.get(position).topImg);
        viewHolder.topImage.setOnTouchListener(new MyTouchListener());
        viewHolder.topImage.setOnDragListener(new MyDragListener());
        viewHolder.bottomImage.setOnClickListener(this);
        viewHolder.bottomImage.setTag(mData.get(position).bottomImg);
        viewHolder.bottomImage.setOnTouchListener(new MyTouchListener());
        viewHolder.bottomImage.setOnDragListener(new MyDragListener());

        //stove values in cache based on current drawable id and original url
        //MainActivity.urlCache.put(mData.get(position).largeImg, mData.get(position).largeImg);
        //MainActivity.urlCache.put(String.valueOf(viewHolder.topImage.getId()), mData.get(position).topImg);
        //MainActivity.urlCache.put(String.valueOf(viewHolder.bottomImage.getId()), mData.get(position).bottomImg);

    }

    private float mDownX;
    private float mDownY;
    private final float SCROLL_THRESHOLD = 10;
    private boolean isOnClick;

    // This defines your touch listener
    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent ev) {

            switch (ev.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = ev.getX();
                    mDownY = ev.getY();
                    isOnClick = true;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    view.setAlpha(1);
                    break;
                case MotionEvent.ACTION_UP:
                    if (isOnClick) {
                        //Log.i(LOG_TAG, "onClick ");
                        //TODO onClick code
                    }
                    view.setAlpha(1);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isOnClick && (Math.abs(mDownX - ev.getX()) > SCROLL_THRESHOLD || Math.abs(mDownY - ev.getY()) > SCROLL_THRESHOLD)) {
                        // Log.i(LOG_TAG, "movement detected");
                        isOnClick = false;
                        ClipData data = ClipData.newPlainText("", "");
                        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                        view.startDrag(data, shadowBuilder, view, 0);
                       // view.setVisibility(View.INVISIBLE);
                        view.setAlpha(.5f);
                        return true;
                    }
                    break;

                default:
                    break;
            }


            return false;
        }
    }

    class MyDragListener implements View.OnDragListener {
        //Drawable enterShape = getResources().getDrawable(R.drawable.shape_droptarget);
        //Drawable normalShape = getResources().getDrawable(R.drawable.shape);

        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    //v.setBackgroundDrawable(enterShape);
                    v.setBackgroundColor(Color.CYAN);
                    v.setPadding(2,2,2,2);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    //v.setBackgroundDrawable(normalShape);
                    v.setBackgroundColor(Color.TRANSPARENT);
                    v.setPadding(0,0,0,0);
                    break;
                case DragEvent.ACTION_DROP:
                    v.setBackgroundColor(Color.TRANSPARENT);
                    v.setPadding(0,0,0,0);
                    // Dropped, swap images
                    View view = (View) event.getLocalState();
                    ImageView container = (ImageView) v;
                    //container.set(view);
                    String sourceUrl = view.getTag().toString();
                    String containerUrl = container.getTag().toString();
                    int sourceSize = view.getHeight();
                    int containerSize = container.getHeight();
                    //Swap Images
                    if(containerSize > 200) {
                        Picasso.with(MainActivity.context)
                                .load(sourceUrl)
                                .resize(310, 310)
                                .centerCrop()
                                .into(container);
                        //view.setVisibility(View.VISIBLE);
                    }
                    else{
                        Picasso.with(MainActivity.context)
                                .load(sourceUrl)
                                .resize(150, 150)
                                .centerCrop()
                                .into(container);
                        //view.setVisibility(View.VISIBLE);
                    }

                    if(sourceSize > 200) {
                        Picasso.with(MainActivity.context)
                                .load(containerUrl)
                                .resize(310, 310)
                                .centerCrop()
                                .into((ImageView)view);
                        //view.setVisibility(View.VISIBLE);
                    }
                    else{
                        Picasso.with(MainActivity.context)
                                .load(containerUrl)
                                .resize(150, 150)
                                .centerCrop()
                                .into((ImageView)view);
                        //view.setVisibility(View.VISIBLE);
                    }

                    //swap tags to account for url change
                    container.setTag(sourceUrl);
                    view.setTag(containerUrl);

                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    //v.setBackgroundDrawable(normalShape);
                default:
                    break;
            }
            return true;
        }
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
