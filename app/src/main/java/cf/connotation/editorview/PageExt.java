package cf.connotation.editorview;

import android.util.Pair;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Conota on 2017-07-04.
 */

public class PageExt {
    private File main_img;
    private int page;
    private Pair<Integer, Integer> res_count;
    private File res_back;
    private ArrayList<ResManager> res_img = new ArrayList<>();
    private ArrayList<ResManager> res_txt = new ArrayList<>();

    public PageExt() {

    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setResCount(Pair<Integer, Integer> res_count) {
        this.res_count = res_count;
    }

    public void setMainImg(File img) {
        this.main_img = img;
    }

    public void setResBack(File img) {
        this.res_back = img;
    }

    public void addResImg(ResManager res) {
        res_img.add(res);
    }

    public void addResTxt(ResManager res) {
        res_txt.add(res);
    }


}
