package io.github.easyintent.quickref.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.List;

import io.github.easyintent.quickref.R;
import io.github.easyintent.quickref.data.ReferenceItem;

public class ReferenceAdapter extends ArrayAdapter<ReferenceItem> {

    private LayoutInflater inflater;

    public ReferenceAdapter(Context context, List<ReferenceItem> list) {
        super(context, 0, 0, list);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;
        if (row == null) {
            row = inflater.inflate(R.layout.item_reference, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        }
        ReferenceItem item = getItem(position);
        holder = (ViewHolder) row.getTag();
        holder.title.setText(item.getTitle());
        holder.detail.setText(item.getSummary());

        if (item.hasCommand()) {
            holder.command.setText(item.getCommand());
            holder.command.setVisibility(View.VISIBLE);
        } else {
            holder.command.setVisibility(View.GONE);
        }

        return row;
    }

    static final class ViewHolder {
        private TextView title;
        private TextView detail;
        private TextView command;

        public ViewHolder(View view) {
            title = (TextView) view.findViewById(R.id.title_view);
            detail = (TextView) view.findViewById(R.id.detail_view);
            command = (TextView) view.findViewById(R.id.command_view);
        }
    }
}
