package cf.connotation.editorview;

import java.io.File;

/**
 * Created by Conota on 2017-07-04.
 */

public class ResManager {
    private File img;        // img
    private String img_name;
    private String txt;          // txt
    private float x;
    private float y;
    private int width;          // img
    private int height;         // img
    private int size;           // txt
    private String font;        // txt
    private String color;       // txt
    private boolean _img = false;

    public ResManager(File img, float x, float y, int width, int height) {  // 이미지
        this.img = img;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        _img = true;
    }

    public ResManager(String img, float x, float y, int width, int height) { // 이미지
        this.img_name = img;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public ResManager(String txt, float x, float y, int size, String font, String color) {
        this.txt = txt;
        this.x = x;
        this.y = y;
        this.size = size;
        this.font = font;
        this.color = color;
    }

    public File getImg() {
        return img;
    }

    public String getImgName() {
        return img.getAbsolutePath();
    }

    public String getTxt() {
        return txt;
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

    public String getColor() {
        return color;
    }

    public String getImgData() {
        String result = " [ \"" + getImgName() + "\"" +
                ", " + getX() + ", " + getY() +
                ", " + getWidth() + ", " + getHeight() + " ] ";
        return result;
    }

    public String getTxtData() {
        String result = " [ \"" + getTxt() + "\"" +
                ", " + getX() + ", " + getY() +
                ", " + getSize() + ", " + getFont() + ", " + getColor() + " ] ";
        return result;
    }
}
