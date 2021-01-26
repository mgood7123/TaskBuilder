package smallville7123.example.tasks.tasks;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import smallville7123.example.taskbuilder.TaskBuilderView;
import smallville7123.example.taskbuilder.TaskList;
import smallville7123.example.taskbuilder.TaskParameters;
import smallville7123.example.tasks.R;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

public class Toast {
    public static void get(TaskList prebuilt) {
        prebuilt.add(
                "Show Toast",
                new TaskList.Builder() {
                    @Override
                    public View generateEditView(Context context, LayoutInflater inflater, TaskList task) {
                        return inflater.inflate(R.layout.toast_parameters, null);
                    }

                    @Override
                    public TaskParameters generateParameters() {
                        return new TaskParameters() {
                            int checkedId;
                            CharSequence text;
                            int length;

                            EditText editText;
                            RadioGroup radioGroup;

                            @Override
                            public void acquireViewIDsInEditView(View view) {
                                editText = view.findViewById(R.id.toast_text);
                                editText.setHint(TaskBuilderView.default_TextView_hint_optional);
                                radioGroup = view.findViewById(R.id.radioGroup);
                            }

                            @Override
                            public boolean checkParametersAreValid(Context context, View view) {
                                checkedId = radioGroup.getCheckedRadioButtonId();
                                if (checkedId == R.id.toastLengthShort) {
                                    length = LENGTH_SHORT;
                                } else if (checkedId == R.id.toastLengthLong) {
                                    length = LENGTH_LONG;
                                } else {
                                    makeText(context, "Toast length is invalid, please select a length", LENGTH_LONG).show();
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
                                    case LENGTH_SHORT:
                                        return "Short";
                                    case LENGTH_LONG:
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
                                return () -> makeText(context, text, length).show();
                            }

                            /**
                             * Writes the bytes for the object to the output.
                             *
                             * @param kryo
                             * @param output
                             */
                            @Override
                            public void write(Kryo kryo, Output output) {
                                output.writeString(text.toString());
                                output.writeVarInt(length, true);
                                output.writeVarInt(checkedId, true);
                            }

                            /**
                             * Reads bytes and returns a new object of the specified concrete type.
                             *
                             * @param kryo
                             * @param input
                             */
                            @Override
                            public void read(Kryo kryo, Input input) {
                                text = input.readStringBuilder();
                                length = input.readVarInt(true);
                                checkedId = input.readVarInt(true);
                            }
                        };
                    }
                }
        );
    }
}
