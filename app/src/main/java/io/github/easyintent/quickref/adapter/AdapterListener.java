package io.github.easyintent.quickref.adapter;


public interface AdapterListener<T> {
    void onItemTap(T item, int index);
    void onMultiSelectionStart();
    void onSelectedItemsChanged();
}
