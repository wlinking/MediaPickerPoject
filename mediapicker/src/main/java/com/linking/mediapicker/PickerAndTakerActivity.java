package com.linking.mediapicker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hyf.takephotovideolib.support.TakePhotoVideoHelper;
import com.linking.mediapicker.adapter.FolderAdapter;
import com.linking.mediapicker.adapter.MyMediaGridAdapter;
import com.linking.mediapicker.adapter.SpacingDecoration;
import com.linking.mediapicker.data.DataCallback;
import com.linking.mediapicker.data.ImageLoader;
import com.linking.mediapicker.data.MediaLoader;
import com.linking.mediapicker.data.VideoLoader;
import com.linking.mediapicker.entity.Folder;
import com.linking.mediapicker.entity.Media;
import com.linking.mediapicker.utils.ScreenUtils;

import java.io.File;
import java.util.ArrayList;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * 选择或拍照/视频
 * <p>
 * Created by Linking on 2018/12/25
 */
public class PickerAndTakerActivity extends Activity implements DataCallback, View.OnClickListener,
        MyMediaGridAdapter.OnRecyclerViewItemClickListener {

    ArrayList<Media> medias;
    ArrayList<Media> selects;
    Intent argsIntent;
    RecyclerView recyclerView;
    Button done, category_btn, preview;
    MyMediaGridAdapter gridAdapter;
    ListPopupWindow mFolderPopupWindow;
    private FolderAdapter mFolderAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        argsIntent = getIntent();
        setContentView(R.layout.main);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        findViewById(R.id.btn_back).setOnClickListener(this);
        setTitleBar();
        done = (Button) findViewById(R.id.done);
        category_btn = (Button) findViewById(R.id.category_btn);
        preview = (Button) findViewById(R.id.preview);
        done.setOnClickListener(this);
        category_btn.setOnClickListener(this);
        preview.setOnClickListener(this);
        //get view end
        createAdapter();
        createFolderAdapter();
        getMediaData();

        savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + getPackageName();
    }

    public void setTitleBar() {
        int type = argsIntent.getIntExtra(PickerConfig.SELECT_MODE, PickerConfig.PICKER_IMAGE_VIDEO);
        if (type == PickerConfig.PICKER_IMAGE_VIDEO) {
            ((TextView) findViewById(R.id.bar_title)).setText(getString(R.string.select_title));
        } else if (type == PickerConfig.PICKER_IMAGE) {
            ((TextView) findViewById(R.id.bar_title)).setText(getString(R.string.select_image_title));
        } else if (type == PickerConfig.PICKER_VIDEO) {
            ((TextView) findViewById(R.id.bar_title)).setText(getString(R.string.select_video_title));
        }
    }

    private static int singleMaxTime = 0;

    void createAdapter() {
        //创建默认的线性LayoutManager
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, PickerConfig.GridSpanCount);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new SpacingDecoration(PickerConfig.GridSpanCount, PickerConfig.GridSpace));
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        recyclerView.setHasFixedSize(true);
        //创建并设置Adapter
        medias = new ArrayList<>();
        selects = argsIntent.getParcelableArrayListExtra(PickerConfig.DEFAULT_SELECTED_LIST);
        int maxSelect = argsIntent.getIntExtra(PickerConfig.MAX_SELECT_COUNT, PickerConfig.DEFAULT_SELECTED_MAX_COUNT);
        singleMaxTime = argsIntent.getIntExtra(PickerConfig.MAX_VIDEO_TIME, PickerConfig.DEFAULT_VIDEO_TIME);
        long maxSingleSize = argsIntent.getIntExtra(PickerConfig.MAX_MEDIA_SIZE, PickerConfig.DEFAULT_MEDIA_SIZE);
        gridAdapter = new MyMediaGridAdapter(medias, this, selects, maxSelect, singleMaxTime, maxSingleSize);
        gridAdapter.setShowCamera(true);
        recyclerView.setAdapter(gridAdapter);
    }

    void createFolderAdapter() {
        ArrayList<Folder> folders = new ArrayList<>();
        mFolderAdapter = new FolderAdapter(folders, this);
        mFolderPopupWindow = new ListPopupWindow(this);
        mFolderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mFolderPopupWindow.setAdapter(mFolderAdapter);
        mFolderPopupWindow.setHeight((int) (ScreenUtils.getScreenHeight(this) * 0.6));
        mFolderPopupWindow.setAnchorView(findViewById(R.id.footer));
        mFolderPopupWindow.setModal(true);
        mFolderPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mFolderAdapter.setSelectIndex(position);
                category_btn.setText(mFolderAdapter.getItem(position).name);
                gridAdapter.updateAdapter(mFolderAdapter.getSelectMedias());
                mFolderPopupWindow.dismiss();
            }
        });
    }

    @AfterPermissionGranted(119)
    void getMediaData() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            int type = argsIntent.getIntExtra(PickerConfig.SELECT_MODE, PickerConfig.PICKER_IMAGE_VIDEO);
            if (type == PickerConfig.PICKER_IMAGE_VIDEO) {
                getLoaderManager().initLoader(type, null, new MediaLoader(this, this));
            } else if (type == PickerConfig.PICKER_IMAGE) {
                getLoaderManager().initLoader(type, null, new ImageLoader(this, this));
            } else if (type == PickerConfig.PICKER_VIDEO) {
                getLoaderManager().initLoader(type, null, new VideoLoader(this, this));
            }
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.READ_EXTERNAL_STORAGE), 119, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onData(ArrayList<Folder> list) {
        setView(list);
        category_btn.setText(list.get(0).name);
        mFolderAdapter.updateAdapter(list);
    }

    void setView(ArrayList<Folder> list) {
        medias = list.get(0).getMedias();
        // TODO 重新拍的视频加入selector，需在TakePhotoVideoLib 将 video保存到内存中才彻底解决
        ArrayList<Media> selectMedias = gridAdapter.getSelectMedias();
        for (Media media : selectMedias) {
            if (!isDuplicated(medias, media))
                medias.add(0, media);
        }
        gridAdapter.updateAdapter(medias);
        gridAdapter.updateSelectAdapter(gridAdapter.getSelectMedias());
        setButtonText();
        gridAdapter.setOnAlbumSelectListener(this);
    }

    // 判断已经包含，因为 TakePhotoVideoLib 未将 video保存到内存中
    private boolean isDuplicated(ArrayList<Media> medias, Media media) {
        for (Media me : medias) {
            if (me.path.equals(media.path))
                return true;
        }
        return false;
    }

    void setButtonText() {
        int max = argsIntent.getIntExtra(PickerConfig.MAX_SELECT_COUNT, PickerConfig.DEFAULT_SELECTED_MAX_COUNT);
        String doneStr = getString(R.string.done) + "(" + gridAdapter.getSelectMedias().size() + "/" + max + ")";
        done.setText(doneStr);
        String previewStr = getString(R.string.preview) + "(" + gridAdapter.getSelectMedias().size() + ")";
        preview.setText(previewStr);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            done();
        } else if (id == R.id.category_btn) {
            if (mFolderPopupWindow.isShowing()) {
                mFolderPopupWindow.dismiss();
            } else {
                mFolderPopupWindow.show();
            }
        } else if (id == R.id.done) {
            done();
        } else if (id == R.id.preview) {
            if (gridAdapter.getSelectMedias().size() <= 0) {
                Toast.makeText(this, getString(R.string.select_null), Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(this, PreviewActivity.class);
            intent.putExtra(PickerConfig.MAX_SELECT_COUNT, argsIntent.getIntExtra(
                    PickerConfig.MAX_SELECT_COUNT, PickerConfig.DEFAULT_SELECTED_MAX_COUNT));
            intent.putExtra(PickerConfig.PRE_RAW_LIST, gridAdapter.getSelectMedias());
            this.startActivityForResult(intent, PickerConfig.REQUEST_CODE_OK);
        }
    }

    public void done() {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(PickerConfig.EXTRA_RESULT, gridAdapter.getSelectMedias());
        setResult(PickerConfig.RESULT_CODE, intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        Glide.get(this).clearMemory();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        done();
        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null == data) return;
        final ArrayList<Media> select = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);
        if (requestCode == PickerConfig.REQUEST_CODE_OK) {
            gridAdapter.updateSelectAdapter(select);
            setButtonText();
        } else if (requestCode == RC_OPEN_TAKE_PHOTO_VIDEO && resultCode == RESULT_OK) {
            String path = data.getStringExtra(TakePhotoVideoHelper.RESULT_DATA);
            final File photo = new File(path);
            int mediaType = 3; //video
            if (path.contains(".jpg") || path.contains(".jpeg") || path.contains(".png")) {
                mediaType = 1; //image
            }
            final Media media = new Media(photo.getPath(), photo.getName(), System.currentTimeMillis(),
                    mediaType, photo.length(), 0, "");
            gridAdapter.getSelectMedias().add(media);
            ArrayList<Media> listMedia = gridAdapter.getMedias();
            listMedia.add(0, media); //错误所在，直接add不行，需要定位
            gridAdapter.updateAdapter(listMedia);
            setButtonText();
        } else if (requestCode == PickerConfig.REQUEST_CODE_TAKE && PickerConfig.RESULT_CODE == resultCode) {
            String path = data.getStringExtra(TakePhotoVideoHelper.RESULT_DATA);
            final File photo = new File(path);
            int mediaType = 3; //video
            if (path.contains(".jpg") || path.contains(".jpeg") || path.contains(".png")) {
                mediaType = 1; //image
            }
            final Media media = new Media(photo.getPath(), photo.getName(), System.currentTimeMillis(),
                    mediaType, photo.length(), 0, "");
            gridAdapter.getSelectMedias().add(media);
            ArrayList<Media> listMedia = gridAdapter.getMedias();
            listMedia.add(0, media); //错误所在，直接add不行，需要定位
            gridAdapter.updateAdapter(listMedia);
            setButtonText();
        }
    }

    @Override
    public void onCamera() {
        int max = argsIntent.getIntExtra(PickerConfig.MAX_SELECT_COUNT, PickerConfig.DEFAULT_SELECTED_MAX_COUNT);
        if (gridAdapter.getSelectMedias().size() >= max) {
            Toast.makeText(this, R.string.msg_amount_limit, Toast.LENGTH_SHORT).show();
            return;
        }
        int type = argsIntent.getIntExtra(PickerConfig.SELECT_MODE, PickerConfig.PICKER_IMAGE_VIDEO);
        if (type == PickerConfig.PICKER_IMAGE) {
            Intent intent = new Intent(this, TakePhotoActivity.class);
            startActivityForResult(intent, PickerConfig.REQUEST_CODE_TAKE);
        } else {
            // 拍照+录像
            startRecordPhotoVideo();
        }
    }

    @Override
    public void onItemClick(View view, Media data, ArrayList<Media> selectMedias) {
        setButtonText();
    }

    String savePath;
    String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO};

    private static final int requestCode = 113;
    private static final int RC_OPEN_TAKE_PHOTO_VIDEO = 100;

    @AfterPermissionGranted(requestCode)
    private void startRecordPhotoVideo() {
        if (EasyPermissions.hasPermissions(this, permission))
            TakePhotoVideoHelper.startTakePhotoVideo(this, RC_OPEN_TAKE_PHOTO_VIDEO, savePath,
                    singleMaxTime == 0 ? 10000 : singleMaxTime * 1000);
        else
            EasyPermissions.requestPermissions(this, "申请获取相关权限", requestCode, permission);
    }

}
