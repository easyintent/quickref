package io.github.easyintent.quickref.util;


import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.List;

import io.github.easyintent.quickref.data.ReferenceItem;

public final class ReferenceListSelection {

    /** Get list of selected items.
     *
     * @param list
     * @param checked
     * @return
     */
    public static List<String> getChecked(List<ReferenceItem> list, SparseBooleanArray checked) {
        List<String> selectedArray = new ArrayList<>();
        int n = checked.size();
        for (int i=0; i < n; i++) {
            int key = checked.keyAt(i);
            boolean value = checked.valueAt(i);
            if (value) {
                selectedArray.add(list.get(key).getId());
            }
        }
        return selectedArray;
    }
}
