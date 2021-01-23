package smallville7123.example.taskbuilder;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import smallville7123.DraggableSwipableExpandableRecyclerView.Contents.ExpandableView;
import smallville7123.DraggableSwipableExpandableRecyclerView.Contents.RecyclerListAdapter;
import smallville7123.DraggableSwipableExpandableRecyclerView.Contents.ShadowItemTouchHelper;
import smallville7123.DraggableSwipableExpandableRecyclerView.Contents.SimpleShadowItemTouchHelperCallback;
import smallville7123.contextmenu.ContextWindow;
import smallville7123.contextmenu.ContextWindowItem;

import static android.R.drawable.ic_menu_delete;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static androidx.appcompat.content.res.AppCompatResources.getDrawable;
import static smallville7123.DraggableSwipableExpandableRecyclerView.Contents.ShadowItemTouchHelper.Callback.getDragInfo;

public class TaskBuilderView extends FrameLayout {

    private static final String TAG = "TaskBuilderView";
    RecyclerListAdapter adapter;
    int taskCount = 0;
    ContextWindow mainMenu;
    View taskListContainer;
    View taskEditContainer;
    TextView taskName;
    FrameLayout parametersView;

    public TaskBuilderView(Context context) {
        this(context, null);
    }

    public TaskBuilderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    Toast noView;

    void showTaskEdit(TaskList task) {
        if (task.addView == null) {
//            noView.cancel();
            noView.setText("The item '" + task.name + "' has no parameters");
            noView.show();
            addTask(task.name);
            showTaskList();
            return;
        }
        taskEditContainer.setVisibility(VISIBLE);
        taskListContainer.setVisibility(GONE);
        taskName.setText(task.name);
        if (parametersView.getChildCount() == 1) parametersView.removeViewAt(0);
        View v = task.addView.run(LayoutInflater.from(getContext()), task);
        parametersView.addView(v, new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        taskEditContainer.findViewById(R.id.doneButton).setOnClickListener(unused -> {
            if (task.checkParameters.run(v)) {
                addTask(task.name);
                showTaskList();
            }
        });
    }

    void showTaskList() {
        taskEditContainer.setVisibility(GONE);
        taskListContainer.setVisibility(VISIBLE);
    }

    public TaskBuilderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        noView = Toast.makeText(context, "Apples", Toast.LENGTH_SHORT);
        View taskBuilderView = inflate(context, R.layout.task_builder, this);
        taskListContainer = taskBuilderView.findViewById(R.id.taskListContainer);
        taskEditContainer = taskBuilderView.findViewById(R.id.taskEditContainer);
        taskName = taskEditContainer.findViewById(R.id.taskName);
        parametersView = taskEditContainer.findViewById(R.id.parametersView);
        FloatingActionButton floatingActionButton = taskBuilderView.findViewById(R.id.floatingActionButton2);
        Drawable original = floatingActionButton.getDrawable();
        Drawable onDrag = getDrawable(context, ic_menu_delete);

        RecyclerView recyclerView = taskBuilderView.findViewById(R.id.recyclerView);

        mainMenu = new ContextWindow(context);

        floatingActionButton.setOnClickListener(v -> {
            mainMenu.showAtLocation(recyclerView, Gravity.CENTER, 0, 0);
        });
        floatingActionButton.setOnLongClickListener(v -> {
            mainMenu.showAsDropDown(floatingActionButton);
            return true;
        });
        floatingActionButton.setOnDragListener((v, event) -> {
            final int action = event.getAction();
            switch(action) {
                case DragEvent.ACTION_DROP:
                    RecyclerListAdapter adapter = (RecyclerListAdapter) recyclerView.getAdapter();
                    int position = getDragInfo(event).adapterPosition;
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
                new SimpleShadowItemTouchHelperCallback(adapter)
        ).attachToRecyclerView(recyclerView);
    }

    public void addTask(String taskName) {
        taskCount++;
        Context context = getContext();

        TextView header = new TextView(context);
        header.setText(taskName);
        header.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50f);

        adapter.mItems.add(new ExpandableView(context) {
            {
                setHeader(header);
            }
        });
        adapter.notifyDataSetChanged();
    }

    void setTaskList(TaskList list, ContextWindow window, float textSize) {
        if (window.getParent() == null) {
            // we are the root window
            if (list.arrayList.isEmpty()) {
                throw new RuntimeException("attempting to add an empty task list to the root window");
            }
            for (TaskList taskList : list.arrayList) {
                if (taskList.name != null) {
                    if (taskList.arrayList.isEmpty()) {
                        ContextWindowItem item = window.addItem(taskList.name, textSize);
                        item.data = taskList.action;
                        item.setOnClickListener(unused -> showTaskEdit(taskList));
                    } else {
                        ContextWindowItem item = window.addSubMenu(taskList.name, textSize);
                        if (taskList.action != null) {
                            Log.w(TAG, "setTaskList: item '" + taskList.name + "' contains an action, however it contains sub taskList's and as such is treated as a sub menu, and sub menu actions have no effect");
                        }
                        for (TaskList taskList1 : taskList.arrayList) {
                            setTaskList(taskList1, item.subMenu, textSize);
                        }
                    }
                }
            }
        } else {
            // we are not the root window
            if (list.arrayList.isEmpty()) {
                if (list.name != null) {
                    ContextWindowItem item = window.addItem(list.name, textSize);
                    item.data = list.action;
                    item.setOnClickListener(unused -> showTaskEdit(list));
                }
            } else {
                ContextWindowItem item = window.addSubMenu(list.name, textSize);
                if (list.action != null) {
                    Log.w(TAG, "setTaskList: item '" + list.name + "' contains an action, however it contains sub taskList's and as such is treated as a sub menu, and sub menu actions have no effect");
                }
                for (TaskList taskList : list.arrayList) {
                    setTaskList(taskList, item.subMenu, textSize);
                }
            }
        }
    }

    public void setTaskList(TaskList list, float textSize) {
        setTaskList(list, mainMenu, textSize);
    }
}
