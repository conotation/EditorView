package cf.connotation.editorview;

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
    private int page;   // NOT Page

    Page(ArrayList card, ArrayList draw, ArrayList bitmap, Bitmap b, int page) {
        this.card = card;
        this.draw = draw;
        this.bitmap = bitmap;
        this.back = b;
        this.page = page;
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

    public int getPage(){
        return page;
    }
}
