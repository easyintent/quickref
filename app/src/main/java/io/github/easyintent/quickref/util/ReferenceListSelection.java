package io.github.easyintent.quickref.util;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.github.easyintent.quickref.model.ReferenceItem;

public final class ReferenceListSelection {

    public static List<String> getSelectedIds(Set<ReferenceItem> selectedItems) {
        List<String> selectedList = new ArrayList<>();
        for (ReferenceItem selectedItem : selectedItems) {
            selectedList.add(selectedItem.getId());
        }
        return selectedList;
    }
}
