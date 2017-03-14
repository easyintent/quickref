package io.github.easyintent.quickref.config;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class FavoriteConfig {

    private static final String NAME = "favorite";

    private Context context;

    // item index since last item saved using this instance
    private int itemIndex;

    public FavoriteConfig(Context context) {
        this.context = context;
        itemIndex = 0;
    }

    /** Add item to favorite.
     *
     * @param id
     */
    public synchronized void add(String id) {
        SharedPreferences settings = getSetting();
        settings
                .edit()
                .putLong(id, System.currentTimeMillis() + itemIndex++)
                .apply();
    }

    public synchronized void add(List<String> ids) {
        SharedPreferences settings = getSetting();
        SharedPreferences.Editor editor = settings.edit();

        long time = System.currentTimeMillis();
        for (String id: ids) {
            editor.putLong(id, time + itemIndex++);
        }
        editor.apply();
    }

    public synchronized void delete(String id) {
        getSetting()
                .edit()
                .remove(id)
                .apply();
    }

    /** Delete the favorite ids.
     *
     * @param ids
     */
    public synchronized void delete(List<String> ids) {
        SharedPreferences.Editor editor = getSetting()
                .edit();
        for (String id: ids) {
            editor.remove(id);
        }
        editor.apply();
    }

    /** Get list of favorite item ids, sorted saved last first.
     *
     * @return
     */
    public synchronized List<String> list() {
        Map<String, ?> all = getSetting().getAll();
        List<? extends Map.Entry<String, ?>> entry = new LinkedList<>(all.entrySet());
        Collections.sort(entry, new Comparator<Map.Entry<String, ?>>() {
            @Override
            public int compare(Map.Entry<String, ?> e1, Map.Entry<String, ?> e2) {
                Long t1 = (Long) e1.getValue();
                Long t2 = (Long) e2.getValue();
                return t2.compareTo(t1);
            }
        });
        int n = entry.size();
        String[] array = new String[n];
        for (int i=0; i<n; i++) {
            array[i] = entry.get(i).getKey();
        }
        return Arrays.asList(array);
    }

    public synchronized void clear() {
        getSetting().edit().clear().apply();
    }

    private SharedPreferences getSetting() {
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }
}
