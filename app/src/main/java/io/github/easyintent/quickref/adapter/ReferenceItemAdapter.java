package io.github.easyintent.quickref.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import io.github.easyintent.quickref.R;
import io.github.easyintent.quickref.data.ReferenceItem;
import io.github.easyintent.quickref.fragment.AdapterListener;

public class ReferenceItemAdapter extends RecyclerView.Adapter<ReferenceItemAdapter.ViewHolder> {

    private static final Logger logger = LoggerFactory.getLogger(ReferenceItemAdapter.class);

    private List<ReferenceItem> list;
    private Set<ReferenceItem> selectedItems;
    private AdapterListener<ReferenceItem> listener;

    private boolean selectionMode;

    public ReferenceItemAdapter(List<ReferenceItem> list, AdapterListener<ReferenceItem> listener) {
        this.list = list;
        this.listener = listener;
        selectedItems = new LinkedHashSet<>();
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
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public boolean isSelectionMode() {
        return selectionMode;
    }

    public Set<ReferenceItem> getSelectedItems() {
        return Collections.unmodifiableSet(selectedItems);
    }

    public void clearSelection() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public void startSelectionMode() {
        this.selectionMode = true;
        notifyDataSetChanged();
    }

    public void endSelectionMode() {
        selectedItems.clear();
        selectionMode = false;
        notifyDataSetChanged();
    }

    private void addItemSelection(ReferenceItem item) {
        selectedItems.add(item);
        notifyDataSetChanged();
    }

    private void removeItemSelection(ReferenceItem item) {
        selectedItems.remove(item);
        notifyDataSetChanged();
    }

    private boolean isSelected(ReferenceItem item) {
        return selectedItems.contains(item);
    }

    final class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private TextView title;
        private TextView detail;
        private TextView command;
        private View selectionOverlay;

        public ViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title_view);
            detail = itemView.findViewById(R.id.detail_view);
            command = itemView.findViewById(R.id.command_view);

            selectionOverlay = itemView.findViewById(R.id.selection_overlay);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(ReferenceItem item) {
            title.setText(item.getTitle());
            detail.setText(item.getSummary());

            if (item.hasCommand()) {
                command.setText(item.getCommand());
                command.setVisibility(View.VISIBLE);
            } else {
                command.setVisibility(View.GONE);
            }

            updateSelectionOverlay(item);
        }


        private void updateSelectionOverlay(ReferenceItem item) {
            if (selectionMode && isSelected(item)) {
                selectionOverlay.setVisibility(View.VISIBLE);
            } else {
                selectionOverlay.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            if (selectionMode) {
                addOrRemoveItem();
                logger.debug("Selected items: {}", selectedItems);
            } else {
                int pos = getLayoutPosition();
                listener.onItemTap(list.get(pos), pos);
            }
        }

        private void addOrRemoveItem() {
            int i = getLayoutPosition();
            ReferenceItem item = list.get(i);
            if (isSelected(item)) {
                removeItemSelection(item);
            } else {
                addItemSelection(item);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if (selectionMode) {
                // already in selection mode
                return false;
            }
            listener.onMultiSelectionStart();
            selectionMode = true;

            int i = getLayoutPosition();
            addItemSelection(list.get(i));
            return true;
        }
    }
}
