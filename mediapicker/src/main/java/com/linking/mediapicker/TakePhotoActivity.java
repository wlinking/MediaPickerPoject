package com.linking.mediapicker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.hyf.takephotovideolib.support.TakePhotoVideoHelper;
import com.linking.mediapicker.entity.Media;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by linking on 2018/11/14.
 */

public class TakePhotoActivity extends Activity {
    Uri NuriForFile;
    File mTmpFile = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            mTmpFile = createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//如果大于等于7.0使用FileProvider
            NuriForFile = FileProvider.getUriForFile(this, this.getPackageName() + ".dmc", mTmpFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, NuriForFile);
            startActivityForResult(intent, 100);
        } else {
            if (mTmpFile != null && mTmpFile.exists()) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
                startActivityForResult(intent, 101);
            } else {
                Toast.makeText(this, "take error", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (0 != resultCode) {//过滤未拍照返回情况
            ArrayList<Media> medias = new ArrayList<>();
            if (requestCode == 100 || requestCode == 101 && resultCode == RESULT_OK && mTmpFile.length() > 0) {
                Media media = new Media(mTmpFile.getPath(), mTmpFile.getName(), 0, 1, mTmpFile.length(), 0, "");
                medias.add(media);
            }

            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(PickerConfig.EXTRA_RESULT, medias);
            intent.putExtra(TakePhotoVideoHelper.RESULT_DATA, medias.get(medias.size() - 1).path);
            setResult(PickerConfig.RESULT_CODE, intent);
        }
        finish();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

}