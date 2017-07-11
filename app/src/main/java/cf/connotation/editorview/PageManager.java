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

    public void addPagePM(Page p) {
        arr.remove(arr.size() - 1); // 지우고
        Log.e(TAG, "arrSize1: " + arr.size() + " / p.getViewPage(): " + p.getViewPage());
        arr.add(p);
        arr.add(new FooterView());  // 넣고
        cf.count_page = arr.size() - 1;
    }

    public void modPagePM(Page p) {
        Log.e(TAG, "arrSize2: " + arr.size() + " / p.getViewPage(): " + p.getViewPage());
        arr.set(p.getViewPage() - 1, p);
    }

    public Page returnPage(int i) {
        return (Page) arr.get(i - 1);  // 1페이지부터 시작
    }

}
