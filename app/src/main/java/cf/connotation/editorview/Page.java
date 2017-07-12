package cf.connotation.editorview;

import android.databinding.ObservableBoolean;
import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Conota on 2017-06-27.
 * - Page 객체
 */

public class Page {
    private ArrayList card;
    private ArrayList draw;
    private ArrayList bitmap;
    private Bitmap back;
    private int viewPage;   // NOT Page Class
    private ObservableBoolean seleceted = new ObservableBoolean();
    private PageExt pageExt = new PageExt();

    public Page(ArrayList card, ArrayList draw, ArrayList bitmap, Bitmap b, int viewPage) {
        this.card = card;
        this.draw = draw;
        this.bitmap = bitmap;
        this.back = b;
        if (viewPage == 1) seleceted.set(true);
        this.viewPage = viewPage;
    }

    public Page(ArrayList card, ArrayList draw, ArrayList bitmap, Bitmap b, int viewPage, PageExt pageExt) {
        Log.e(TAG, "Page: " + viewPage + " modify" );
        this.card = card;
        this.draw = draw;
        this.bitmap = bitmap;
        this.back = b;
        this.viewPage = viewPage;
        this.pageExt = pageExt;
    }

    public ArrayList getBitmap() {
        return bitmap;
    }

    public ArrayList getCard() {
        return card;
    }

    public ArrayList getDraw() {
        return draw;
    }

    public Bitmap getBack() {
        return back;
    }

    public int getViewPage() {
        return viewPage;
    }

    public void setSeleceted(boolean seleceted) {
        this.seleceted.set(seleceted);
    }

    public boolean isSeleceted() {
        return this.seleceted.get();
    }

    public PageExt getPageExt() {
        return pageExt;
    }
}
