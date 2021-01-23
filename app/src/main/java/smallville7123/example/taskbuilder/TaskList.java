package smallville7123.example.taskbuilder;

import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;

import smallville7123.contextmenu.ContextWindowItem;

public class TaskList {
    ArrayList<TaskList> arrayList = new ArrayList<>();
    public final String name;
    public final AddView addView;
    public final CheckParameters checkParameters;

    public interface AddView {
        View run(LayoutInflater inflater, TaskList task);
    }

    public interface CheckParameters {
        boolean run(View view);
    }
    public interface Action {
        void run(TaskList taskList);
    }

    public final Action action;

    public TaskList(String name, AddView addView, CheckParameters checkParameters, Action action) {
        this.name = name;
        this.addView = addView;
        this.checkParameters = checkParameters;
        this.action = action;
    }

    public TaskList() {
        this.name = null;
        this.addView = null;
        this.checkParameters = null;
        this.action = null;
    }

    /**
     * adds a new task and returns the constructed task, this is often used as a task
     */
    public TaskList add(String name, AddView addView, CheckParameters checkParameters, Action action) {
        TaskList l = new TaskList(name, addView, checkParameters, action);
        arrayList.add(l);
        return l;
    }

    /**
     * adds a new task and returns the constructed task, this is often used as a sub menu
     */
    public TaskList add(String name) {
        TaskList l = new TaskList(name, null, null, null);
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
