package io.github.easyintent.quickref.util;


import com.bignerdranch.android.multiselector.MultiSelector;

import java.util.ArrayList;
import java.util.List;

import io.github.easyintent.quickref.data.ReferenceItem;

public final class ReferenceListSelection {

    public static List<String> getSelectedIds(List<ReferenceItem> list, MultiSelector selector) {
        int n = list.size();
        List<String> selectedList = new ArrayList<>();
        for (int i=0; i<n; i++) {
            if (selector.isSelected(i, 0)) {
                selectedList.add(list.get(i).getId());
            }
        }
        return selectedList;
    }
}
