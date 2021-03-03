package io.github.easyintent.quickref.model;

import java.util.List;

import androidx.annotation.NonNull;

public class ReferenceListData {
    private final List<ReferenceItem> list;
    private final String message;

    public static ReferenceListData of(@NonNull List<ReferenceItem> list) {
        return new ReferenceListData(list);
    }

    public static ReferenceListData of(String message) {
        return new ReferenceListData(message);
    }

    private ReferenceListData(@NonNull List<ReferenceItem> list) {
        this.list = list;
        message = null;
    }

    private ReferenceListData(@NonNull String message) {
        this.message = message;
        list = null;
    }

    public boolean hasList() {
        return list != null;
    }

    public boolean hasMessage() {
        return message != null;
    }

    public List<ReferenceItem> getList() {
        return list;
    }

    public String getMessage() {
        return message;
    }
}
