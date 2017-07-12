package cf.connotation.editorview;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nitrico.fontbinder.FontBinder;
import com.github.nitrico.lastadapter.Holder;
import com.github.nitrico.lastadapter.ItemType;
import com.github.nitrico.lastadapter.LastAdapter;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cf.connotation.editorview.databinding.ActivityMainBinding;
import cf.connotation.editorview.databinding.StudioFragViewBinding;
import cf.connotation.editorview.databinding.StudioLastFooterBinding;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends BaseActivity {
    protected ActivityMainBinding binding;
    protected CfView cfv;
    protected LastAdapter adapter;

    private final String TAG = "MainActivityCf";

    private final int REQUEST_SELECT_PICTURE = 0x01;
    protected int NOW_EDITING = 1;
    private boolean _dialog = false;
    private String _current_color = "#000000";

    private final int EDITING_TEXT = 0;
    private final int EDITING_VIEW = 1;

    private boolean drawFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        init();

        cfv.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                try {
                    // TODO: 비트맵 하단부 슬라이드에 넣기
                    cfv.currentShow = getLtoB();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        binding.btnStudioMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cfv.setLocked(); // 이동 Lock
            }
        });

        binding.btnStudioRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cfv.removeCard();    // 개체 삭제
            }
        });

        binding.btnStudioAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawFlag = true;    //
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
                cfv.createIndiFormat();
                adapter.notifyDataSetChanged();
            }
        });

        binding.btnStudioText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.editorLow.setVisibility(GONE);
                binding.editorHigh.setVisibility(VISIBLE);
                if (!cfv.getLocked()) cfv.setLocked();
                NOW_EDITING = EDITING_TEXT;
            }
        });

        binding.btnStudioTextAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final InText tv = new InText(getApplicationContext());
                tv.setText("텍스트를 변경해주세요");
                tv.setTextSize(binding.btnStudioTextSlidesize.getProgress());
                Log.e(TAG, "onClick: " + binding.studioSpinner.getSelectedItemPosition());
                if (binding.studioSpinner.getSelectedItemPosition() == 0) {
                    tv.setTypeface(FontBinder.get("NanumGothic"), "NanumGothic");
                } else {
                    tv.setTypeface(FontBinder.get("NanumSquareB"), "NanumSquareB");
                }
                tv.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (NOW_EDITING == EDITING_TEXT) {
                            if (!_dialog)
                                showXDialog(tv);
                        } else if (cfv.getLocked()) {
                            return false;
                        }

                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                cfv.setFlag(true, tv);
                                break;
                            case MotionEvent.ACTION_UP:
                                cfv.setFlag(false);
                                break;
                        }
                        return true;
                    }
                });
                cfv.addCard(tv, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        });


        binding.btnStudioTextcolor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int cv = cfv.getCVInstance();
                if (cv == -1)
                    Toast.makeText(MainActivity.this, "카드를 선택해주세요", Toast.LENGTH_SHORT).show();
                else if (cv == 0)
                    Toast.makeText(MainActivity.this, "텍스트의 색을 바꾸는 기능입니다", Toast.LENGTH_SHORT).show();
                else {
                    cfv.changeColor(_current_color);   // TODO 파레트 추가 (!)
                }
            }
        });

        binding.btnStudioBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void showXDialog(final TextView tv) {
        LayoutInflater dialog = LayoutInflater.from(this);
        final View dialogLayout = dialog.inflate(R.layout.modi_view, null);
        final Dialog xDialog = new Dialog(this);

        xDialog.setTitle("텍스트 변경");
        xDialog.setContentView(dialogLayout);
        xDialog.show();

        xDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                _dialog = false;
            }
        });

        EditText editModi = dialogLayout.findViewById(R.id.modi_edit);
        editModi.setText(tv.getText().toString());
        _dialog = true;
        Button posbtn = dialogLayout.findViewById(R.id.modi_pos);
        Button negbtn = dialogLayout.findViewById(R.id.modi_neg);

        posbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = ((TextView) dialogLayout.findViewById(R.id.modi_edit)).getText().toString();
                tv.setText(s);
                _dialog = false;
                xDialog.dismiss();
            }
        });

        negbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _dialog = false;
                xDialog.cancel();
            }
        });


    }

    private void init() {
        cfv = binding.cfview;

        List<String> data = new ArrayList<>();
        data.add("NanumGothic");
        data.add("NanumSquareB");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, data);
        binding.studioSpinner.setAdapter(arrayAdapter);
        binding.studioSpinner.setSelection(0);

        binding.cv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        cfv.addPage();

        adapter = new LastAdapter(cfv.getPag().arr, BR.content)
                .map(FooterView.class, new ItemType<StudioLastFooterBinding>(R.layout.studio_last_footer) {
                    @Override
                    public void onBind(Holder<StudioLastFooterBinding> holder) {
                        super.onBind(holder);
                        holder.getBinding().setActivity(MainActivity.this);
                        holder.getBinding().btnPageAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (cfv.getPage() == 11) {
                                    Toast.makeText(MainActivity.this, "최대 페이지는 10p입니다", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                addPage();
                            }
                        });
                    }
                })
                .map(Page.class, new ItemType<StudioFragViewBinding>(R.layout.studio_frag_view) {
                    @Override
                    public void onBind(final Holder<StudioFragViewBinding> holder) {
                        super.onBind(holder);
                        cfv.getPag().arr.get(holder.getLayoutPosition());
                        holder.getBinding().setPosition(holder.getLayoutPosition());
                        if (cfv.getPag().arr.size() == 2) {
                            holder.getBinding().getContent().setSeleceted(true);
                        }
                        holder.getBinding().getRoot().setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (holder.getBinding().getContent().isSeleceted())
                                    return;
                                for (int i = 0; i < cfv.getPag().arr.size(); i++) {
                                    Object o = cfv.getPag().arr.get(i);
                                    if (o instanceof Page) {
                                        Page p = (Page) o;
                                        p.setSeleceted(false);
                                    }
                                }
                                cfv.movePage(holder.getBinding().getPosition() + 1);        // X 페이지로 이동
                                holder.getBinding().getContent().setSeleceted(true);
                                ((Page) cfv.getPag().arr.get(holder.getBinding().getPosition())).setSeleceted(true);
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                })
                .into(binding.cv);
//        alp.add(cfv.addPage());
    }

    @Override
    public void onBackPressed() {
        if (NOW_EDITING == EDITING_TEXT) {
            binding.editorHigh.setVisibility(GONE);
            binding.editorLow.setVisibility(VISIBLE);
            _dialog = false;
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
                    cropResult(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            drawFlag = false;
            if (resultCode == UCrop.RESULT_ERROR) {
                Toast.makeText(this, "에러 발생", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Using UCrop Library
     */

    private void cropResult(Intent i) throws Exception {
        if (i != null) {
            final Uri uri = UCrop.getOutput(i);
            if (drawFlag) {
                drawFlag = false;
                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;

                Bitmap b = getBitmap(getContentResolver(), uri, options);

                // TODO 이미지 카드 만들기
                final LinearLayout layout = new LinearLayout(this);
                final int _width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 360 * b.getWidth() / (b.getWidth() + b.getHeight()), getResources().getDisplayMetrics());
                final int _height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 360 * b.getHeight() / (b.getWidth() + b.getHeight()), getResources().getDisplayMetrics());
                layout.setLayoutParams(new LinearLayoutCompat.LayoutParams(_width, _height));
                BitmapDrawable bm = new BitmapDrawable(this.getResources(), b);
                layout.setBackground(bm);
                layout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (cfv.getLocked())
                            return false;
                        switch (event.getAction() & MotionEvent.ACTION_MASK) {
                            case MotionEvent.ACTION_UP:
                                cfv.MODE = CfView.NONE;
                                cfv.setFlag(false);
                                break;
                            case MotionEvent.ACTION_POINTER_DOWN:
                                cfv.setFlag(true, layout);
                                cfv.MODE = CfView.ZOOM;
                                cfv.onTouchEvent(event);
                                break;
                            case MotionEvent.ACTION_DOWN:
                            case MotionEvent.ACTION_MOVE:
                                cfv.MODE = CfView.MOVE;
                                cfv.setFlag(true, layout);
                                break;
                        }
                        return true;
                    }
                });
                Log.e(TAG, "cropResult: " + b.getWidth() + " / " + b.getHeight());
                cfv.addDrawCard(layout, null, b);
            } else {
                Bitmap b = getBitmap(getContentResolver(), uri);
                cfv.setCardBackground(b);
            }
        } else {
            Toast.makeText(this, "이미지 에러 [001]", Toast.LENGTH_SHORT).show();
        }
    }


    private int DistantFar(Pair<Float, Float> p1, Pair<Float, Float> p2) {
        float _x = cfv.pos1.first - cfv.pos2.first;
        float _y = cfv.pos1.second - cfv.pos2.second;
        double _1 = Math.sqrt(_x + _y);
        float x = p1.first - p2.first;
        float y = p2.second - p2.second;
        double _2 = Math.sqrt(x * x + y * y);

        return _1 < _2 ? 1 : 0;
    }


    private void startCropActivity(@NonNull Uri uri) {
        String destinationFileName = "0x10x20x30x4.png";    // 의미없음
        if (drawFlag) {
            UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
            uCrop = uCrop.useSourceImageAspectRatio();
            UCrop.Options options = new UCrop.Options();
            options.setCompressionFormat(Bitmap.CompressFormat.PNG);
            uCrop = uCrop.withOptions(options);

            uCrop.start(MainActivity.this);
        } else {
            UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));
            uCrop = uCrop.withAspectRatio(1, 1);

            uCrop.start(MainActivity.this);
        }
    }

    /**
     * Layout to Image
     */

    public Bitmap getLtoB() {   // LinearLayout to Bitmap
        Bitmap snapshot = Bitmap.createBitmap(cfv.getWidth(), cfv.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(snapshot);
        cfv.draw(canvas);
        return snapshot;
    }

    public Bitmap getBitmap(ContentResolver cr, Uri url)
            throws IOException {
        InputStream input = cr.openInputStream(url);
        Bitmap bitmap = BitmapFactory.decodeStream(input);
        input.close();
        return bitmap;
    }

    public Bitmap getBitmap(ContentResolver cr,
                            Uri url,
                            BitmapFactory.Options options)
            throws IOException {
        InputStream input = cr.openInputStream(url);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
        input.close();
        return bitmap;
    }

    public void setCurrentColor(String s) {
        if (s.length() != 7)
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        _current_color = s.substring(1);
        GradientDrawable bgShape = (GradientDrawable) binding.studioIvColor.getBackground();
        bgShape.setColor(Color.parseColor(s));
    }

    public void addPage() {
        cfv.addPage();
        adapter.notifyDataSetChanged();
    }


}


/**
 * 일 하 기 싫 다
 */