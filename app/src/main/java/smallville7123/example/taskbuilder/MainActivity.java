package smallville7123.example.taskbuilder;

import android.app.Activity;
import android.os.Bundle;

import smallville7123.example.tasks.Tasks;

public class MainActivity extends Activity {
    TaskListView taskListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        taskListView = new TaskListView(this);
        setContentView(taskListView);
        TaskList list = new TaskList();
        Tasks.addTaskList(list);
        taskListView.setTaskList(list, 40f);
        taskListView.setOnDoneButtonClicked(() -> taskListView.writeKryoToFile(this, "kryo") );
        taskListView.readKryoFromRelativeFilePath(this, "kryo", list);
    }

    @Override
    protected void onPause() {
        taskListView.writeKryoToFile(this, "kryo");
        super.onPause();
    }
}