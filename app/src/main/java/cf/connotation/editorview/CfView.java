package cf.connotation.editorview;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by Connotation on 2017-06-03.
 */

public class CfView extends FrameLayout {
    final private String TAG = "CfView";

    public CfView(@NonNull Context context) {
        super(context);
    }

    public CfView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CfView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e(TAG, "onTouchEvent: " + event.getAction());
        return true;
    }
}
