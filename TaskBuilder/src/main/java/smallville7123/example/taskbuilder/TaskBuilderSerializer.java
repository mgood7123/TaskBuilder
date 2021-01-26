package smallville7123.example.taskbuilder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import smallville7123.DraggableSwipableExpandableRecyclerView.Contents.ExpandableView;
import smallville7123.DraggableSwipableExpandableRecyclerView.Contents.RecyclerListAdapter;

import static smallville7123.example.taskbuilder.TaskBuilderView.requestNewAdapter;
import static smallville7123.example.taskbuilder.TaskBuilderView.task_tag;

public class TaskBuilderSerializer {
    /**
     * Writes the bytes for the object to the output.
     */
    public static void write(Kryo kryo, Output output, RecyclerListAdapter recyclerListAdapter) {
        for (View mItem : recyclerListAdapter.mItems) {
            TaskSerializationMarkers.taskBegin(output);
            Object tag = null;
            if (mItem instanceof ExpandableView) {
                ExpandableView item = (ExpandableView) mItem;
                View viewWithTag = item.findViewWithTag(task_tag);
                TextView textView = (TextView) viewWithTag;
                output.writeString((String) textView.getText());
                tag = item.getHeaderTag();
            }
            TaskSerializationMarkers.taskEnd(output);
            TaskSerializationMarkers.parameterBegin(output);
            if (tag instanceof TaskParameters) {
                ((TaskParameters) tag).write(kryo, output);
            }
            TaskSerializationMarkers.parameterEnd(output);
        }
    }

    /**
     * Reads bytes and returns a new object of the specified concrete type.
     * <p>
     * Before Kryo can be used to read child objects, {@link Kryo#reference(Object)} must be called with the parent object to
     * ensure it can be referenced by the child objects. Any serializer that uses {@link Kryo} to read a child object may need to
     * be reentrant.
     */
    public static RecyclerListAdapter read(Context context, Kryo kryo, Input input, TaskList taskList, TaskBuilderView taskBuilderView) {
        RecyclerListAdapter recyclerListAdapter = requestNewAdapter();
        while (TaskSerializationMarkers.taskBegin(input)) {
            String taskName = null;
            if (!TaskSerializationMarkers.taskEnd(input)) {
                taskName = input.readString();
                TaskSerializationMarkers.taskEnd(input);
            }
            if (taskName != null) {
                TaskList task = taskList.getTask(taskName);
                if (task != null) {
                    TaskParameters params = task.builder.generateParameters();
                    TaskSerializationMarkers.parameterBegin(input);
                    params.read(kryo, input);
                    TaskSerializationMarkers.parameterEnd(input);
                    taskBuilderView.populate(context, recyclerListAdapter, params, taskName, task);
                }
            }
        }
        return recyclerListAdapter;
    }
}
