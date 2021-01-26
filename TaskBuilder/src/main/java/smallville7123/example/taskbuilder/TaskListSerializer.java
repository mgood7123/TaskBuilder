package smallville7123.example.taskbuilder;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.Hashtable;

import smallville7123.DraggableSwipableExpandableRecyclerView.Contents.RecyclerListAdapter;

public class TaskListSerializer {
    /**
     * Writes the bytes for the object to the output.
     */
    public static void write(Kryo kryo, Output output, Hashtable<String, RecyclerListAdapter> adapterHashtable, RecyclerListAdapter recyclerListAdapter) {
        for (View mItem : recyclerListAdapter.mItems) {
            TaskSerializationMarkers.projectBegin(output);
            if (mItem instanceof TextView) {
                String key = (String) ((TextView) mItem).getText();
                output.writeString(key);
                RecyclerListAdapter adapter = adapterHashtable.get(key);
                TaskBuilderSerializer.write(kryo, output, adapter);
            }
            TaskSerializationMarkers.projectEnd(output);
        }
    }

    /**
     * Reads bytes and returns a new object of the specified concrete type.
     * <p>
     * Before Kryo can be used to read child objects, {@link Kryo#reference(Object)} must be called with the parent object to
     * ensure it can be referenced by the child objects. Any serializer that uses {@link Kryo} to read a child object may need to
     * be reentrant.
     */
    public static Pair<RecyclerListAdapter, Hashtable<String, RecyclerListAdapter>> read(Context context, Kryo kryo, Input input, TaskList taskList, TaskListView taskListView) {
        RecyclerListAdapter adapter = new RecyclerListAdapter();
        Hashtable<String, RecyclerListAdapter> adapterHashtable = new Hashtable<>();
        while (TaskSerializationMarkers.projectBegin(input)) {
            if (!TaskSerializationMarkers.projectEnd(input)) {
                String key = input.readString();
                taskListView.populate(context, adapter, key);
                RecyclerListAdapter adapter1 = TaskBuilderSerializer.read(context, kryo, input, taskList, taskListView.taskBuilderView);
                adapterHashtable.put(key, adapter1);
            }
            TaskSerializationMarkers.projectEnd(input);
        }
        return new Pair<>(adapter, adapterHashtable);
    }
}
