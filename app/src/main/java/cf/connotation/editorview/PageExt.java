package cf.connotation.editorview;

import android.util.Log;
import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Conota on 2017-07-04.
 */

public class PageExt {
    private File main_img;
    private String main_img_name;
    private int page;
    private Pair<Integer, Integer> res_count;
    private File res_back;
    private String res_back_name;
    private ArrayList<ResManager> res_img = new ArrayList<>();
    private ArrayList<ResManager> res_txt = new ArrayList<>();

    public PageExt() {

    }

    public PageExt(String s) {  // get JSON
        try {
            JSONObject data = new JSONObject(s);
            this.main_img_name = data.getString("main_img");
            this.page = data.getInt("page");
            JSONArray res_count = data.getJSONArray("res_count");
            this.res_count = Pair.create(res_count.getInt(0), res_count.getInt(1));
            this.res_back_name = data.getString("res_back");
            JSONArray res_img = data.getJSONArray("res_img");
            if (res_img != null) {
                for (int i = 0; i < res_img.length(); i++) {
                    JSONArray json_img = new JSONArray(new JSONObject(res_img.getString(i)));

                    ResManager res = new ResManager(
                            json_img.get(0).toString(),
                            Float.parseFloat(json_img.get(1).toString()),
                            Float.parseFloat(json_img.get(2).toString()),
                            Integer.parseInt(json_img.get(3).toString()),
                            Integer.parseInt(json_img.get(4).toString())
                    );
                    this.res_img.add(res);
                }
            }
            JSONArray res_txt = data.getJSONArray("res_txt");
            if (res_txt != null) {
                for (int i = 0; i < res_txt.length(); i++) {
                    JSONArray json_img = new JSONArray(new JSONObject(res_txt.getString(i)));

                    ResManager res = new ResManager(
                            json_img.get(0).toString(),
                            Float.parseFloat(json_img.get(1).toString()),
                            Float.parseFloat(json_img.get(2).toString()),
                            Integer.parseInt(json_img.get(3).toString()),
                            json_img.get(4).toString(),
                            json_img.get(5).toString()
                    );
                    this.res_img.add(res);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param page {
     *             "main_img": "link1~~~~~~~",	// 이미지와 텍스트로 구성된 카드 뉴스 NULL 불가
     *             "page": 1,
     *             "res_count": [2, 0], // images / text	0 -> null
     *             "res_back": "rsss.png",	// Background Resource
     *             "res_img": [
     *             ["img1", "x", "y", "width", "height"],
     *             ["img2", "x", "y", "width", "height"],
     *             ],
     *             "res_txt": [ ]
     *             },
     */

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


    public File getMainImg() {
        return main_img;
    }

    public String getMainImgName() {
        return main_img.getAbsolutePath();
    }

    public File getResBack() {
        return res_back;
    }

    public String getResBackName() {
        return res_back.getAbsolutePath();
    }

    public ResManager getResImg(int i) {
        return res_img.get(i);
    }

    public ResManager getResTxt(int i) {
        return res_txt.get(i);
    }

    public void log() throws JSONException {
        /*String result = "{" +
                "\"main_img\": " + getMainImgName() +
                ", \"page\": " + page +
                ", \"res_count\": [" + res_count.first + ", " + res_count.second +
                "], \"res_back\": " + getResBackName() +
                ", \"res_img\": [";*/

        JSONObject d = new JSONObject();
        d.put("main_img", getMainImgName());
        d.put("page", page);
//        d.put("res_count", )
        JSONArray i1 = new JSONArray();
        i1.put(res_count.first);
        i1.put(res_count.second);
        d.put("res_count", i1);
        d.put("res_back", getResBackName());

        JSONArray eimg = new JSONArray();

        for (ResManager r : res_img) {
            JSONArray ja = new JSONArray();
            ja.put(r.getImgName());
            ja.put(r.getX());
            ja.put(r.getY());
            ja.put(r.getWidth());
            ja.put(r.getHeight());
            eimg.put(ja);
//            result = result + r.getImgData() + ", ";
        }
        d.put("res_img", eimg);

        JSONArray etxt = new JSONArray();

//        result = result + "], \"res_txt\": [";

        for (ResManager r : res_txt) {
            JSONArray ja = new JSONArray();
            ja.put(r.getTxt());
            ja.put(r.getX());
            ja.put(r.getY());
            ja.put(r.getSize());
            ja.put(r.getFont());
            ja.put(r.getColor());
//            result = result + r.getTxtData() + ", ";
        }
        d.put("res_txt", etxt);


//        result = result + "] }";

        Log.e("PageExt", d.toString());
    }


}
