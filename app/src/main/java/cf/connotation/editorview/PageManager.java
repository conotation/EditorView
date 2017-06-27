package cf.connotation.editorview;

import java.util.ArrayList;

/**
 * Created by Conota on 2017-06-27.
 */

public class PageManager {
    ArrayList<Page> arr;
    CfView cf;

    public PageManager(CfView cf) {
        this.cf = cf;
        arr = new ArrayList<>();
    }

    public void addArr(Page p) {
        if (arr.size() < p.getPage())
            arr.add(p);
        else {
            arr.remove(p.getPage() - 1);
            arr.add(p.getPage() - 1, p);
        }
        cf.count_page = arr.size();
    }

    public Page returnPage(int i) {
        return arr.get(i - 1);  // 1페이지부터 시작
    }


}
