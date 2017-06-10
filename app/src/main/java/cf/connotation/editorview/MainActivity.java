package cf.connotation.editorview;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import cf.connotation.editorview.databinding.ActivityMainBinding;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends BaseActivity {
    protected ActivityMainBinding binding;
    protected CfView cf;
    private final int REQUEST_SELECT_PICTURE = 0x01;
    private String TAG = "MainActivity";
    protected int NOW_EDITING = 1;

    private final int DOWNLOAD_NOTIFICATION_ID_DONE = 0x02;
    private final int EDITING_TEXT = 0;
    private final int EDITING_VIEW = 1;

    @Override
    public void onBackPressed() {
        if (NOW_EDITING == EDITING_TEXT) {
            binding.editorHigh.setVisibility(GONE);
            binding.editorLow.setVisibility(VISIBLE);
            NOW_EDITING = EDITING_VIEW;
        } else {
            showAlertDialog(getString(R.string.app_name), getString(R.string.close_text),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.super.onBackPressed();
                        }
                    }, getString(R.string.label_ok), null, getString(R.string.label_cancel));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Stetho.initializeWithDefaults(this);
        cf = binding.cfview;

        binding.btnStudioMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cf.setLocked();
            }
        });

        binding.btnStudioRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cf.removeCard();
            }
        });

        binding.btnStudioAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {       // + 테스트 버튼

            }
        });

        binding.btnStudioPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage();
            }
        });

        binding.btnStudioText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.editorLow.setVisibility(GONE);
                binding.editorHigh.setVisibility(VISIBLE);
                NOW_EDITING = EDITING_TEXT;
            }
        });

        binding.btnStudioTextAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final TextView tv = (TextView) getLayoutInflater().inflate(R.layout.item_inner_text, null);
                tv.setText("for Test");
                tv.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (cf.getLocked())
                            return false;
                        switch (event.getAction()) {
                            case 0:
                                cf.setFlag(true, tv);
                                break;
                            case 1:
                                cf.setFlag(false);
                                break;
                        }
                        return true;
                    }
                });
                cf.addCard(tv, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        });


    }

    /**
     * Gallery Intent
     */

    public void getImage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    getString(R.string.permission_read_storage_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, getString(R.string.label_select_picture)), REQUEST_SELECT_PICTURE);
        }
    }

    /**
     * UCrop Run Page
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SELECT_PICTURE) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    startCropActivity(data.getData());
                } else {
                    Toast.makeText(getApplicationContext(), R.string.toast_cannot_retrieve_selected_image, Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                CropResult(data);
            }
        }
        if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, "에러 발생", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Using UCrop Library
     */

    private void CropResult(Intent i) {
        if (i != null) {
            final Uri uri = UCrop.getOutput(i);
            try {
                saveCroppedImage(uri);
                cf.setCardBackground();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "이미지 에러 [002]", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "이미지 에러 [001]", Toast.LENGTH_SHORT).show();
        }
    }

    private void startCropActivity(@NonNull Uri uri) {
        String destinationFileName = "0x10x20x30x4.png";    // 의미없음

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
        uCrop = uCrop.withAspectRatio(1, 1);

        uCrop.start(MainActivity.this);
    }

    /**
     * 이미지 다운로드
     */

    private void saveCroppedImage(Uri uri) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    getString(R.string.permission_write_storage_rationale),
                    REQUEST_STORAGE_WRITE_ACCESS_PERMISSION);
        } else {
            if (uri != null && uri.getScheme().equals("file")) {
                try {
                    copyFileToDownloads(uri);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, uri.toString(), e);
                }
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.toast_unexpected_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void copyFileToDownloads(Uri croppedFileUri) throws Exception {
        String downloadsDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Cardline/";
        String filename = getString(R.string.background_resource);

        File polder = new File(downloadsDirectoryPath);
        if (!polder.exists())
            polder.mkdir();
        File saveFile = new File(downloadsDirectoryPath + filename);
        if (saveFile.exists())
            saveFile.delete();
        saveFile.createNewFile();

        FileInputStream inStream = new FileInputStream(new File(croppedFileUri.getPath()));
        FileOutputStream outStream = new FileOutputStream(saveFile);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();

    }
}
