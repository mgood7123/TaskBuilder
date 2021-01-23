package smallville7123.example.taskbuilder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import smallville7123.contextmenu.ContextWindowItem;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TaskBuilderView taskBuilderView = new TaskBuilderView(this);
        setContentView(taskBuilderView);

        TaskList list = new TaskList();
        list.add("nothing");
        list.add(
                "toast",
                new TaskList.AddView() {
                    @Override
                    public View run(LayoutInflater inflater, TaskList task) {
                        return inflater.inflate(R.layout.toast_parameters, null);
                    }
                },
                new TaskList.CheckParameters() {
                    @Override
                    public boolean run(View view) {
                        return true;
                    }
                },
                new TaskList.Action() {
                    @Override
                    public void run(TaskList taskList) {
                    }
                }
                );

        taskBuilderView.setTaskList(list, 40f);
    }
}