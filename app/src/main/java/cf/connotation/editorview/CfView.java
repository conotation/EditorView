package cf.connotation.editorview;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutCompat;
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
import com.github.nitrico.fontbinder.FontBinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by Connotation on 2017-06-03.
 * - 에디터 본체
 */

public class CfView extends FrameLayout {
    final private String TAG = "CfView";
    private PageManager pag = new PageManager(this);
    private ArrayList<View> cardList = new ArrayList<>();     // Text
    private ArrayList<View> drawList = new ArrayList<>();     // Image
    private ArrayList<Bitmap> drawSubList = new ArrayList<>();     // Image Bitmap
    private int page = 1;
    private PageExt pageExt = new PageExt();
    public int count_page = 0;
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
                        if (event.getX() - 5 < currentView.getX() || event.getX() + 5 > currentView.getX() + currentView.getWidth() || event.getY() - 5 < currentView.getY() || event.getY() + 5 > currentView.getY() + currentView.getHeight()) {
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

                        ((InText) currentView).setNx(event.getX() - currentView.getMeasuredWidth() / 2);
                        ((InText) currentView).setNy(event.getY() - currentView.getMeasuredHeight() / 2);

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
            if (currentView.getId() == (cardList.get(i)).getId()) {
                removeView(currentView);
                setFlag(false);
                cardList.remove(i);
                return;
            }
        }

