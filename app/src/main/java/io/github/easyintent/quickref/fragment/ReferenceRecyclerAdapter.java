package io.github.easyintent.quickref.fragment;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;

import java.util.List;

import io.github.easyintent.quickref.R;
import io.github.easyintent.quickref.data.ReferenceItem;

public class ReferenceRecyclerAdapter extends RecyclerView.Adapter<ReferenceRecyclerAdapter.ViewHolder> {

    private List<ReferenceItem> list;
    private OnItemTapListener<ReferenceItem> listener;
    private MultiSelector selector;

    public ReferenceRecyclerAdapter(List<ReferenceItem> list, MultiSelector selector, OnItemTapListener<ReferenceItem> listener) {
        this.list = list;
        this.selector = selector;
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

    final class ViewHolder extends SwappingHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView title;
        private TextView detail;
        private TextView command;

        public ViewHolder(View itemView) {
            super(itemView, selector);

            setSelectionModeBackgroundDrawable(ContextCompat.getDrawable(itemView.getContext(), R.drawable.list_item));

            title = itemView.findViewById(R.id.title_view);
            detail = itemView.findViewById(R.id.detail_view);
            command = itemView.findViewById(R.id.command_view);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (!selector.tapSelection(this)) {
                int pos = getLayoutPosition();
                listener.onItemTap(list.get(pos), pos);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (selector.isSelectable()) {
                return false;
            }
            listener.onMultiSelectionStart();
            selector.setSelectable(true);
            selector.setSelected(this, true);
            return true;
        }
    }
}
