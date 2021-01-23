package smallville7123.DraggableSwipableExpandableRecyclerView.Contents;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;

public class SimpleShadowItemTouchHelperCallback extends ShadowItemTouchHelper.Callback {

    private final RecyclerListAdapter mAdapter;

    final int scrollSpeed = 20;

    public SimpleShadowItemTouchHelperCallback(RecyclerListAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public View createSelector(Context context) {
        TextView selector = new TextView(context);
        selector.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);
        selector.setText("> Move Item Here");
        selector.setTextColor(Color.GREEN);
        selector.setLayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );
        return selector;
    }

    @Override
    public void attachSelector(View selector) {
        mAdapter.mItems.add(selector);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public int getSelectorPosition(View selector) {
        return mAdapter.mItems.indexOf(selector);
    }

    @Override
    public void moveItem(int fromPosition, int toPosition) {
        moveItemInList(mAdapter.mItems, fromPosition, toPosition);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void moveItemToEnd(int fromPosition) {
        moveItemToEndOfList(mAdapter.mItems, fromPosition);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void detachSelector(View selector) {
        mAdapter.mItems.remove(selector);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public int interpolateOutOfBoundsScroll(RecyclerView recyclerView, int viewSize, int viewSizeOutOfBounds, int totalSize, long msSinceStartScroll) {
        final int direction = (int) Math.signum(viewSizeOutOfBounds);
        return scrollSpeed * direction;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        final int dragFlags = ShadowItemTouchHelper.START | ShadowItemTouchHelper.END | ShadowItemTouchHelper.UP | ShadowItemTouchHelper.DOWN;
        final int swipeFlags = ShadowItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        int fromPosition = source.getAdapterPosition();
        int toPosition = target.getAdapterPosition();
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mAdapter.mItems, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mAdapter.mItems, i, i - 1);
            }
        }
        mAdapter.notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        int position = viewHolder.getAdapterPosition();
        mAdapter.mItems.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return .25f;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (actionState != ShadowItemTouchHelper.ACTION_STATE_IDLE) {
            ((ViewHolder) viewHolder).onItemSelected();
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        ((ViewHolder) viewHolder).onItemClear();
    }
}
