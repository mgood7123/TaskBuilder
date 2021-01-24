package smallville7123.example.taskbuilder;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TaskListView taskListView = new TaskListView(this);
        setContentView(taskListView);

        TaskList list = new TaskList();
        list.add("nothing");
        list.add(
                "toast",
                new TaskList.Builder() {
                    @Override
                    public View generateEditView(Context context, LayoutInflater inflater, TaskList task) {
                        return inflater.inflate(R.layout.toast_parameters, null);
                    }

                    @Override
                    public TaskList.Parameters generateParameters() {
                        return new TaskList.Parameters() {
                            int checkedId;
                            Editable text;
                            int length;

                            EditText editText;
                            RadioGroup radioGroup;

                            @Override
                            public void acquireViewIDsInEditView(View view) {
                                editText = view.findViewById(R.id.toast_text);
                                radioGroup = view.findViewById(R.id.radioGroup);
                            }

                            @Override
                            public boolean checkParametersAreValid(Context context, View view) {
                                checkedId = radioGroup.getCheckedRadioButtonId();
                                switch (checkedId) {
                                    case R.id.toastLengthShort:
                                        length = Toast.LENGTH_SHORT;
                                        break;
                                    case R.id.toastLengthLong:
                                        length = Toast.LENGTH_LONG;
                                        break;
                                    default:
                                        Toast.makeText(context, "Toast length is invalid, please select a length", Toast.LENGTH_LONG).show();
                                        return false;
                                }
                                text = editText.getText();
                                return true;
                            }

                            @Override
                            public void restoreParametersInEditView(Context context, View view) {
                                radioGroup.check(checkedId);
                                editText.setText(text);
                            }

                            String toastLengthToString(int length) {
                                switch (length) {
                                    case Toast.LENGTH_SHORT:
                                        return "Short";
                                    case Toast.LENGTH_LONG:
                                        return "Long";
                                    default:
                                        return "Unknown";
                                }
                            }

                            @Override
                            public SpannableStringBuilder getParameterDescription() {
                                return new DescriptionBuilder()
                                        .appendItalic("Title: ")
                                        .appendBold(text)
                                        .append(", ")
                                        .appendItalic("Length: ")
                                        .appendBold(toastLengthToString(length))
                                        .getBuilder();
                            }

                            @Override
                            public Runnable generateAction(Context context) {
                                return () -> Toast.makeText(context, text, length).show();
                            }
                        };
                    }
                }
        );

        taskListView.setTaskList(list, 40f);
    }
}