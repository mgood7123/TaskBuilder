package smallville7123.DraggableSwipableExpandableRecyclerView.Contents.Examples;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import smallville7123.DraggableSwipableExpandableRecyclerView.Contents.ExpandableView;
import smallville7123.DraggableSwipableExpandableRecyclerView.Contents.RecyclerListAdapter;
import smallville7123.DraggableSwipableExpandableRecyclerView.Contents.SimpleItemTouchHelperCallback;

public class ExampleRecyclerView extends RecyclerView {

    public static void onCreate(Activity activity) {
        activity.setContentView(new ExampleRecyclerView(activity));
    }

    RecyclerListAdapter adapter;
    public ExampleRecyclerView(Context context) {
        this(context, null);
    }

    public ExampleRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExampleRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        adapter = new RecyclerListAdapter();
        setAdapter(adapter);
        setLayoutManager(new LinearLayoutManager(context));

        new ItemTouchHelper(new SimpleItemTouchHelperCallback(adapter)).attachToRecyclerView(this);

        // create 50 expandable items
        for (int i = 0; i < 50; i++) {
            addItem("Header " + i, "Content " + i);
        }
    }

    public void addItem(String headerName, String contentName) {
        // get context
        Context context = getContext();

        // create header
        TextView header = new TextView(context);
        header.setText(headerName);
        header.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50f);

        // create context
        TextView content = new TextView(context);
        content.setText(contentName);
        content.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40f);

        // create toasts
        Toast headerToast = Toast.makeText(context, headerName + " clicked", Toast.LENGTH_SHORT);
        Toast contentToast = Toast.makeText(context, contentName + " clicked", Toast.LENGTH_SHORT);

        // create the expandable view and add it to the recycler view
        adapter.mItems.add(new ExpandableView(context) {
            {
                setHeader(header);
                setContent(content);
                setOnHeaderClicked(() -> {
                    contentToast.cancel();
                    headerToast.show();
                });
                setOnContentClicked(() -> {
                    headerToast.cancel();
                    contentToast.show();
                });
            }
        });
        adapter.notifyDataSetChanged();
    }
}
