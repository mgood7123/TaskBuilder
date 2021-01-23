package smallville7123.contextmenu;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.PopupWindow;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ContextWindow {
    private Context mContext;
    private ContextWindow parent;
    private PopupWindow popupWindow;
    ListView listView;
    ListPopupWindow listPopupWindow;
    private ContextWindowAdapter popupWindowAdapter;
    private List<ContextWindowItem> sampleData;
    private View anchor;
    private View currentParent;
    private int currentGravity;
    private int currentX;
    private int currentY;
    private String backText;
    private boolean dropdown;

    private void setup() {
        sampleData = new ArrayList<>();

        popupWindowAdapter = new ContextWindowAdapter(mContext, sampleData);

        popupWindow = new PopupWindow(mContext);
        listPopupWindow = new ListPopupWindow(mContext);


        popupWindow.setWidth(800);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);

        listPopupWindow.setWidth(800);
        listPopupWindow.setModal(true);

        listView = new ListView(mContext);
        listView.setDivider(null);
        listView.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));

        AdapterView.OnItemClickListener click = (parent, view, position, id) -> {
            ContextWindowItem item = popupWindowAdapter.getItem(position);
            if (item.onClickListener != null) {
                item.onClickListener.run(item);
            }
            ContextWindow window = item.subMenu;
            if (window == null) {
                if (item.isHeader) {
                    if (item.parent != null) {
                        window = item.parent;
                    }
                }
            }
            if (dropdown) {
                listPopupWindow.dismiss();
                if (window != null) window.showAsDropDown_(currentParent);
            } else {
                popupWindow.dismiss();
                if (window != null) window.showAtLocation_(currentParent, currentGravity, currentX, currentY);
            }
        };

        listView.setOnItemClickListener(click);
        listPopupWindow.setOnItemClickListener(click);
    }

    public ContextWindow(Context context) {
        mContext = context;
        parent = null;
        setup();
    }

    public ContextWindow(Context context, ContextWindow parent) {
        mContext = context;
        this.parent = parent;
        setup();
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }
    public ListPopupWindow getListPopupWindow() {
        return listPopupWindow;
    }

    public ContextWindowItem addBackItem(String title, float textSize) {
        ContextWindowItem item = new ContextWindowItem(title, parent, null, true);
        item.menuTextSize = textSize;
        sampleData.add(item);
        popupWindowAdapter.notifyDataSetChanged();
        return item;
    }

    public ContextWindowItem addBackItem(int index, String title, float textSize) {
        ContextWindowItem item = new ContextWindowItem(title, parent, null, true);
        item.menuTextSize = textSize;
        sampleData.add(index, item);
        popupWindowAdapter.notifyDataSetChanged();
        return item;
    }

    public ContextWindowItem addItem(String title, float textSize) {
        ContextWindowItem item = new ContextWindowItem(title, null, null, false);
        item.menuTextSize = textSize;
        sampleData.add(item);
        popupWindowAdapter.notifyDataSetChanged();
        return item;
    }

    public ContextWindowItem addItem(int index, String title, float textSize) {
        ContextWindowItem item = new ContextWindowItem(title, null, null, false);
        item.menuTextSize = textSize;
        sampleData.add(index, item);
        popupWindowAdapter.notifyDataSetChanged();
        return item;
    }

    public ContextWindowItem addSubMenu(String title, float textSize) {
        return addSubMenu(title, new ContextWindow(mContext, this), textSize);
    }

    public ContextWindowItem addSubMenu(String title, ContextWindow contextWindow, float textSize) {
        contextWindow.parent = this;
        if (backText != null) {
            contextWindow.setGoBackText(backText);
            contextWindow.addBackItem(0, backText, textSize);
        } else {
            contextWindow.addBackItem(0, title, textSize);
        }
        contextWindow.setAnchorView(anchor);

        ContextWindowItem item = new ContextWindowItem(title, parent, contextWindow, false);
        item.menuTextSize = textSize;
        sampleData.add(item);
        popupWindowAdapter.notifyDataSetChanged();
        return item;
    }

    public ContextWindowItem addSubMenu(int index, String title, float textSize) {
        return addSubMenu(index, title, new ContextWindow(mContext, this), textSize);
    }

    public ContextWindowItem addSubMenu(int index, String title, ContextWindow contextWindow, float textSize) {
        contextWindow.parent = this;
        if (backText != null) {
            contextWindow.setGoBackText(backText);
            contextWindow.addBackItem(0, backText, textSize);
        } else {
            contextWindow.addBackItem(0, title, textSize);
        }
        contextWindow.setAnchorView(anchor);

        ContextWindowItem item = new ContextWindowItem(title, parent, contextWindow, false);
        item.menuTextSize = textSize;
        sampleData.add(index, item);
        popupWindowAdapter.notifyDataSetChanged();
        return item;
    }

    void setAnchorView(@Nullable View anchor) {
        listPopupWindow.setAnchorView(anchor);
        for (ContextWindowItem item : sampleData) {
            if (item.subMenu != null) {
                item.subMenu.setAnchorView(anchor);
            }
        }
    }

    // do these matter if we do any of this every time
    // we enter a new sub menu?

    public void showAsDropDown(View anchor) {
        setAnchorView(anchor);
        showAsDropDown_(anchor);
    }

    private void showAsDropDown_(View parent) {
        currentParent = parent;
        currentGravity = Gravity.NO_GRAVITY;
        currentX = 0;
        currentY = 0;
        dropdown = true;
        popupWindow.setContentView(null);
        listView.setAdapter(null);
        listPopupWindow.setAdapter(popupWindowAdapter);
        popupWindowAdapter.notifyDataSetChanged();
        listPopupWindow.show();
    }

    public void showAtLocation(View parent, int gravity, int x, int y) {
        setAnchorView(null);
        showAtLocation_(parent, gravity, x, y);
    }

    private void showAtLocation_(View parent, int gravity, int x, int y) {
        currentParent = parent;
        currentGravity = gravity;
        currentX = x;
        currentY = y;
        dropdown = false;
        popupWindow.setContentView(listView);
        listPopupWindow.setAdapter(null);
        listView.setAdapter(popupWindowAdapter);
        popupWindowAdapter.notifyDataSetChanged();
        popupWindow.showAtLocation(parent, gravity, x, y);
    }

//    public void setMenuTextSize(float size) {
//        menuTextSize = size;
//        for (ContextWindowItem item : sampleData) {
//            if (item.subMenu != null) {
//                for (ContextWindowItem item_ : item.subMenu.sampleData) {
//                    if (item_.isHeader) item_.title = go_back;
//                }
//            }
//        }
//    }

    public void setGoBackText(String go_back) {
        backText = go_back;
        for (ContextWindowItem item : sampleData) {
            if (item.subMenu != null) {
                for (ContextWindowItem item_ : item.subMenu.sampleData) {
                    if (item_.isHeader) item_.title = go_back;
                }
            }
        }
    }

    public ContextWindow getParent() {
        return parent;
    }
}
