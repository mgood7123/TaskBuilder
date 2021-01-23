package smallville7123.DraggableSwipableExpandableRecyclerView.Contents;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerListAdapter extends RecyclerView.Adapter<ViewHolder> {

    public final ArrayList<View> mItems = new ArrayList<>();

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(new FrameLayout(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItem(mItems.get(position));
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        holder.root.removeAllViews();
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
