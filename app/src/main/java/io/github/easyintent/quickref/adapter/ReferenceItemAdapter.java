package io.github.easyintent.quickref.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.github.easyintent.quickref.databinding.ItemReferenceBinding;
import io.github.easyintent.quickref.model.ReferenceItem;

public class ReferenceItemAdapter extends RecyclerView.Adapter<ReferenceItemAdapter.ViewHolder> {

    private static final Logger logger = LoggerFactory.getLogger(ReferenceItemAdapter.class);

    private final List<ReferenceItem> list;
    private final Set<ReferenceItem> selectedItems;
    private final AdapterListener<ReferenceItem> listener;

    private boolean selectionMode;

    public ReferenceItemAdapter(List<ReferenceItem> list, AdapterListener<ReferenceItem> listener) {
        this.list = list;
        this.listener = listener;
        selectedItems = new LinkedHashSet<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemReferenceBinding binding = ItemReferenceBinding
                .inflate(inflater, parent, false);
        return new ViewHolder(binding);
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

    public int getSelectedItemCount() {
        return selectedItems.size();
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
        onSelectedItemsChanges();
    }

    private void removeItemSelection(ReferenceItem item) {
        selectedItems.remove(item);
        onSelectedItemsChanges();
    }

    private void onSelectedItemsChanges() {
        listener.onSelectedItemsChanged();
        notifyDataSetChanged();
    }

    private boolean isSelected(ReferenceItem item) {
        return selectedItems.contains(item);
    }

    final class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private final ItemReferenceBinding binding;

        public ViewHolder(ItemReferenceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(ReferenceItem item) {
            binding.titleView.setText(item.getTitle());
            binding.detailView.setText(item.getSummary());

            if (item.hasCommand()) {
                binding.commandView.setText(item.getCommand());
                binding.commandView.setVisibility(View.VISIBLE);
            } else {
                binding.commandView.setVisibility(View.GONE);
            }

            updateSelectionOverlay(item);
        }

        private void updateSelectionOverlay(ReferenceItem item) {
            if (selectionMode && isSelected(item)) {
                binding.selectionOverlay.setVisibility(View.VISIBLE);
            } else {
                binding.selectionOverlay.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            if (selectionMode) {
                addOrRemoveItem();
            } else {
                int pos = getLayoutPosition();
                listener.onItemTap(list.get(pos), pos);
            }
        }

        private void addOrRemoveItem() {
            int i = getLayoutPosition();
            ReferenceItem item = list.get(i);
            if (isSelected(item) && getSelectedItemCount() > 1) {
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
