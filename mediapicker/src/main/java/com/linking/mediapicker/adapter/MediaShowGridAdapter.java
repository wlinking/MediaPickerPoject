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
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.linking.mediapicker.R;
import com.linking.mediapicker.entity.Media;
import com.linking.mediapicker.utils.ScreenUtils;

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
    private UpdateFileSizeListener updateFileSizeListener;

    private ArrayList<Media> medias;
    private Context context;
    private int maxSelect;

    public MediaShowGridAdapter(ArrayList<Media> list, Context context, int max) {
        this.maxSelect = max;
        this.medias = list;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        //给出最低宽度，30为控件两端边距
        int width = (ScreenUtils.getScreenWidth(context) - ScreenUtils.dp2px(context,30))
                < ScreenUtils.dp2px(context,320) ? ScreenUtils.dp2px(context,320)
                : ScreenUtils.getScreenWidth(context) - ScreenUtils.dp2px(context,30);
        size = width / 5;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ADD_TYPE) {
            View addHoldeView = mInflater.inflate(R.layout.nine_add, parent, false);
            addHoldeView.setLayoutParams(new LinearLayout.LayoutParams(size, size));
            addHoldeView.setPadding(ScreenUtils.dp2px(context,5),
                    ScreenUtils.dp2px(context,5),
                    ScreenUtils.dp2px(context,5),
                    ScreenUtils.dp2px(context,5));
            return new AddHolder(addHoldeView);
        } else {
            return new ItemHolder(mInflater.inflate(R.layout.media_pre, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof ItemHolder) {

            final Media media = medias.get(position);
            Uri mediaUri = Uri.parse("file://" + media.path);

            Glide.with(context)
                    .load(mediaUri)
                    .into(((ItemHolder) holder).item);

            // 图片
            if (media.mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                ((ItemHolder) holder).ivMask.setVisibility(View.GONE);
            }
            // 视频
            if (media.mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                ((ItemHolder) holder).ivMask.setVisibility(View.VISIBLE);
            }

            ((ItemHolder) holder).ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (updateFileSizeListener != null) {
                        updateFileSizeListener.delete(getItemFileSize(position));
                    }

                    medias.remove(position);
                    notifyDataSetChanged();
                }
            });

            ((ItemHolder) holder).item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onAlbumSelectListener != null) {
                        onAlbumSelectListener.onPre(position);
                    }
                }
            });
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
        ImageView ivMask;

        private ItemHolder(View itemView) {
            super(itemView);
            item = (ImageView) itemView.findViewById(R.id.item);
            ivDelete = (ImageView) itemView.findViewById(R.id.iv_delete);
            ivMask = (ImageView) itemView.findViewById(R.id.iv_mask);
            itemView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, size));
        }
    }

    private class AddHolder extends RecyclerView.ViewHolder {
        private AddHolder(final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onAlbumSelectListener != null) {
                        onAlbumSelectListener.onInsert(maxSelect - medias.size());
                    }
                }
            });
        }
    }

    public void updateAdapter(ArrayList<Media> list) {
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

    private double getItemFileSize(int position) {
        double itemFileSize = 0.0;
        if (medias.get(position) != null)
            itemFileSize = medias.get(position).size;
        return itemFileSize;
    }

    public void setUpdateFileSizeListener(UpdateFileSizeListener updateFileSizeListener) {
        this.updateFileSizeListener = updateFileSizeListener;
    }

    public interface UpdateFileSizeListener {
        void delete(double size);
    }

}
