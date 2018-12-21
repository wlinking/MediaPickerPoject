package dmc.mediapickerpoject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.dmcbig.mediapicker.PickerActivity;
import com.dmcbig.mediapicker.PickerConfig;
import com.dmcbig.mediapicker.PreviewActivity;
import com.dmcbig.mediapicker.adapter.MediaShowGridAdapter;
import com.dmcbig.mediapicker.adapter.SpacingDecoration;
import com.dmcbig.mediapicker.entity.Media;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MediaShowGridAdapter gridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        createAdapter();
    }

    ArrayList<Media> select;

    void createAdapter() {
        //创建默认的线性LayoutManager
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, PickerConfig.DEFAULT_SELECTED_MAX_COUNT);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new SpacingDecoration(PickerConfig.DEFAULT_SELECTED_MAX_COUNT, PickerConfig.GridSpace));
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        //创建并设置Adapter
        ArrayList<Media> medias = new ArrayList<>();
        gridAdapter = new MediaShowGridAdapter(medias, this, PickerConfig.DEFAULT_SELECTED_MAX_COUNT);
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
    }

    /**
     * 开始选择 media
     */
    void toPickMedias() {

        Intent intent = new Intent(MainActivity.this, PickerActivity.class);
        intent.putExtra(PickerConfig.SELECT_MODE, PickerConfig.PICKER_IMAGE_VIDEO);

        intent.putExtra(PickerConfig.MAX_SELECT_SIZE, PickerConfig.DEFAULT_SELECTED_MAX_SIZE);
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
            }/* 暂时只 点完成 按钮才更新gridAdapter的select
             else if (resultCode == PickerConfig.RESULT_UPDATE_CODE) {
                gridAdapter.updateAdapter(select);
            }*/
        }
    }
}
