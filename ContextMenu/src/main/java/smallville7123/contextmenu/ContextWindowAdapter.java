package smallville7123.contextmenu;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ContextWindowAdapter extends BaseAdapter {
    private Context mContext;
    private List<ContextWindowItem> mDataSource;
    private LayoutInflater layoutInflater;

    ContextWindowAdapter(Context context, List<ContextWindowItem> dataSource) {
        mContext = context;
        mDataSource = dataSource;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public ContextWindowItem getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        ContextWindowItem item = getItem(position);

        if (convertView == null) {
            holder = new ViewHolder();
            int res;
            if (item.subMenu == null) {
                if (item.parent != null) {
                    res = R.layout.windows_context_menu_sub_menu_back;
                } else {
                    res = R.layout.windows_context_menu_item;
                }
            } else {
                res = R.layout.windows_context_menu_sub_menu;
            }
            convertView = layoutInflater.inflate(res,
                    null
            );
            holder.title = convertView.findViewById(R.id.text_title);
            holder.chevron = convertView.findViewById(R.id.text_chevron);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // bind data
        holder.title.setText(item.title);
        holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, item.menuTextSize);
        if (holder.chevron != null) {
            holder.chevron.setTextSize(TypedValue.COMPLEX_UNIT_SP, item.menuTextSize);
        }
        return convertView;
    }

    public class ViewHolder {
        public TextView chevron;
        private TextView title;
    }
}
