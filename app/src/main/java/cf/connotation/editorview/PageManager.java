package cf.connotation.editorview;

import android.databinding.ObservableArrayList;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by Conota on 2017-06-27.
 */

public class PageManager {
    public ObservableArrayList<Object> arr;
    private CfView cf;

    public PageManager(CfView cf) {
        this.cf = cf;
        arr = new ObservableArrayList<>();

        arr.add(new FooterView());
    }

    public void addArr(Page p) {
        Log.e(TAG, "arrSize: " + arr.size() + " / p.getViewPage(): " + p.getViewPage());
        arr.remove(arr.size()-1);
        if (arr.size() < p.getViewPage())
            arr.add(p);
        else {
            arr.remove(p.getViewPage() - 1);
            arr.add(p.getViewPage() - 1, p);
        }
        arr.add(new FooterView());
        cf.count_page = arr.size()-1;
    }

    public Page returnPage(int i) {
        return (Page) arr.get(i - 1);  // 1페이지부터 시작
    }


}
