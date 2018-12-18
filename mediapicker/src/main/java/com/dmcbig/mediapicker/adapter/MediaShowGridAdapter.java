package com.dmcbig.mediapicker.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dmcbig.mediapicker.R;
import com.dmcbig.mediapicker.entity.Media;
import com.dmcbig.mediapicker.utils.FileUtils;

import java.util.ArrayList;

/**
 * Created by dmcBig on 2017/7/5.
 */

public class MediaShowGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int ITEM_TYPE = 100;
    public static final int ADD_TYPE = 101;
    private int size;
    private LayoutInflater mInflater;
    private OnRecyclerViewItemClickListener onAlbumSelectListener;

    ArrayList<Media> medias;
    Context context;
    FileUtils fileUtils = new FileUtils();
    int maxSelect;

    public MediaShowGridAdapter(ArrayList<Media> list, Context context, int max) {
        this.maxSelect = max;
        this.medias = list;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        size = context.getResources().getDisplayMetrics().widthPixels / 5;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ADD_TYPE) {
            return new AddHolder(mInflater.inflate(R.layout.nine_add, parent, false));
        } else {
            return new ItemHolder(mInflater.inflate(R.layout.media_pre, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ItemHolder) {

            final Media media = medias.get(position);
            Uri mediaUri = Uri.parse("file://" + media.path);

            Glide.with(context)
                    .load(mediaUri)
                    .into(((ItemHolder) holder).item);

            ((ItemHolder) holder).textview.setText(fileUtils.getSizeByUnit(medias.get(position).size));
        }
    }

    @Override
    public int getItemCount() {
        return medias.size() >= maxSelect ? maxSelect : medias.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (medias.size() >= maxSelect) {
            return ITEM_TYPE;
        } else if (position != getItemCount() - 1) {
            return ITEM_TYPE;
        }
        return ADD_TYPE;
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        ImageView item;
        TextView textview;

        public ItemHolder(View itemView) {
            super(itemView);
            item = (ImageView) itemView.findViewById(R.id.item);
            textview = (TextView) itemView.findViewById(R.id.textview);
            if (onAlbumSelectListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onAlbumSelectListener.onPre(getAdapterPosition());
                    }
                });
            }
        }
    }

    class AddHolder extends RecyclerView.ViewHolder {
        public AddHolder(View itemView) {
            super(itemView);
            if (onAlbumSelectListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onAlbumSelectListener.onInsert(maxSelect - medias.size());
                    }
                });
            }
        }
    }

    public void updateAdapter(ArrayList<Media> list) {
//        this.medias.clear();
        this.medias = list;
        notifyDataSetChanged();
    }

    public void setOnAlbumSelectListener(OnRecyclerViewItemClickListener onAlbumSelectListener) {
        this.onAlbumSelectListener = onAlbumSelectListener;
    }

    public interface OnRecyclerViewItemClickListener {

        void onPre(int position);

        void onInsert(int other);
    }

}
