package cf.connotation.editorview;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by Connotation on 2017-06-05.
 *
 * 텍스트가 포함되있는 카드 레이아웃
 */

public class InText extends android.support.v7.widget.AppCompatTextView {
    private String _font;
    private String _color;
    private float _size;

    public InText(Context context) {
        super(context);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//                LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
    }

    public void setTypeface(Typeface tf, String s) {
        _font = s;
        super.setTypeface(tf);
    }

    public void setTextColor(int color, String s) {
        _color = s;
        super.setTextColor(color);
    }

    public String get_color() {
        return _color;
    }

    public String get_font() {
        return _font;
    }

    public float get_size() {
        return _size;
    }

    @Override
    public void setTextSize(float size) {
        _size = size;
        super.setTextSize(size);
    }

    public InText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
