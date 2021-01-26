package smallville7123.example.taskbuilder;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.ArrayList;

public class TaskList {
    ArrayList<TaskList> arrayList = new ArrayList<>();
    public final String name;

    public TaskList getSubMenu(String subMenuName) {
        if (subMenuName != null) return null;
        if (arrayList.isEmpty()) return null;
        if (name == null) return null;
        if (name.contentEquals(subMenuName)) return this;
        if (!arrayList.isEmpty()) {
            for (TaskList taskList : arrayList) {
                TaskList tmp = taskList.getSubMenu(subMenuName);
                if (tmp != null) return tmp;
            }
        }
        return null;
    }

    public TaskList getTask(String taskName) {
        if (taskName == null) return null;
        if (name != null && name.contentEquals(taskName)) return this;
        if (!arrayList.isEmpty()) {
            for (TaskList taskList : arrayList) {
                TaskList tmp = taskList.getTask(taskName);
                if (tmp != null) return tmp;
            }
        }
        return null;
    }

    public interface Builder {
        View generateEditView(Context context, LayoutInflater inflater, TaskList task);
        TaskParameters generateParameters();
    }

    Builder builder;

    public TaskList(String name, Builder builder) {
        this.name = name;
        this.builder = builder;
    }

    public TaskList() {
        this.name = null;
        this.builder = null;
    }

    /**
     * adds a new task and returns the constructed task, this is often used as a task
     */
    public TaskList add(String name, Builder builder) {
        TaskList l = new TaskList(name, builder);
        arrayList.add(l);
        return l;
    }

    /**
     * adds a new task and returns the constructed task, this is often used as a task
     */
    public TaskList add(String name, String description) {
        TaskList l = new TaskList(name, new Builder() {
            @Override
            public View generateEditView(Context context, LayoutInflater inflater, TaskList task) {
                return null;
            }

            @Override
            public TaskParameters generateParameters() {
                return new TaskParameters() {
                    @Override
                    public void acquireViewIDsInEditView(View view) {}

                    @Override
                    public boolean checkParametersAreValid(Context context, View view) {
                        return false;
                    }

                    @Override
                    public void restoreParametersInEditView(Context context, View view) {
                    }

                    @Override
                    public SpannableStringBuilder getParameterDescription() {
                        return new SpannableStringBuilder(description);
                    }

                    @Override
                    public Runnable generateAction(Context context) {
                        return null;
                    }

                    /**
                     * Writes the bytes for the object to the output.
                     *
                     * @param kryo
                     * @param output
                     */
                    @Override
                    public void write(Kryo kryo, Output output) {
                    }

                    /**
                     * Reads bytes and returns a new object of the specified concrete type.
                     *
                     * @param kryo
                     * @param input
                     */
                    @Override
                    public void read(Kryo kryo, Input input) {
                    }
                };
            }
        });
        arrayList.add(l);
        return l;
    }

    /**
     * adds a new task and returns the constructed task, this is often used as a sub menu
     */
    public TaskList add(String name) {
        TaskList l = new TaskList(name, null);
        arrayList.add(l);
        return l;
    }

    /**
     * adds an existing task list and returns the given task list
     */
    public TaskList add(TaskList taskList) {
        arrayList.add(taskList);
        return taskList;
    }
}
