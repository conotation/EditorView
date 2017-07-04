package cf.connotation.editorview;

import java.io.Serializable;

/**
 * Created by Conota on 2017-07-04.
 */

public class ShowPage implements Serializable {
    int page;
    boolean selected;

    public ShowPage(){

    }

    public ShowPage(int page) {
        this.page = page;
    }
}
