package smallville7123.DraggableSwipableExpandableRecyclerView.Contents;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

public class ToastList {
    HashMap<Integer, Toast> toasts = new HashMap<>();

    int key = 0;

    @IntDef(value = {
            LENGTH_SHORT,
            LENGTH_LONG
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Duration {}


    public int add(Context context, CharSequence text, @Duration int duration) {
        toasts.put(key, Toast.makeText(context, text, duration));
        return key++;
    }

    Toast lastShownToast;

    public void show(int key) {
        if (lastShownToast != null) lastShownToast.cancel();
        lastShownToast = toasts.get(key);
        if (lastShownToast != null) lastShownToast.show();
    }
}
