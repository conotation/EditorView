package cf.connotation.editorview;

import android.databinding.ObservableArrayList;

/**
 * Created by Conota on 2017-06-27.
 * - 페이지 정보 통합 관리자
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
        if(p.getViewPage() > cf.count_page + 1){
            arr.add(new FooterView());
            return;
        }
        arr.add(p);
        arr.add(new FooterView());  // 넣고
        cf.count_page = arr.size() - 1;
    }

    public void modPagePM(Page p) {
        arr.remove(arr.size() - 1);
        arr.set(p.getViewPage() - 1, p);
        arr.add(new FooterView());  // 넣고
    }

    public Page returnPage(int i) {
        return (Page) arr.get(i - 1);  // 1페이지부터 시작
    }

}
