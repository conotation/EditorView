package cf.connotation.editorview;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Conota on 2017-06-27.
 */

class Page {
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

    ArrayList getBitmap() {
        return bitmap;
    }

    ArrayList getCard() {
        return card;
    }

    ArrayList getDraw() {
        return draw;
    }

    Bitmap getBack() {
        return back;
    }

    int getPage() {
        return page;
    }
}
