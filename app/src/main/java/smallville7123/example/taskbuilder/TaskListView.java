package smallville7123.example.taskbuilder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Hashtable;

import smallville7123.DraggableSwipableExpandableRecyclerView.Contents.ExpandableView;
import smallville7123.DraggableSwipableExpandableRecyclerView.Contents.RecyclerListAdapter;
import smallville7123.DraggableSwipableExpandableRecyclerView.Contents.ShadowItemTouchHelper;
import smallville7123.DraggableSwipableExpandableRecyclerView.Contents.SimpleShadowItemTouchHelperCallback;
import smallville7123.contextmenu.ContextWindow;
import smallville7123.contextmenu.ContextWindowItem;

import static android.R.drawable.ic_menu_delete;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static androidx.appcompat.content.res.AppCompatResources.getDrawable;
import static smallville7123.DraggableSwipableExpandableRecyclerView.Contents.ShadowItemTouchHelper.Callback.getDragInfo;

public class TaskListView extends FrameLayout {

    private static final String TAG = "TaskListView";
    private final View header;
    RecyclerListAdapter adapter;
    int taskCount = 0;
    ContextWindow mainMenu;
    View taskListContainer;
    FrameLayout taskBuilderContainer;
    private final TaskBuilderView taskBuilderView;
    TextView taskListName;
    Hashtable<String, RecyclerListAdapter> adapterHashtable = new Hashtable<>();

    public TaskListView(Context context) {
        this(context, null);
    }

    public TaskListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    void showTaskList() {
        taskListContainer.setVisibility(VISIBLE);
        taskBuilderContainer.setVisibility(GONE);
    }

    void showTaskBuilder(String listName, boolean isCreate) {
        taskBuilderContainer.setVisibility(VISIBLE);
        taskListContainer.setVisibility(GONE);
        taskListName.setText(listName);
        if (isCreate) {
            RecyclerListAdapter adapter = taskBuilderView.requestNewAdapter();
            adapterHashtable.put(listName, adapter);
            taskBuilderView.setAdapter(adapter);
        } else {
            taskBuilderView.setAdapter(adapterHashtable.get(listName));
        }
        taskBuilderView.setOnEditListener(visible -> {
            if (visible) {
                header.setVisibility(GONE);
            } else {
                header.setVisibility(VISIBLE);
            }
        });
        taskBuilderContainer.findViewById(R.id.doneButton).setOnClickListener(unused -> {
            adapterHashtable.replace(listName, taskBuilderView.getAdapter());
            showTaskList();
        });
    }

    Toast toast;

    public TaskListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View taskListView = inflate(context, R.layout.task_list, this);
        taskBuilderContainer = taskListView.findViewById(R.id.taskBuilderContainer);
        taskListContainer = taskListView.findViewById(R.id.taskListContainer);

        header = taskBuilderContainer.findViewById(R.id.container_);

        FrameLayout tb = taskBuilderContainer.findViewById(R.id.taskBuilderView);
        taskBuilderView = new TaskBuilderView(context, attrs, defStyleAttr);
        tb.addView(taskBuilderView);
        taskListName = taskBuilderContainer.findViewById(R.id.taskListName);

        FloatingActionButton floatingActionButton = taskListContainer.findViewById(R.id.floatingActionButton2);
        Drawable original = floatingActionButton.getDrawable();
        Drawable onDrag = getDrawable(context, ic_menu_delete);

        RecyclerView recyclerView = taskListContainer.findViewById(R.id.recyclerView);

        toast = Toast.makeText(context, "The task name cannot be empty", Toast.LENGTH_SHORT);

        EditText editText = new EditText(context);
        editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Enter Task Name")
                .setView(editText)
                .setPositiveButton("Continue", null)
                .setNegativeButton("Cancel", null)
                .create();
        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                String name = editText.getText().toString();
                if (name.isEmpty()) {
                    toast.show();
                } else {
                    dialog.dismiss();
                    addTaskList(name);
                }
            });
        });
        floatingActionButton.setOnClickListener(v -> {
            editText.setText("");
            dialog.show();
        });
        floatingActionButton.setOnDragListener((v, event) -> {
            final int action = event.getAction();
            switch(action) {
                case DragEvent.ACTION_DROP:
                    RecyclerListAdapter adapter = (RecyclerListAdapter) recyclerView.getAdapter();
                    int position = getDragInfo(event).adapterPosition;
                    String listName = (String) ((TextView) adapter.mItems.get(position)).getText();
                    adapterHashtable.remove(listName);
                    adapter.mItems.remove(position);
                    adapter.notifyItemRemoved(position);
                case DragEvent.ACTION_DRAG_LOCATION:
                case DragEvent.ACTION_DRAG_ENTERED:
                case DragEvent.ACTION_DRAG_EXITED:
                    return true;
                case DragEvent.ACTION_DRAG_STARTED:
                    floatingActionButton.setImageDrawable(onDrag);
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    floatingActionButton.setImageDrawable(original);
                    return true;
            }
            return false;
        });

        adapter = new RecyclerListAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        new ShadowItemTouchHelper(
                new SimpleShadowItemTouchHelperCallback(adapter) {
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
                        int position = viewHolder.getAdapterPosition();
                        String listName = (String) ((TextView) adapter.mItems.get(position)).getText();
                        adapterHashtable.remove(listName);
                        super.onSwiped(viewHolder, i);
                    }
                }
        ).attachToRecyclerView(recyclerView);
    }

    public void addTaskList(String listName) {
        taskCount++;
        Context context = getContext();

        TextView title = new TextView(context);
        title.setText(listName);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50f);
        title.setOnClickListener(v -> showTaskBuilder(listName, false));

        adapter.mItems.add(title);
        adapter.notifyDataSetChanged();
        showTaskBuilder(listName, true);
    }

    public void setTaskList(TaskList list, float textSize) {
        taskBuilderView.setTaskList(list, textSize);
    }

    public void setTaskList(TaskList list, ContextWindow window, float textSize) {
        taskBuilderView.setTaskList(list, window, textSize);
    }
}
