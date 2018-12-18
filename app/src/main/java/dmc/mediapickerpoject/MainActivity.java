package dmc.mediapickerpoject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.dmcbig.mediapicker.PickerActivity;
import com.dmcbig.mediapicker.PickerConfig;
import com.dmcbig.mediapicker.TakePhotoActivity;
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
    long maxSize = 180 * 1024 * 1024L; //文件大小，默认 180MB
    int maxSelected = 5;
    int requestCodeOK = 200;

    void createAdapter() {
        //创建默认的线性LayoutManager
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 5);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new SpacingDecoration(5, PickerConfig.GridSpace));
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        //创建并设置Adapter
        ArrayList<Media> medias = new ArrayList<>();
        gridAdapter = new MediaShowGridAdapter(medias, this, maxSelected);
        recyclerView.setAdapter(gridAdapter);
        gridAdapter.setOnAlbumSelectListener(new MediaShowGridAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onPre(int position) {
                //TODO 预览
            }

            @Override
            public void onInsert(int other) {
                go();
            }
        });
    }

    void go() {

        Intent intent = new Intent(MainActivity.this, PickerActivity.class);
        intent.putExtra(PickerConfig.SELECT_MODE, PickerConfig.PICKER_IMAGE_VIDEO);//选择类型 image and video

        intent.putExtra(PickerConfig.MAX_SELECT_SIZE, maxSize); // 文件大小，默认 180MB
        intent.putExtra(PickerConfig.MAX_SELECT_COUNT, maxSelected);  // 默认 选择数
        intent.putExtra(PickerConfig.DEFAULT_SELECTED_LIST, select); // 默认选中参数
        MainActivity.this.startActivityForResult(intent, requestCodeOK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == PickerConfig.RESULT_CODE) {
            select = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);
            Log.i("select", "select.size" + select.size());
            gridAdapter.updateAdapter(select);
        }
    }
}
