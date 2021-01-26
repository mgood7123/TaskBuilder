package smallville7123.example.taskbuilder;

import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class TaskSerializationMarkers {
    private static final int PROJECT_BEGIN = 1;
    private static final int PROJECT_END = 2;
    private static final int TASK_LIST_BEGIN = 3;
    private static final int TASK_LIST_END = 4;
    private static final int TASK_BEGIN = 5;
    private static final int TASK_END = 6;
    private static final int PARAMETER_BEGIN = 7;
    private static final int PARAMETER_END = 8;

    private static void write(final Output output, final int flag) {
        output.writeVarInt(flag, true);
    }
    
    public static void projectBegin(final Output output) {
        write(output, PROJECT_BEGIN);
    }

    public static void projectEnd(final Output output) {
        write(output, PROJECT_END);
    }

    public static void taskListBegin(final Output output) {
        write(output, TASK_LIST_BEGIN);
    }

    public static void taskListEnd(final Output output) {
        write(output, TASK_LIST_END);
    }

    public static void taskBegin(final Output output) {
        write(output, TASK_BEGIN);
    }

    public static void taskEnd(final Output output) {
        write(output, TASK_END);
    }

    public static void parameterBegin(final Output output) {
        write(output, PARAMETER_BEGIN);
    }

    public static void parameterEnd(final Output output) {
        write(output, PARAMETER_END);
    }

    private static boolean read(final Input input, final int flag) {
        if (input.end()) return false;
        int position = input.position();
        if (flag != input.readVarInt(true)) {
            input.setPosition(position);
            return false;
        }
        return true;
    }

    public static boolean projectBegin(final Input input) {
        return read(input, PROJECT_BEGIN);
    }

    public static boolean projectEnd(final Input input) {
        return read(input, PROJECT_END);
    }

    public static boolean taskListBegin(final Input input) {
        return read(input, TASK_LIST_BEGIN);
    }

    public static boolean taskListEnd(final Input input) {
        return read(input, TASK_LIST_END);
    }

    public static boolean taskBegin(final Input input) {
        return read(input, TASK_BEGIN);
    }

    public static boolean taskEnd(final Input input) {
        return read(input, TASK_END);
    }

    public static boolean parameterBegin(final Input input) {
        return read(input, PARAMETER_BEGIN);
    }

    public static boolean parameterEnd(final Input input) {
        return read(input, PARAMETER_END);
    }
}
