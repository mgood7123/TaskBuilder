package smallville7123.example.tasks;

import smallville7123.example.taskbuilder.TaskList;
import smallville7123.example.tasks.tasks.Succeed;
import smallville7123.example.tasks.tasks.Toast;

public class Tasks {
    public static void addTaskList(TaskList taskList) {
        TaskList prebuilt = taskList.add("prebuilt");
        Succeed.get(prebuilt);
        Toast.get(prebuilt);
    }
}