        for (int i = 0; i < drawList.size(); i++) {
            if (currentView.getId() == (drawList.get(i)).getId()) {
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

    public void addPage() {
        // TODO 페이지 추가
        Page p = new Page(cardList, drawList, drawSubList, back_resource, count_page + 1);
        pag.addPagePM(p);
    }

    public int getPage() {
        return page;
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
        final hexColor h = new hexColor(tmp);
        ColorPickerDialogBuilder        // 컬러피커 라이브러리 * 맘에 안듬
                .with(cv)
                .setTitle("색 선택")
                .initialColor(h.getargbColor())
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

    public void movePage(int x) {       // position
        // TODO 페이지 이동 구현
        // TODO v2 재구축 필요
        setFlag(false);
        ((MainActivity) cv).changeForm();

        pag.modPagePM(new Page(cardList, drawList, drawSubList, back_resource, page, createIndiFormat()));
        Log.e(TAG, "movePage: " + getChildCount());
        while (getChildCount() != 1) {
            removeViewAt(1);
        }
        if (back_resource != null) {
            Bitmap bitTemp = back_resource.copy(Bitmap.Config.ARGB_8888, true);
            bitTemp.eraseColor(Color.WHITE);
            setCardBackground(bitTemp);
        }
        Page p = pag.returnPage(x);
        cardList = p.getCard();
        drawList = p.getDraw();
        drawSubList = p.getBitmap();
        back_resource = p.getBack();
        page = p.getViewPage();
        pageExt = p.getPageExt();
        if (page == 1)
            Toast.makeText(cv, "" + (restorePage() ? "완료" : "실패"), Toast.LENGTH_SHORT).show();
    }

    public PageExt createIndiFormat() {
        PageExt ext = new PageExt();
        File folder = new File(cv.getExternalCacheDir() + "/" + page);
        if (!folder.exists()) {
            folder.mkdir();
        }
        ext.setMainImg(bTof(currentShow, "show", page));
        ext.setPage(page);
        ext.setResCount(Pair.create(cardList.size(), drawList.size()));
        if (back_resource != null) {
            ext.setResBack(bTof(back_resource, "back", page));
        } else {
            ext.setResBack(null);
        }
        for (int i = 0; i < drawList.size(); i++) {
            View v = drawList.get(i);
            ResManager resManager = new ResManager(bTof(drawSubList.get(i), "f" + i, page), v.getX(), v.getY(), v.getWidth(), v.getHeight());
            ext.addResImg(resManager);
        }

        for (int i = 0; i < cardList.size(); i++) {
            InText v = (InText) cardList.get(i);
            ResManager resManager = new ResManager(v.getText().toString(), v.getNx(), v.getNy(), Math.round(v.get_size()), v.get_font(), v.get_color());
            ext.addResTxt(resManager);
        }

        try {
            JSONObject data = new JSONObject();
            data.put("main_img", ext.getMainImgName());
            data.put("page", ext.getPage());
            data.put("res_count", ext.getResCount());
            data.put("res_back", ext.getResBackName() != null ? ext.getResBackName() : JSONObject.NULL);

            JSONArray resImg = new JSONArray();
            for (int i = 0; i < ext.getImageSize(); i++) {
                resImg.put(ext.getImageData(i));
            }
            data.put("res_img", resImg);

            JSONArray resTxt = new JSONArray();
            for (int i = 0; i < ext.getTextSize(); i++) {
                resTxt.put(ext.getTextData(i));
            }
            data.put("res_txt", resTxt);

            Log.e(TAG, "createIndiFormat: " + data.toString());
            try (PrintWriter out = new PrintWriter(cv.getExternalCacheDir() + "/" + page + "/data.json", "UTF-8")) {
                out.write(data.toString());
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return ext;
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
        private long A = 0xff;
        private long R = 0xff;
        private long G = 0xff;
        private long B = 0xff;

        public hexColor(String s) {
            setColor(s);
        }

        public void setColor(int i1, int i2, int i3) {
            s = Integer.toHexString(i1)
                    + Integer.toHexString(i2)
                    + Integer.toHexString(i3);
        }

        public void setColor(String s) {
            if (s.length() == 8) {
                this.s = s.substring(2);
                A = Long.parseLong(s.substring(0, 2), 16);
                R = Long.parseLong(s.substring(2, 4), 16);
                G = Long.parseLong(s.substring(4, 6), 16);
                B = Long.parseLong(s.substring(6, 8), 16);
            } else if (s.length() == 7) {
                this.s = s.substring(1);
                A = 255;
                R = Long.parseLong(s.substring(1, 3), 16);
                G = Long.parseLong(s.substring(3, 5), 16);
                B = Long.parseLong(s.substring(5, 7), 16);
            } else {
                this.s = s;
                A = 0xff;
                R = Long.parseLong(s.substring(0, 2), 16);
                G = Long.parseLong(s.substring(2, 4), 16);
                B = Long.parseLong(s.substring(4, 6), 16);
            }
        }

        public String getColor() {
            return "#" + s;
        }

        public int getargbColor() {
            return Color.argb((int) A, (int) R, (int) G, (int) B);
        }

    }

    public File bTof(Bitmap b, String s, int i) {   // Bitmap to File
        File f = new File(cv.getExternalCacheDir() + "/" + i + "/" + s);
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

    public Bitmap fTob(String s) {
        return BitmapFactory.decodeFile(cv.getExternalCacheDir() + s);
    }

    public PageManager getPag() {
        return pag;
    }

    public boolean restorePage() {
        // TODO 페이지 복원 구현 필요

        PageExt p = new PageExt();

        try {
            File f = new File(cv.getExternalCacheDir() + "/" + page + "/data.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            String json = br.readLine();

            JSONObject data = new JSONObject(json);

            File rmain = new File((String) data.get("main_img"));
            p.setMainImg(rmain);
            int rpage = (int) data.get("page");
            p.setPage(rpage);
            JSONArray rcount = data.getJSONArray("res_count");
            p.setResCount(Pair.create(rcount.getInt(0), rcount.getInt(1)));

            File rback = null;
            if (!data.isNull("res_back")) {
                rback = new File((String) data.get("res_back"));
            }
            p.setResBack(rback);

            ArrayList<ResManager> img_pack = new ArrayList<>();
            if (!data.isNull("res_img")) {
                JSONArray imgData = data.getJSONArray("res_img");
                for (int i = 0; i < imgData.length(); i++) {
                    JSONArray img = imgData.getJSONArray(i);
                    ResManager res = new ResManager(new File(img.getString(0)), (float) img.getDouble(1), (float) img.getDouble(2), img.getInt(3), img.getInt(4));
                    Log.e(TAG, "restorePage: X" + img.getDouble(1));
                    Log.e(TAG, "restorePage: Y" + img.getDouble(2));
                    img_pack.add(res);
                }
            }
            p.setResImg(img_pack);


            ArrayList<ResManager> txt_pack = new ArrayList<>();
            if (!data.isNull("res_txt")) {
                JSONArray txtData = data.getJSONArray("res_txt");
                for (int i = 0; i < txtData.length(); i++) {
                    JSONArray txt = txtData.getJSONArray(i);
                    ResManager res = new ResManager(txt.getString(0), (float) txt.getDouble(1), (float) txt.getDouble(2), txt.getInt(3), txt.getString(4), txt.getString(5));
                    txt_pack.add(res);
                }
            }
            p.setResTxt(txt_pack);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }

        cardList.clear();   // Re: Zero
        drawList.clear();
        drawSubList.clear();

        page = p.getPage();
//        page = 1;
        if (p.getResBack() != null) {
            Log.e(TAG, "restorePage: Back Bitmap Not Null");
            back_resource = fTob("/" + page + "/" + p.getResBackName());
            setCardBackground(back_resource);
        }

        ArrayList<ResManager> drawTemp = p.getResImg();
        for (int i = 0; i < drawTemp.size(); i++) {
            ResManager res = drawTemp.get(i);
            final LinearLayout ll = new LinearLayout(cv);
            Bitmap b = fTob("/" + page + "/" + res.getImgName());
            BitmapDrawable bm = new BitmapDrawable(getResources(), b);
            ll.setBackground(bm);
            ll.setX(res.getX());
            ll.setY(res.getY());
            ll.setLayoutParams(new LinearLayoutCompat.LayoutParams(res.getWidth(), res.getHeight()));
            ll.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (getLocked())
                        return false;
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            MODE = CfView.NONE;
                            setFlag(false);
                            break;
                        case MotionEvent.ACTION_POINTER_DOWN:
                            setFlag(true, ll);
                            MODE = CfView.ZOOM;
                            onTouchEvent(event);
                            break;
                        case MotionEvent.ACTION_DOWN:
                        case MotionEvent.ACTION_MOVE:
                            if (event.getPointerCount() > 1) {
                                MODE = CfView.ZOOM;
                            }
                            MODE = CfView.MOVE;
                            setFlag(true, ll);
                            break;
                    }
                    return true;
                }
            });
            addDrawCard(ll, null, fTob("/" + page + "/" + res.getImgName()));
        }

        ArrayList<ResManager> cardTemp = p.getResTxt();
        for (int i = 0; i < cardTemp.size(); i++) {
            ResManager res = cardTemp.get(i);
            InText tv = new InText(cv);
            tv.setX(res.getX());
            tv.setY(res.getY());
            tv.setNx(res.getX());
            tv.setNy(res.getY());
            tv.setText(res.getTxt());
            tv.setTextSize(res.getSize());
            tv.setTypeface(FontBinder.get(res.getFont()), "NanumGothic");
            tv.setTextColor(Color.parseColor(res.getColor()), res.getColor());
            tv = ((MainActivity) cv).setListener(tv);
            addCard(tv, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }


        return true;
    }


}
