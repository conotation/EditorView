package cf.connotation.editorview;

import android.util.Log;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Conota on 2017-06-27.
 */

public class PageManager {
    public ArrayList<Page> arr;
    private CfView cf;

    public PageManager(CfView cf) {
        this.cf = cf;
        arr = new ArrayList<>();
    }

    public void addArr(Page p) {
        Log.e(TAG, "arrSize: " + arr.size() + " / p.getViewPage(): " + p.getViewPage());
        if (arr.size() < p.getViewPage())
            arr.add(p);
        else {
            arr.remove(p.getViewPage() - 1);
            arr.add(p.getViewPage() - 1, p);
        }
        cf.count_page = arr.size();
    }

    public Page returnPage(int i) {
        return arr.get(i - 1);  // 1페이지부터 시작
    }


}
