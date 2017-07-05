package cf.connotation.editorview;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by Connotation on 2017-06-03.
 */

public class CfView extends FrameLayout {
    final private String TAG = "CfView";
    private PageManager pag = new PageManager(this);
    private ArrayList<View> cardList = new ArrayList<>();     // Text
    private ArrayList<View> drawList = new ArrayList<>();     // Image
    private ArrayList<Bitmap> drawSubList = new ArrayList<>();     // Image Bitmap
    private int page = 1;
    public int count_page = 1;
    public static final int NONE = 0;
    public static final int MOVE = 1;
    public static final int ZOOM = 2;

    public Pair<Float, Float> pos1 = Pair.create(0f, 0f);
    public Pair<Float, Float> pos2 = Pair.create(0f, 0f);

    public int MODE = NONE;

    private Bitmap back_resource = null;
    public Bitmap currentShow = null;

    private boolean flag = false;
    private boolean lock = true;
    private Context cv;

    private View currentView = null;
    private View lastView = null;

    public CfView(@NonNull Context context) {
        super(context);
        cv = context;

    }

    public CfView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        cv = context;

    }

    public CfView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        cv = context;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:       // (1)
                MODE = NONE;
                pos1 = Pair.create(0f, 0f);
                pos2 = Pair.create(0f, 0f);
                setFlag(false);
                break;

            case MotionEvent.ACTION_POINTER_DOWN:   // ( 5 )  > 1
                MODE = ZOOM;
                break;

            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (currentView == null)
                    return true;
                if (event.getPointerCount() == 2) {
                    if (pos1 == Pair.create(0f, 0f)) {
                        pos1 = Pair.create(event.getX(0), event.getY(0));
                        pos2 = Pair.create(event.getX(1), event.getY(1));
                    } else {
                        Pair<Float, Float> cpos1;
                        Pair<Float, Float> cpos2;
                        cpos1 = Pair.create(event.getX(0), event.getY(0));
                        cpos2 = Pair.create(event.getX(1), event.getY(1));
                        if (lastView == null)
                            return true;
                        ViewGroup.LayoutParams params;
                        if (currentView == null) {
                            params = lastView.getLayoutParams();
                        } else {
                            params = currentView.getLayoutParams();
                        }
                        if (DistantFar(cpos1, cpos2)) {
                            params.width = (int) Math.ceil(params.width * 1.01);
                            params.height = (int) Math.ceil(params.height * 1.01);
                            if (currentView == null)
                                lastView.setLayoutParams(params);
                            else {
                                currentView.setLayoutParams(params);
                            }
                        } else {
                            params.width = (int) Math.floor(params.width * 0.99);
                            params.height = (int) Math.floor(params.height * 0.99);
                            if (currentView == null)
                                lastView.setLayoutParams(params);
                            else {
                                currentView.setLayoutParams(params);
                            }
                        }
                        if (currentView == null) {
                            lastView.invalidate();
                        } else {
                            currentView.invalidate();
                        }
                    }
                    pos1 = Pair.create(event.getX(0), event.getY(0));
                    pos2 = Pair.create(event.getX(1), event.getY(1));
                } else {
                    MODE = MOVE;
                    currentView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                    if (currentView instanceof LinearLayout) {
                        if (event.getX() - 5 < currentView.getX() || event.getX() + 5 > currentView.getX() + currentView.getMeasuredWidth() || event.getY() - 5 < currentView.getY() || event.getY() > currentView.getY() + currentView.getHeight() + 5) {
                            setFlag(false);
                            return true;
                        }
                        currentView.setX(event.getX() - currentView.getWidth() / 2);
                        currentView.setY(event.getY() - currentView.getHeight() / 2);
                    } else {
                        if (event.getX() < currentView.getX() || event.getX() > currentView.getX() + currentView.getMeasuredWidth() || event.getY() < currentView.getY() || event.getY() > currentView.getY() + currentView.getMeasuredHeight()) {
                            setFlag(false);
                            return true;
                        }
                        currentView.setX(event.getX() - currentView.getMeasuredWidth() / 2);
                        currentView.setY(event.getY() - currentView.getMeasuredHeight() / 2);
                    }
                }
                break;
        }

        currentShow = ((MainActivity) cv).getLtoB();
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return !lock && flag;
    }

    public void addCard(View child, ViewGroup.LayoutParams layoutParams) {
        addView(child, layoutParams);
        cardList.add(child);
    }

    public void addDrawCard(final View child, LinearLayout.LayoutParams layoutParams, final Bitmap b) {
        if (layoutParams != null) {
            addView(child, layoutParams);
        } else {
            addView(child);
        }
        drawList.add(child);
        drawSubList.add(b);
    }

    public void setFlag(boolean b) {
        flag = b;
        if (currentView != null)
            if (currentView instanceof TextView)
                currentView.setBackgroundColor(Color.argb(0, 0, 0, 0));
        lastView = currentView;
        currentView = null;

        MODE = NONE;
    }

    public void setFlag(boolean b, View tv) {
        flag = b;
        if (b) {
            currentView = tv;
            if (currentView instanceof TextView)
                currentView.setBackground(ContextCompat.getDrawable(cv, R.drawable.xml_tv_border));
        } else {
            currentView = null;
        }
    }

    public boolean getFlag() {
        return flag;
    }

    public void setLocked() {
        lock = !lock;
        setFlag(false);
    }

    public boolean getLocked() {
        return lock;
    }

    public void removeCard() {
        if (currentView == null) {  // 현재 선택된 뷰가 없음
            Toast.makeText(cv, "카드를 선택해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        for (int i = 0; i < cardList.size(); i++) {
            if (currentView.getId() == ((View) cardList.get(i)).getId()) {
                removeView(currentView);
                setFlag(false);
                cardList.remove(i);
                return;
            }
        }

        for (int i = 0; i < drawList.size(); i++) {
            if (currentView.getId() == ((View) drawList.get(i)).getId()) {
                removeView(currentView);
                setFlag(false);
                drawList.remove(i);
                drawSubList.remove(i);
                return;
            }
        }
        Toast.makeText(cv, "에러코드[404]", Toast.LENGTH_SHORT).show();
    }

    public void setCardBackground(Bitmap b) {
        ImageView iv = findViewById(R.id.tfv);
        back_resource = b;
        iv.setImageBitmap(b);
    }

    public Page addPage() {
        // TODO 페이지 추가
        Page p = new Page(cardList, drawList, drawSubList, back_resource, page);
        page = page + 1;
        addPage(p);
        return p;
    }

    public void addPage(Page p) {
        // __addPage__
        pag.addArr(p);
    }

    public int getCVInstance() {
        if (currentView == null) {
            return -1;
        } else if (currentView instanceof LinearLayout) {
            return 0;
        } else if (currentView instanceof TextView) {
            return 1;
        }
        return -1;
    }

    public void changeColor(String tmp) {
        final hexColor h = new hexColor();
        h.setColor(tmp);
        ColorPickerDialogBuilder
                .with(cv)
                .setTitle("색 선택")
                .initialColor(Color.parseColor(h.getColor()))
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .lightnessSliderOnly()
                .density(12)
                .setPositiveButton("완료", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        h.setColor(Integer.toHexString(selectedColor));
                        Log.e(TAG, "onClick: " + Integer.toHexString(selectedColor) + " / / / " + h.getColor());
                        ((MainActivity) cv).setCurrentColor(h.getColor());
                        ((InText) currentView).setTextColor(Color.parseColor(h.getColor()), h.getColor());

                        dialog.dismiss();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .build()
                .show();
    }

    public void movePage(int x) {
        // TODO 페이지 이동 구현
        setFlag(false);
        addPage(new Page(cardList, drawList, drawSubList, back_resource, page + 1));
        Page p = pag.returnPage(x);
        cardList = p.getCard();
        drawList = p.getDraw();
        drawSubList = p.getBitmap();
        back_resource = p.getBack();
        page = p.getViewPage();
    }

    public void createIndiFormat() {
        PageExt ext = new PageExt();
        ext.setMainImg(bTof(currentShow, "show"));
        ext.setPage(page);
        ext.setResCount(Pair.create(cardList.size(), drawList.size()));
        ext.setResBack(bTof(back_resource, "back"));

        for (int i = 0; i < drawList.size(); i++) {
            View v = drawList.get(i);
            ResManager resManager = new ResManager(bTof(drawSubList.get(i), "f" + i), v.getX(), v.getY(), v.getWidth(), v.getHeight());
            ext.addResImg(resManager);
        }

        for (int i = 0; i < cardList.size(); i++) {
            InText v = (InText) cardList.get(i);
            ResManager resManager = new ResManager(v.getText().toString(), v.getX(), v.getY(), Math.round(v.get_size()), v.get_font(), v.get_color());
            ext.addResTxt(resManager);
        }

        ext.log();
    }

    private boolean DistantFar(Pair<Float, Float> p1, Pair<Float, Float> p2) {
        float _x = pos1.first - pos2.first;
        float _y = pos1.second - pos2.second;
        double _1 = Math.sqrt(_x * _x + _y * _y);       // 과거점
        float x = p1.first - p2.first;
        float y = p1.second - p2.second;
        double _2 = Math.sqrt(x * x + y * y);       // 현재점

        return _1 < _2;
    }

    private class hexColor {
        private String s;

        hexColor() {

        }

        public void setColor(int i1, int i2, int i3) {
            s = Integer.toHexString(i1)
                    + Integer.toHexString(i2)
                    + Integer.toHexString(i3);
        }

        public void setColor(String s) {
            if (s.length() == 8) {
                this.s = s.substring(2);
            } else if (s.length() == 7) {
                this.s = s.substring(1);
            } else {
                this.s = s;
            }
        }

        public String getColor() {
            return "#" + s;
        }

    }

    public File bTof(Bitmap b, String s) {
        File f = new File(cv.getExternalCacheDir() + "/" + s);
        OutputStream out = null;
        try {
            f.createNewFile();
            out = new FileOutputStream(f);
            b.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f;
    }

    public PageManager getPag() {
        return pag;
    }
}
