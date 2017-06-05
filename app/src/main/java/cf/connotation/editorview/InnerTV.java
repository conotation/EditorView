package cf.connotation.editorview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by Connotation on 2017-06-05.
 */

public class InnerTV extends AppCompatTextView {
    public String value = "for Test";

    public InnerTV(Context context) {
        super(context);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
    }

    public InnerTV(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InnerTV(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
