package io.github.easyintent.quickref.fragment;


public interface AdapterListener<T> {
    void onItemTap(T item, int index);
    void onMultiSelectionStart();
    void onSelectedItemsChanged();
}
