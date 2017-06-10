package cf.connotation.editorview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Connotation on 2017-06-03.
 */

public class CfView extends FrameLayout {
    final private String TAG = "CfView";
    private ArrayList cardList = new ArrayList<>();

    private boolean flag = false;
    private boolean lock = true;
    private Context cv;

    private View currentView = null;

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
    public boolean onTouchEvent(MotionEvent event) {
//        Log.e(TAG, "onTouchEvent: " + event.getAction());
        switch (event.getAction()) {
            case 0:
                setFlag(false);
                break;

//            case 1:       // 추후 좌표값으로 수정해야될듯
//                setFlag(false);
//                break;

            case 2:
                if (currentView == null)
                    return false;
                currentView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                currentView.setX(event.getX() - currentView.getMeasuredWidth() / 2);
                currentView.setY(event.getY() - currentView.getMeasuredHeight() / 2);
                break;
        }
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

    public void setFlag(boolean b) {
        flag = b;
        if (currentView != null)
            currentView.setBackgroundColor(Color.argb(0, 0, 0, 0));
        currentView = null;
    }

    public void setFlag(boolean b, View tv) {
        flag = b;
        if (b) {
            currentView = tv;
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
        Toast.makeText(cv, "참조 실패", Toast.LENGTH_SHORT).show();
    }

    public void setCardBackground() {
        // TODO: 추가바람 ㅎ
        ImageView iv = (ImageView) findViewById(R.id.tfv);
        try {
            String downloadsDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Cardline/";
            String filename = cv.getString(R.string.background_resource);

            File f = new File(downloadsDirectoryPath + filename);
            if (f.exists()) {
                Bitmap b = BitmapFactory.decodeFile(f.getAbsolutePath());
                iv.setImageBitmap(b);
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
            }
        } catch (Exception e) {
            Toast.makeText(cv, "이미지 설정 실패", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}
