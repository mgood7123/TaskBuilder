package smallville7123.example.taskbuilder;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;

public class TaskList {
    ArrayList<TaskList> arrayList = new ArrayList<>();
    public final String name;

    public interface Parameters {
        void acquireViewIDsInEditView(View view);
        boolean checkParametersAreValid(Context context, View view);
        void restoreParametersInEditView(Context context, View view);
        SpannableStringBuilder getParameterDescription();
        Runnable generateAction(Context context);

        class DescriptionBuilder {
            final private SpannableStringBuilder builder = new SpannableStringBuilder();

            final static StyleSpan normal = new StyleSpan(Typeface.NORMAL);
            final static StyleSpan italic = new StyleSpan(Typeface.ITALIC);
            final static StyleSpan bold = new StyleSpan(Typeface.BOLD);
            final static StyleSpan bold_italic = new StyleSpan(Typeface.BOLD_ITALIC);

            /**
             * duplicates the given StyleSpan so it can be reused on another region of text
             * @param styleSpan the StyleSpan to duplicate
             * @return a new CharacterStyle object with the same effects as the given styleSpan
             */
            public CharacterStyle copyStyleSpan(StyleSpan styleSpan) {
                return CharacterStyle.wrap(styleSpan);
            }

            /**
             * appends the specified text, with a style applied
             * @param text the text to append
             * @param styleSpan the style to apply
             * @return this
             */
            public DescriptionBuilder appendStyleSpan(CharSequence text, StyleSpan styleSpan) {
                builder.append(text, copyStyleSpan(styleSpan), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                return this;
            }

            /**
             * appends the specified text, with normal style applied
             * @param text the text to append
             * @return this
             */
            public DescriptionBuilder append(CharSequence text) {
                return appendStyleSpan(text, normal);
            }

            /**
             * appends the specified text, with an <strong>italic</strong> style applied
             * @param text the text to append
             * @return this
             */
            public DescriptionBuilder appendItalic(CharSequence text) {
                return appendStyleSpan(text, italic);
            }

            /**
             * appends the specified text, with a <strong>bold</strong> style applied
             * @param text the text to append
             * @return this
             */
            public DescriptionBuilder appendBold(CharSequence text) {
                return appendStyleSpan(text, bold);
            }

            /**
             * appends the specified text, with a <strong>bold italic</strong> style applied
             * @param text the text to append
             * @return this
             */
            public DescriptionBuilder appendBoldItalic(CharSequence text) {
                return appendStyleSpan(text, bold_italic);
            }

            /**
             * Return a String containing a copy of the chars in this buffer.
             *
             * @return  a string consisting of exactly this sequence of characters
             */
            public SpannableStringBuilder getBuilder() {
                return builder;
            }
        }
    }

    public interface Builder {
        View generateEditView(Context context, LayoutInflater inflater, TaskList task);
        Parameters generateParameters();
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
