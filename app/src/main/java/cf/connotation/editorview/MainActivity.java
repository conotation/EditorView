package cf.connotation.editorview;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cf.connotation.editorview.databinding.ActivityMainBinding;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends BaseActivity {
    protected ActivityMainBinding binding;
    protected CfView cf;
    private final String TAG = "MainActivity";
    private final int REQUEST_SELECT_PICTURE = 0x01;
    protected int NOW_EDITING = 1;

    private final int EDITING_TEXT = 0;
    private final int EDITING_VIEW = 1;

    private boolean drawFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        init();

        cf = binding.cfview;
        cf.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                try {
                    // TODO: 비트맵 하단부 슬라이드에 넣기
                    cf.currentShow = getLtoB();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        binding.btnStudioMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cf.setLocked(); // 이동 Lock
            }
        });

        binding.btnStudioRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cf.removeCard();    // 개체 삭제
            }
        });

        binding.btnStudioAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {       //TODO 이미지 추가
                drawFlag = true;
                getImage();
            }
        });

        binding.btnStudioPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage(); // 백그라운드 추가
            }
        });

        binding.btnStudioDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {   // TODO 하단부 리스트에 그려주기
                    //getLtoB();   // 일단은 그리기 종료
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        binding.fragCover.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        binding.fragCover.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }

    private void init() {
        List<String> data = new ArrayList<>();
        data.add("Font 1");
        data.add("Font 2");
        data.add("Font 3");
//        FontSpinAdapter fontAdapter = new FontSpinAdapter(this, data);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, data);
        binding.studioSpinner.setAdapter(arrayAdapter);
        binding.studioSpinner.setSelection(0);
    }


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
                try {
                    CropResult(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, "에러 발생", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Using UCrop Library
     */

    private void CropResult(Intent i) throws Exception{
        if (i != null) {
            final Uri uri = UCrop.getOutput(i);
            if (drawFlag) {
                drawFlag = false;
                Bitmap b = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // TODO 이미지 카드 만들기
                final ImageView iv = (ImageView) getLayoutInflater().inflate(R.layout.item_inner_drawable, null);
                iv.setImageBitmap(b);
                iv.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (cf.getLocked())
                            return false;
                        switch (event.getAction()) {
                            case 0:
                                cf.setFlag(true, iv);
                                break;
                            case 1:
                                cf.setFlag(false);
                                break;
                        }
                        return true;
                    }
                });
                cf.addDrawCard(iv, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                return;
            }
                Bitmap b = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                cf.setCardBackground(b);
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

    /**
     * Layout to Image
     */

    public Bitmap getLtoB() {
//        Bitmap snapshot = null;       //TODO 누군가 NullPointer를 해결해주세요
//        cf.buildDrawingCache(true);
//        snapshot = Bitmap.createBitmap(cf.getDrawingCache());
//        cf.setDrawingCacheEnabled(false);

        Bitmap snapshot = Bitmap.createBitmap(cf.getWidth(), cf.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(snapshot);
        cf.draw(canvas);
        return snapshot;
    }


}
