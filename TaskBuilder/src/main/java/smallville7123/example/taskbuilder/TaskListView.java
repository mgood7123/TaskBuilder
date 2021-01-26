package smallville7123.example.taskbuilder;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Hashtable;

import smallville7123.DraggableSwipableExpandableRecyclerView.Contents.RecyclerListAdapter;
import smallville7123.DraggableSwipableExpandableRecyclerView.Contents.ShadowItemTouchHelper;
import smallville7123.DraggableSwipableExpandableRecyclerView.Contents.SimpleShadowItemTouchHelperCallback;
import smallville7123.contextmenu.ContextWindow;

import static android.R.drawable.ic_menu_delete;
import static androidx.appcompat.content.res.AppCompatResources.getDrawable;

public class TaskListView extends FrameLayout {

    private static final String TAG = "TaskListView";
    private final View header;
    RecyclerListAdapter adapter;
    int taskCount = 0;
    View taskListContainer;
    FrameLayout taskBuilderContainer;
    final TaskBuilderView taskBuilderView;
    TextView taskListName;
    Hashtable<String, RecyclerListAdapter> adapterHashtable = new Hashtable<>();
    SimpleShadowItemTouchHelperCallback simpleShadowItemTouchHelperCallback;
    RecyclerView recyclerView;
    static Kryo kryo = new Kryo();

    Output writeKryo() {
        Output output = new Output(1024, -1);
        TaskListSerializer.write(kryo, output, adapterHashtable, adapter);
        return output;
    }

    void writeKryo(FileOutputStream fileOutputStream) {
        Output output = new Output(fileOutputStream);
        TaskListSerializer.write(kryo, output, adapterHashtable, adapter);
        output.close();
    }

    public void writeKryoToFile(Context context, String relativePath) {
        writeKryoToFile(context.getFilesDir() + "/" + relativePath);
    }

    public void writeKryoToFile(String absolutePath) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(absolutePath);
            writeKryo(fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    void readKryo(Context context, Output output, TaskList list) {
        Input input = new Input(output.getBuffer(), 0, output.position());
        Pair<RecyclerListAdapter, Hashtable<String, RecyclerListAdapter>> object2 = TaskListSerializer.read(context, kryo, input, list, this);
        setAdapter(object2.first);
        adapterHashtable = object2.second;
    }

    void readKryo(Context context, FileInputStream fileInputStream, TaskList list) {
        Input input = new Input(fileInputStream);
        Pair<RecyclerListAdapter, Hashtable<String, RecyclerListAdapter>> object2 = TaskListSerializer.read(context, kryo, input, list, this);
        input.close();
        setAdapter(object2.first);
        adapterHashtable = object2.second;
    }

    public void readKryoFromRelativeFilePath(Context context, String relativePath, TaskList list) {
        readKryoFromAbsoluteFilePath(context, context.getFilesDir() + "/" + relativePath, list);
    }

    public void readKryoFromAbsoluteFilePath(Context context, String absolutePath, TaskList list) {
        try {
            FileInputStream fileInputStream = new FileInputStream(absolutePath);
            readKryo(context, fileInputStream, list);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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

        recyclerView = taskListContainer.findViewById(R.id.recyclerView);

        Toast empty = Toast.makeText(context, "The project name cannot be empty", Toast.LENGTH_SHORT);
        Toast exists = Toast.makeText(context, "The project name is taken by another project", Toast.LENGTH_SHORT);

        EditText editText = new EditText(context);
        editText.setHint(TaskBuilderView.default_TextView_hint_required);
        editText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Enter Project Name")
                .setView(editText)
                .setPositiveButton("Continue", null)
                .setNegativeButton("Cancel", null)
                .create();
        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                String name = editText.getText().toString();
                if (name.isEmpty()) {
                    empty.show();
                } else if (adapterHashtable.containsKey(name)) {
                    exists.show();
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
            ShadowItemTouchHelper.Callback.DragInfo dragInfo = ShadowItemTouchHelper.Callback.getDragInfo(event);
            switch(action) {
                case DragEvent.ACTION_DROP:
                    RecyclerListAdapter adapter = (RecyclerListAdapter) recyclerView.getAdapter();
                    int position = dragInfo.adapterPosition;
                    String listName = (String) ((TextView) adapter.mItems.get(position)).getText();
                    adapterHashtable.remove(listName);
                    adapter.mItems.remove(position);
                    adapter.notifyItemRemoved(position);
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

        adapter = new RecyclerListAdapter();

        simpleShadowItemTouchHelperCallback = new SimpleShadowItemTouchHelperCallback(null) {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
                int position = viewHolder.getAdapterPosition();
                String listName = (String) ((TextView) adapter.mItems.get(position)).getText();
                adapterHashtable.remove(listName);
                super.onSwiped(viewHolder, i);
            }
        };
        setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        ShadowItemTouchHelper shadowItemTouchHelper = new ShadowItemTouchHelper(simpleShadowItemTouchHelperCallback);
        shadowItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void addTaskList(String listName) {
        populate(getContext(), adapter, listName);
        showTaskBuilder(listName, true);
    }

    public void setTaskList(TaskList list, float textSize) {
        taskBuilderView.setTaskList(list, textSize);
    }

    public void setTaskList(TaskList list, ContextWindow window, float textSize) {
        taskBuilderView.setTaskList(list, window, textSize);
    }

    public void populate(Context context, RecyclerListAdapter adapter, String listName) {
        taskCount++;

        TextView title = new TextView(context);
        title.setText(listName);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50f);
        title.setOnClickListener(v -> showTaskBuilder(listName, false));

        adapter.mItems.add(title);
        adapter.notifyDataSetChanged();
    }
}
