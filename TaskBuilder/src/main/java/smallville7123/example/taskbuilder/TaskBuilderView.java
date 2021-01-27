package smallville7123.example.taskbuilder;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.util.AtomicFile;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import smallville7123.DraggableSwipableExpandableRecyclerView.Contents.ExpandableView;
import smallville7123.DraggableSwipableExpandableRecyclerView.Contents.RecyclerListAdapter;
import smallville7123.DraggableSwipableExpandableRecyclerView.Contents.ShadowItemTouchHelper;
import smallville7123.DraggableSwipableExpandableRecyclerView.Contents.SimpleShadowItemTouchHelperCallback;
import smallville7123.DraggableSwipableExpandableRecyclerView.Contents.ViewHolder;
import smallville7123.contextmenu.ContextWindow;
import smallville7123.contextmenu.ContextWindowItem;

import static android.R.drawable.ic_menu_delete;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

public class TaskBuilderView extends FrameLayout {

    public static final String default_TextView_hint_optional = "(Optional)";
    public static final String default_TextView_hint_required = "(Required)";

    private static final String TAG = "TaskBuilderView";
    private static final CharSequence NO_DESCRIPTION = "No Description Provided";
    RecyclerView recyclerView;
    RecyclerListAdapter adapter;
    SimpleShadowItemTouchHelperCallback simpleShadowItemTouchHelperCallback;
    int taskCount = 0;
    ContextWindow mainMenu;
    View taskListContainer;
    View taskEditContainer;
    TextView taskName;
    FrameLayout parametersView;
    EditListener onEditListener;
    static Object step_tag = new Object();
    static Object task_tag = new Object();
    static int orange = Color.rgb(255,165, 0);
    static Kryo kryo = new Kryo();

    Output writeKryo() {
        Output output = new Output(1024, -1);
        TaskBuilderSerializer.write(kryo, output, adapter);
        return output;
    }

    void writeKryo(String name) {
        AtomicFile atomicFile = new AtomicFile(new File(name));
        try {
            FileOutputStream backup = atomicFile.startWrite();
            Output output = new Output(backup);
            TaskBuilderSerializer.write(kryo, output, adapter);
            output.flush();
            atomicFile.finishWrite(backup);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void readKryo(Context context, String name, TaskList list) {
        AtomicFile atomicFile = new AtomicFile(new File(name));
        try {
            FileInputStream fileInputStream = atomicFile.openRead();
            Input input = new Input(fileInputStream);
            RecyclerListAdapter recyclerListAdapter = TaskBuilderSerializer.read(context, kryo, input, list, this);
            input.close(); // fileInputStream.close
            setAdapter(recyclerListAdapter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void writeKryoToFile(Context context, String relativePath) {
        writeKryoToFile(context.getFilesDir() + "/" + relativePath);
    }

    public void writeKryoToFile(String absolutePath) {
        writeKryo(absolutePath);
    }

    void readKryo(Context context, Output output, TaskList list) {
        if (output == null) return;
        Input input = new Input(output.getBuffer(), 0, output.position());
        RecyclerListAdapter recyclerListAdapter = TaskBuilderSerializer.read(context, kryo, input, list, this);
        setAdapter(recyclerListAdapter);
    }

    public void readKryoFromRelativeFilePath(Context context, String relativePath, TaskList list) {
        readKryoFromAbsoluteFilePath(context, context.getFilesDir() + "/" + relativePath, list);
    }

    public void readKryoFromAbsoluteFilePath(Context context, String absolutePath, TaskList list) {
        readKryo(context, absolutePath, list);
    }

    public static RecyclerListAdapter requestNewAdapter() {
        return new RecyclerListAdapter() {
            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                View view = mItems.get(position);
                if (view != null) {
                    TextView step = view.findViewWithTag(step_tag);
                    if (step != null) {
                        step.setText((position + 1) + ". ");
                    }
                }
                holder.setItem(view);
            }
        };
    }

    public void setAdapter(RecyclerListAdapter adapter) {
        this.adapter = adapter;
        recyclerView.setAdapter(adapter);
        simpleShadowItemTouchHelperCallback.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public RecyclerListAdapter getAdapter() {
        return adapter;
    }

    public interface EditListener {
        void run(boolean visible);
    }

    public void setOnEditListener(EditListener editListener) {
        onEditListener = editListener;
    }

    public TaskBuilderView(Context context) {
        this(context, null);
    }

    public TaskBuilderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    Toast noView;

    public void populate(Context context, RecyclerListAdapter recyclerListAdapter, TaskParameters params, String taskName, TaskList taskList) {
        // we can instance everything except the view

        TaskList task = taskList.getTask(taskName);
        if (task == null) {
            Log.e(TAG, "The item '" + taskName + "' does not exist");
            return;
        }
        if (task.builder == null) {
            Log.e(TAG, "The item '" + task.name + "' has no builder");
            return;
        }

        TextView title = new TextView(context);
        title.setText(task.name);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50f);
        title.setTag(task_tag);

        TextView parameterDesc = new TextView(context);
        parameterDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);

        SpannableStringBuilder description = null;

        if (params != null) {
            description = params.getParameterDescription();
        }

        if (description != null) {
            parameterDesc.setText(description, TextView.BufferType.SPANNABLE);
        } else {
            parameterDesc.setText(NO_DESCRIPTION, TextView.BufferType.SPANNABLE);
        }

        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        content.addView(title, new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 1f));
        content.addView(parameterDesc, new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 1f));

        LinearLayout header = new LinearLayout(context);
        header.setOrientation(LinearLayout.HORIZONTAL);
        TextView step = new TextView(context);
        step.setTag(step_tag);
        step.setText("0. ");
        step.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50f);
        step.setTextColor(orange);

        header.addView(step, new LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
        header.addView(content, new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));


        recyclerListAdapter.mItems.add(new ExpandableView(context) {
            {
                setHeader(header);
                setHeaderTag(params);
                setOnHeaderClicked(() -> {
                    showTaskEdit(task, parameterDesc, params, false);
                });
            }
        });
        recyclerListAdapter.notifyDataSetChanged();
    }

