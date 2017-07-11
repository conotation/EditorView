package cf.connotation.editorview;

import android.databinding.ObservableBoolean;
import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Conota on 2017-06-27.
 */

public class Page {
    private ArrayList card;
    private ArrayList draw;
    private ArrayList bitmap;
    private Bitmap back;
    private int viewPage;   // NOT Page Class

    private ObservableBoolean seleceted = new ObservableBoolean();

    public Page(ArrayList card, ArrayList draw, ArrayList bitmap, Bitmap b, int viewPage) {
        this.card = card;
        this.draw = draw;
        this.bitmap = bitmap;
        this.back = b;
        if(viewPage == 1) seleceted.set(true);
        this.viewPage = viewPage;
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
}
