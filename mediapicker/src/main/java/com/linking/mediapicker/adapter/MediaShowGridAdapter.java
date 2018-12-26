package com.linking.mediapicker.adapter;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.linking.mediapicker.R;
import com.linking.mediapicker.entity.Media;
import com.linking.mediapicker.utils.FileUtils;

import java.util.ArrayList;

/**
 * Created by linking on 2018/7/5.
 */

public class MediaShowGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_TYPE = 100;
    private static final int ADD_TYPE = 101;
    private int size;
    private LayoutInflater mInflater;
    private OnRecyclerViewItemClickListener onAlbumSelectListener;

    private ArrayList<Media> medias;
    private Context context;
    private FileUtils fileUtils = new FileUtils();
    private int maxSelect;

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

            // 图片
            if (media.mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                ((ItemHolder) holder).tvMediaType.setVisibility(View.GONE);
                ((ItemHolder) holder).ivMask.setVisibility(View.GONE);
            }
            // 视频
            if (media.mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                ((ItemHolder) holder).tvMediaType.setVisibility(View.VISIBLE);
                ((ItemHolder) holder).ivMask.setVisibility(View.VISIBLE);
            }
            // 文件大小
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

    private class ItemHolder extends RecyclerView.ViewHolder {

        ImageView item;
        ImageView ivDelete;
        RelativeLayout media_info;
        ImageView tvMediaType;
        ImageView ivMask;
        TextView textview;

        private ItemHolder(View itemView) {
            super(itemView);
            item = (ImageView) itemView.findViewById(R.id.item);
            ivDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
            media_info = (RelativeLayout) itemView.findViewById(R.id.media_info);
            textview = (TextView) itemView.findViewById(R.id.textview);
            ivMask = (ImageView) itemView.findViewById(R.id.iv_mask);
            tvMediaType = (ImageView) itemView.findViewById(R.id.tv_media_type);
            itemView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, size)); //让图片是个正方形
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    medias.remove(getAdapterPosition());
                    notifyDataSetChanged();
                }
            });
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

    private class AddHolder extends RecyclerView.ViewHolder {
        private AddHolder(View itemView) {
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