    void showTaskEdit(TaskList task, TextView parameterDesc, TaskParameters parameters, boolean isCreate) {
        // first, check edge cases: no builder, no generated view, no parameters
        if (task.builder == null) {
            noView.setText("The item '" + task.name + "' has no builder");
            noView.show();
            if (isCreate) addTask(task, null);
            showTaskList();
            return;
        }
        Context context = getContext();
        View generated = task.builder.generateEditView(context, LayoutInflater.from(context), task);
        if (isCreate) parameters = task.builder.generateParameters();
        if (generated == null) {
            noView.setText("The item '" + task.name + "' has no view");
            noView.show();
            if (isCreate) {
                TextView desc = addTask(task, parameters);
                desc.setText(parameters.getParameterDescription());
            }
            showTaskList();
            return;
        }
        if (parameters == null) {
            noView.setText("The item '" + task.name + "' has no parameters");
            noView.show();
            if (isCreate) addTask(task, null);
            showTaskList();
            return;
        }
        // everything seems to be stable, get to work
        initEditList(context, task, generated, parameters, parameterDesc, isCreate);
    }

    void initEditList(Context context, TaskList task, View generated, TaskParameters parameters, TextView parameterDesc, boolean isCreate) {
        taskListContainer.setVisibility(GONE);
        taskEditContainer.setVisibility(VISIBLE);
        if (onEditListener != null) onEditListener.run(true);
        taskName.setText(task.name);
        if (parametersView.getChildCount() == 1) parametersView.removeViewAt(0);
        parametersView.addView(generated, new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        parameters.acquireViewIDsInEditView(generated);
        if (!isCreate) parameters.restoreParametersInEditView(context, generated);
        taskEditContainer.findViewById(R.id.doneButton).setOnClickListener(unused -> {
            if (parameters.checkParametersAreValid(context, generated)) {
                TextView desc = isCreate ? addTask(task, parameters) : parameterDesc;
                desc.setText(parameters.getParameterDescription());
                showTaskList();
            }
        });
    }

    void showTaskList() {
        taskEditContainer.setVisibility(GONE);
        if (onEditListener != null) onEditListener.run(false);
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

        recyclerView = taskBuilderView.findViewById(R.id.recyclerView);

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
            ShadowItemTouchHelper.Callback.DragInfo dragInfo = ShadowItemTouchHelper.Callback.getDragInfo(event);
            switch(action) {
                case DragEvent.ACTION_DROP:
                    RecyclerListAdapter adapter = (RecyclerListAdapter) recyclerView.getAdapter();
                    int position = dragInfo.adapterPosition;
                    adapter.mItems.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, adapter.mItems.size() - position);
                case DragEvent.ACTION_DRAG_LOCATION:
                case DragEvent.ACTION_DRAG_ENTERED:
                case DragEvent.ACTION_DRAG_EXITED:
                    return true;
                case DragEvent.ACTION_DRAG_STARTED:
                    if (dragInfo == null) return false;
                    floatingActionButton.setImageDrawable(onDrag);
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    floatingActionButton.setImageDrawable(original);
                    return true;
            }
            return false;
        });

        adapter = requestNewAdapter();

        simpleShadowItemTouchHelperCallback = new SimpleShadowItemTouchHelperCallback(null);
        setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        ShadowItemTouchHelper shadowItemTouchHelper = new ShadowItemTouchHelper(simpleShadowItemTouchHelperCallback);
        shadowItemTouchHelper.attachToRecyclerView(recyclerView);

        FloatingActionButton runListButton = taskBuilderView.findViewById(R.id.floatingActionButton3);
        runListButton.setOnClickListener(v -> run());
    }

    public static void run(Context context, RecyclerListAdapter adapter) {
        if (adapter == null) return;
        for (View mItem : adapter.mItems) {
            if (mItem instanceof ExpandableView) {
                ExpandableView item = (ExpandableView) mItem;
                Object tag = item.getHeaderTag();
                if (tag instanceof TaskParameters) {
                    TaskParameters parameters = (TaskParameters) tag;
                    Runnable action = parameters.generateAction(context);
                    if (action != null) action.run();
                }
            }
        }
    }

    public void run(RecyclerListAdapter adapter) {
        run(getContext(), adapter);
    }

    public void run() {
        run(adapter);
    }

    public TextView addTask(TaskList task, TaskParameters parameters) {
        taskCount++;
        Context context = getContext();

        TextView title = new TextView(context);
        title.setText(task.name);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50f);
        title.setTag(task_tag);

        TextView parameterDesc = new TextView(context);
        parameterDesc.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f);
        parameterDesc.setText(NO_DESCRIPTION, TextView.BufferType.SPANNABLE);

        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        content.addView(title, new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 1f));
        content.addView(parameterDesc, new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 1f));

        LinearLayout header = new LinearLayout(context);
        header.setOrientation(LinearLayout.HORIZONTAL);
        TextView step = new TextView(context);
        step.setTag(step_tag);
        step.setText("0. ");
        step.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50f);
        step.setTextColor(orange);

        header.addView(step, new LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
        header.addView(content, new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));


        adapter.mItems.add(new ExpandableView(context) {
            {
                setHeader(header);
                setHeaderTag(parameters);
                setOnHeaderClicked(() -> {
                    showTaskEdit(task, parameterDesc, parameters, false);
                });
            }
        });
        adapter.notifyDataSetChanged();
        return parameterDesc;
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
                        item.setOnClickListener(unused -> showTaskEdit(taskList, taskName, null, true));
                    } else {
                        ContextWindowItem item = window.addSubMenu(taskList.name, textSize);
                        if (taskList.builder != null) {
                            Log.w(TAG, "setTaskList: item '" + taskList.name + "' contains a task builder, however it contains sub taskList's and as such is treated as a sub menu, and sub menu task builder's have no effect");
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
                    item.setOnClickListener(unused -> showTaskEdit(list, taskName, null, true));
                }
            } else {
                ContextWindowItem item = window.addSubMenu(list.name, textSize);
                if (list.builder != null) {
                    Log.w(TAG, "setTaskList: item '" + list.name + "' contains a task builder, however it contains sub taskList's and as such is treated as a sub menu, and sub menu task builder's have no effect");
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
