package io.github.easyintent.quickref.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.github.easyintent.quickref.R;
import io.github.easyintent.quickref.data.ReferenceItem;

public class ReferenceRecyclerAdapter extends RecyclerView.Adapter<ReferenceRecyclerAdapter.ViewHolder> {

    private List<ReferenceItem> list;
    private OnItemTapListener listener;

    public ReferenceRecyclerAdapter(List<ReferenceItem> list, OnItemTapListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reference, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ReferenceItem item = list.get(position);
        holder.title.setText(item.getTitle());
        holder.detail.setText(item.getSummary());

        if (item.hasCommand()) {
            holder.command.setText(item.getCommand());
            holder.command.setVisibility(View.VISIBLE);
        } else {
            holder.command.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    final class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView detail;
        private TextView command;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title_view);
            detail = (TextView) itemView.findViewById(R.id.detail_view);
            command = (TextView) itemView.findViewById(R.id.command_view);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getLayoutPosition();
                    listener.onItemTap(list.get(pos), pos);
                }
            });
        }
    }


}
