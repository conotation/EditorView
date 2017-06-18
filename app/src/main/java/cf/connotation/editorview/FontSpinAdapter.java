package cf.connotation.editorview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Conota on 2017-06-18.
 */

public class FontSpinAdapter extends BaseAdapter {
    Context context;
    List<String> data;
    LayoutInflater inflater;

    public FontSpinAdapter(Context context, List<String> data) {
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (data != null) return data.size();
        else return 0;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = inflater.inflate(R.layout.spinner_dropdown, parent, false);
        }

        //데이터세팅
        String text = data.get(position);
        ((TextView)convertView.findViewById(R.id.tv_dropdown)).setText(text);

        return convertView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView== null) {
            convertView = inflater.inflate(R.layout.spinner_text, parent, false);
        }
        if(data != null)  {
            String text = data.get(position);
            ((TextView) convertView.findViewById(R.id.tv)).setText(text);
        }
        return convertView;
    }
}
