package smallville7123.contextmenu;

public class ContextWindowItem {
    public final boolean isHeader;
    public String title;
    public final ContextWindow parent;
    public final ContextWindow subMenu;
    OnClickListener onClickListener;
    public Object data;
    float menuTextSize = 20f;

    public interface OnClickListener {
        void run(ContextWindowItem item);
    }


    public void setOnClickListener(OnClickListener l) {
        onClickListener = l;
    }

    ContextWindowItem(String title, ContextWindow parent, ContextWindow subMenu, boolean isHeader) {
        this.isHeader = isHeader;
        this.title = title;
        this.parent = parent;
        this.subMenu = subMenu;
    }
}
