package cf.connotation.editorview;

import android.util.Log;
import android.util.Pair;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Conota on 2017-07-04.
 */

class PageExt {
    private File main_img;
    private int page;
    private Pair<Integer, Integer> res_count;
    private File res_back;
    private ArrayList<ResManager> res_img = new ArrayList<>();
    private ArrayList<ResManager> res_txt = new ArrayList<>();

    PageExt() {

    }

    void setPage(int page) {
        this.page = page;
    }

    void setResCount(Pair<Integer, Integer> res_count) {
        this.res_count = res_count;
    }

    void setMainImg(File img) {
        this.main_img = img;
    }

    void setResBack(File img) {
        this.res_back = img;
    }

    void addResImg(ResManager res) {
        res_img.add(res);
    }

    void addResTxt(ResManager res) {
        res_txt.add(res);
    }

    File getMainImg() {
        return main_img;
    }

    String getMainImgName() {
        return main_img.getAbsolutePath();
    }

    File getResBack() {
        return res_back;
    }

    String getResBackName() {
        return res_back.getAbsolutePath();
    }


    void log() {
        String result = "{" +
                "\"main_img\": " + getMainImgName() +
                ", \"page\": " + page +
                ", \"res_count\": [" + res_count.first + ", " + res_count.second +
                "], \"res_back\": " + getResBackName() +
                ", \"res_img\": [";

        for (ResManager r : res_img) {
            result = result + r.getImgData() + ", ";
        }

        result = result + "], \"res_txt\": [";

        for (ResManager r : res_txt) {
            result = result + r.getTxtData() + ", ";
        }

        result = result + "] }";

        Log.e("PageExt", result );
    }


}
