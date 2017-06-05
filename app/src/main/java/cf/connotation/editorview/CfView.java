package cf.connotation.editorview;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;

/**
 * Created by Connotation on 2017-06-03.
 */

public class CfView extends FrameLayout {
    final private String TAG = "CfView";
    private ArrayList viewList = new ArrayList();
    private boolean flag = false;
    private boolean lock = true;
    private Context cv;

    private View presView = null;

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
                if (presView != null)
                    Log.e(TAG, "onTouchEvent: moveStart");
                break;

            case 1:
                this.setFlag(false);
                break;

            case 2:
                if (presView != null) {
                    presView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                    presView.setX(event.getX() - presView.getMeasuredWidth() / 2);
                    presView.setY(event.getY() - presView.getMeasuredHeight() / 2);
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (lock)
            return false;
        return flag;
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        viewList.add(child);
        Log.e(TAG, "viewList: " + viewList.size());
    }

    public void setFlag(boolean b) {
        flag = b;
        if (presView != null)
            presView.setBackgroundColor(Color.argb(0, 0, 0, 0));
        presView = null;
    }

    public void setFlag(boolean b, View tv) {
        flag = b;
        if (b) {
            presView = tv;
            presView.setBackground(ContextCompat.getDrawable(cv, R.drawable.xml_tv_border));
        } else {
            presView = null;
        }
    }

    public boolean getFlag() {
        return flag;
    }

    public void setLocked() {
        lock = !lock;
    }

    public boolean getLocked() {
        return lock;
    }

}
