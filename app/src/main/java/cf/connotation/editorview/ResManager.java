package cf.connotation.editorview;

import java.io.File;

/**
 * Created by Conota on 2017-07-04.
 */

public class ResManager {
    private File fname;        // img
    private String txt;          // txt
    private float x;
    private float y;
    private int width;          // img
    private int height;         // img
    private int size;           // txt
    private String font;        // txt
    private String color;       // txt
    private boolean _img = false;

    public ResManager(File fname, float x, float y, int width, int height) {
        this.fname = fname;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        _img = true;
    }

    public ResManager(String txt, float x, float y, int size, String font, String color) {
        this.txt = txt;
        this.x = x;
        this.y = y;
        this.size = size;
        this.font = font;
        this.color = color;
    }

    public File getFname() {
        return fname;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getSize() {
        return size;
    }

    public String getFont() {
        return font;
    }
}
