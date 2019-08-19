package com.linking.mediapickerpoject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.linking.mediapicker.PickerAndTakerActivity;
import com.linking.mediapicker.PickerConfig;
import com.linking.mediapicker.PreviewActivity;
import com.linking.mediapicker.adapter.MediaShowGridAdapter;
import com.linking.mediapicker.adapter.SpacingDecoration;
import com.linking.mediapicker.entity.Media;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MediaShowGridAdapter.UpdateFileSizeListener {

    RecyclerView recyclerView;
    MediaShowGridAdapter gridAdapter;

    private TextView mFileSize;
    private double defaultFileSize = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        createAdapter();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mFileSize = (TextView) findViewById(R.id.tv_file_size);
    }

    ArrayList<Media> select;

    void createAdapter() {
        //创建默认的线性LayoutManager
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, PickerConfig.DEFAULT_SCREEN_SPLIT_COUNT);//此处将屏幕宽度分为5分，可调整，但不是选择的文件数量
        recyclerView.setLayoutManager(mLayoutManager);
        //创建过程中有3处DEFAULT_SELECTED_MAX_COUNT，表明选择的文件数量，可统一设置
        recyclerView.addItemDecoration(new SpacingDecoration(PickerConfig.DEFAULT_SELECTED_MAX_COUNT, PickerConfig.GridSpace));
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        ArrayList<Media> medias = new ArrayList<>();
        gridAdapter = new MediaShowGridAdapter(medias, this, PickerConfig.DEFAULT_SELECTED_MAX_COUNT, PickerConfig.DEFAULT_ADD_ICO);
        recyclerView.setAdapter(gridAdapter);
        gridAdapter.setOnAlbumSelectListener(new MediaShowGridAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onPre(int position) {
                Intent intent = new Intent(MainActivity.this, PreviewActivity.class);
                intent.putExtra(PickerConfig.MAX_SELECT_COUNT, PickerConfig.DEFAULT_SELECTED_MAX_COUNT);
                intent.putExtra(PickerConfig.PRE_RAW_LIST, select);
                intent.putExtra(PickerConfig.PRE_IMG_NUM, position);
                MainActivity.this.startActivityForResult(intent, PickerConfig.REQUEST_CODE_OK);
            }

            @Override
            public void onInsert(int other) {
                toPickMedias();
            }
        });
        gridAdapter.setUpdateFileSizeListener(this);
    }

    /**
     * 开始选择 media
     */
    void toPickMedias() {

        Intent intent = new Intent(MainActivity.this, PickerAndTakerActivity.class);
        intent.putExtra(PickerConfig.SELECT_MODE, PickerConfig.PICKER_IMAGE_VIDEO);

        intent.putExtra(PickerConfig.MAX_VIDEO_TIME, PickerConfig.DEFAULT_VIDEO_TIME);
        intent.putExtra(PickerConfig.MAX_SELECT_COUNT, PickerConfig.DEFAULT_SELECTED_MAX_COUNT);
        intent.putExtra(PickerConfig.DEFAULT_SELECTED_LIST, select);
        MainActivity.this.startActivityForResult(intent, PickerConfig.REQUEST_CODE_OK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PickerConfig.REQUEST_CODE_OK) {

            select = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);
            Log.i("select", "select.size" + select.size());
            if (resultCode == PickerConfig.RESULT_CODE) {
                gridAdapter.updateAdapter(select);
                updateFileLimitSize(select);
            }
        }
    }

    @Override
    public void delete(double size) {
        updateFileLimitSizeText(size, false);
    }

    private void updateFileLimitSize(List<Media> selectMediaList) {
        long allSize = 0;
        for (Media media : selectMediaList) {
            allSize += media.size;
        }

        updateFileLimitSizeText(allSize, true);
    }

    private void updateFileLimitSizeText(double size, boolean isAdd) {
        if (isAdd) {
            defaultFileSize = size / 1024.0 / 1024.0;
        } else {
            defaultFileSize = defaultFileSize - size / 1024.0 / 1024.0;
            defaultFileSize = defaultFileSize > 0 ? defaultFileSize : 0.0;
        }

        BigDecimal bd = new BigDecimal(defaultFileSize);
        Double size2Scale = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        mFileSize.setText(String.valueOf(size2Scale));
    }

}
