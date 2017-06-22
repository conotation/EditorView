package cf.connotation.editorview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Conota on 2017-06-22.
 */

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.ViewHolder> {
    private ArrayList<fragData> mDataset;
    private String TAG = "PageAdapter";

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView mImageView;
        public ImageView cv1;
        public ImageView cv2;

        public ViewHolder(View view) {
            super(view);
            mImageView = view.findViewById(R.id.iv);
            cv1 = view.findViewById(R.id.cover_1);
            cv2 = view.findViewById(R.id.cover_2);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PageAdapter(ArrayList<fragData> fragDataset) {
        mDataset = fragDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.studio_frag_view, parent, false);
        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        v.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element


//        holder.mTextView.setText(mDataset.get(position).text);
//        holder.mImageView.setImageResource(mDataset.get(position).img);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

class fragData {
    private boolean b;

    public fragData(boolean b) {
        this.b = b;
    }
}


