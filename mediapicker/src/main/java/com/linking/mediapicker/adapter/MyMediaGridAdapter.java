package com.linking.mediapicker.adapter;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.linking.mediapicker.PickerConfig;
import com.linking.mediapicker.R;
import com.linking.mediapicker.entity.Media;
import com.linking.mediapicker.utils.FileUtils;
import com.linking.mediapicker.utils.ScreenUtils;
import com.linking.mediapicker.utils.TimerUtils;

import java.util.ArrayList;

/**
 * Created by linking on 2018/7/5.
 */

public class MyMediaGridAdapter extends RecyclerView.Adapter<MyMediaGridAdapter.MyViewHolder> {

    private static final int CAMERA_TYPE = 1000;
    private static final int ITEM_TYPE = 1001;
    private int size;
    private boolean showCamera;
    private LayoutInflater mInflater;
    private OnRecyclerViewItemClickListener onAlbumSelectListener;

    private ArrayList<Media> medias;
    private Context context;
    private ArrayList<Media> selectMedias = new ArrayList<>();
    private long maxSelect, maxSize;
    private int maxTime;

    public MyMediaGridAdapter(ArrayList<Media> list, Context context, ArrayList<Media> select,
                              int max, int maxTime, long maxSize) {
        if (select != null) {
            this.selectMedias = select;
        }
        this.maxSelect = max;
        this.maxTime = maxTime;
        this.maxSize = maxSize;
        this.medias = list;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        size = context.getResources().getDisplayMetrics().widthPixels / 3;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView media_image, check_image;
        public View mask_view;
        public TextView tv_time;
        public RelativeLayout video_info;

        private MyViewHolder(View view) {
            super(view);
            media_image = (ImageView) view.findViewById(R.id.media_image);
//            iv_mask = (ImageView) view.findViewById(R.id.iv_mask);
            check_image = (ImageView) view.findViewById(R.id.check_image);
            mask_view = view.findViewById(R.id.mask_view);
            video_info = (RelativeLayout) view.findViewById(R.id.video_info);
            tv_time = (TextView) view.findViewById(R.id.tv_time);
            itemView.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getItemWidth())); //让图片是个正方形
        }
    }

    private class CameraHolder extends MyViewHolder {
        private CameraHolder(View itemView) {
            super(itemView);
            itemView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
        }
    }

    private int getItemWidth() {
        return (ScreenUtils.getScreenWidth(context) / PickerConfig.GridSpanCount) - PickerConfig.GridSpanCount;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if (viewType == CAMERA_TYPE) {
            return new CameraHolder(mInflater.inflate(R.layout.album_camera_layout, viewGroup, false));
        }
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.media_view_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        if (holder instanceof CameraHolder) {
            ((CameraHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onAlbumSelectListener.onCamera();
                }
            });
            return;
        }

        final Media media = showCamera ? medias.get(position - 1) : medias.get(position);
        Uri mediaUri = Uri.parse("file://" + media.path);

        Glide.with(context).load(mediaUri).into(holder.media_image);

        if (media.mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
            holder.video_info.setVisibility(View.VISIBLE);
            holder.tv_time.setText(TimerUtils.getTime(FileUtils.getVideoTime(media.path)));
        } else {
            holder.video_info.setVisibility(View.GONE);
        }

        int isSelect = isSelect(media);
        holder.mask_view.setVisibility(isSelect >= 0 ? View.VISIBLE : View.INVISIBLE);
        holder.check_image.setImageDrawable(isSelect >= 0 ? ContextCompat.getDrawable(context, R.drawable.btn_selected) : ContextCompat.getDrawable(context, R.drawable.btn_unselected));

        holder.media_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int isSelect = isSelect(media);
                if (selectMedias.size() >= maxSelect && isSelect < 0) {
                    Toast.makeText(context, context.getString(R.string.msg_amount_limit), Toast.LENGTH_SHORT).show();
                } else {
                    if (media.size > maxSize) {
                        String sizeHint = "请勿上传超过" + FileUtils.fileSize(maxSize) + "的文件哦";
                        Toast.makeText(context, sizeHint, Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (media.mediaType == 3 && FileUtils.getVideoTime(media.path) > maxTime) {
                        String hintStr = context.getString(R.string.msg_size_limit) + maxTime + "秒";
                        Toast.makeText(context, hintStr, Toast.LENGTH_LONG).show();
                    } else {
                        holder.mask_view.setVisibility(isSelect >= 0 ? View.INVISIBLE : View.VISIBLE);
                        holder.check_image.setImageDrawable(isSelect >= 0 ? ContextCompat.getDrawable(context, R.drawable.btn_unselected) : ContextCompat.getDrawable(context, R.drawable.btn_selected));
                        setSelectMedias(media);
                        onAlbumSelectListener.onItemClick(v, media, selectMedias);
                    }
                }
            }
        });
    }

    private void setSelectMedias(Media media) {
        int index = isSelect(media);
        if (index == -1) {
            selectMedias.add(media);
        } else {
            selectMedias.remove(index);
        }
    }

    /**
     * @param media media
     * @return 大于等于0 就是表示以选择，返回的是在selectMedias中的下标
     */
    private int isSelect(Media media) {
        int is = -1;
        if (selectMedias.size() <= 0) {
            return is;
        }
        for (int i = 0; i < selectMedias.size(); i++) {
            Media m = selectMedias.get(i);
            if (m.path.equals(media.path)) {
                is = i;
                break;
            }
        }
        return is;
    }

    public void updateSelectAdapter(ArrayList<Media> select) {
        if (select != null) {
            this.selectMedias = select;
        }
        notifyDataSetChanged();
    }

    public void updateAdapter(ArrayList<Media> list) {
        this.medias = list;
        notifyDataSetChanged();
    }

    public ArrayList<Media> getMedias() {
        return medias;
    }

    public ArrayList<Media> getSelectMedias() {
        return selectMedias;
    }

    @Override
    public int getItemCount() {
        return showCamera ? medias.size() + 1 : medias.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (showCamera && position == 0) {
            return CAMERA_TYPE;
        }
        return ITEM_TYPE;
    }

    /**
     * 切换时设置是否显示首格拍照
     *
     * @param showCamera boolean
     */
    public void setShowCamera(boolean showCamera) {
        this.showCamera = showCamera;
    }

    public void setOnAlbumSelectListener(OnRecyclerViewItemClickListener onAlbumSelectListener) {
        this.onAlbumSelectListener = onAlbumSelectListener;
    }

    public interface OnRecyclerViewItemClickListener {
        //点击拍照
        void onCamera();

        //点击 item
        void onItemClick(View view, Media data, ArrayList<Media> selectMedias);
    }

}
