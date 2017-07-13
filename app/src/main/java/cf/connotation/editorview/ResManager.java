package cf.connotation.editorview;

import java.io.File;

/**
 * Created by Conota on 2017-07-04.
 * - 카드 및 리소스 정보 관리자
 */

public class ResManager {
    private File img;        // 최종 이미지
    private String img_name;
    private String txt;          // 최종 텍스트
    private float x;
    private float y;
    private int width;          // img
    private int height;         // img
    private int size;           // txt
    private String font;        // txt
    private String color;       // txt
    private boolean _img = false;       // 이미지 체크 여부

    public ResManager(File img, float x, float y, int width, int height) {  // 이미지
        this.img = img;
        this.img_name = img.getName();
        this.x = (float) Math.floor(x);
        this.y = (float) Math.floor(y);
        this.width = width;
        this.height = height;
        this._img = true;
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
        return img_name;
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

    public boolean isImg() {
        return _img;
    }

}
