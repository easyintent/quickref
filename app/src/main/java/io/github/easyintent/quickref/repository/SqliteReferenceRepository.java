package io.github.easyintent.quickref.repository;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import androidx.annotation.NonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.easyintent.quickref.R;
import io.github.easyintent.quickref.model.ReferenceItem;

public class SqliteReferenceRepository implements ReferenceRepository {

    private static final Logger logger = LoggerFactory.getLogger(SqliteReferenceRepository.class);

    private static final String REF_TABLE = "quickref";

    private static final String SEARCH_TABLE = "quicksearch";

    private Context context;
    private DbFileLocator dbFileLocator;

    public SqliteReferenceRepository(Context context, DbFileLocator dbFileLocator) {
        this.context = context;
        this.dbFileLocator = dbFileLocator;
    }

    @Override
    public List<ReferenceItem> list(String parentId) throws RepositoryException {

        List<ReferenceItem> result = Collections.emptyList();

        String whereClause;
        String[] whereValue;

        if (parentId != null) {
            whereClause = "parent_id = ?";
            whereValue = new String[]{parentId};
        } else {
            whereClause = "parent_id IS NULL";
            whereValue = new String[0];
        }

        File dbFile = dbFileLocator.findDbFile();

        SQLiteDatabase sqlite = null;
        Cursor cursor = null;

        try {
            sqlite = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);

            cursor = sqlite.query(REF_TABLE,
                    selectColumns(),
                    whereClause,
                    whereValue,
                    null,
                    null,
                    "priority ASC",
                    null);

            if (cursor.moveToFirst()) {
                result = createList(cursor);
            }

        } catch (SQLiteException e) {
            throw new RepositoryException(context.getString(R.string.msg_reference_not_found), e);
        } finally {
            close(cursor);
            close(sqlite);
        }

        return result;
    }

    @Override
    public List<ReferenceItem> listByIds(final List<String> ids) throws RepositoryException {

        List<ReferenceItem> result = Collections.emptyList();

        File dbFile = dbFileLocator.findDbFile();

        int n = ids.size();
        StringBuilder placeholder = new StringBuilder();
        for (int i=0; i<n; i++) {
            placeholder.append("?");
            if (i+1 < n) {
                placeholder.append(",");
            }
        }

        String sql = "SELECT id, parent_id, priority, leaf, title, summary, command " +
                "FROM " + REF_TABLE + " " +
                "WHERE id IN (" + placeholder + ")";

        SQLiteDatabase sqlite = null;
        Cursor cursor = null;

        try {
            sqlite = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);

            cursor = sqlite.rawQuery(sql, ids.toArray(new String[0]));

            // save unordered result to map
            Map<String,ReferenceItem> maps = new HashMap<>();
            if (cursor.moveToFirst()) {
                do {
                    ReferenceItem item = toReferenceItem(cursor);
                    maps.put(item.getId(), item);
                } while (cursor.moveToNext());
            }

            // create new list with the same order as args list
            result = new ArrayList<>();
            for (String id: ids) {
                ReferenceItem ref = maps.get(id);
                if (ref != null) {
                    result.add(ref);
                }
            }

        } catch (SQLiteException e) {
            throw new RepositoryException(context.getString(R.string.msg_reference_not_found), e);
        } finally {
            close(cursor);
            close(sqlite);
        }

        return result;

    }

    @Override
    public List<ReferenceItem> search(String query) throws RepositoryException {

        List<ReferenceItem> result = Collections.emptyList();

        String sql = "SELECT " +
                "r.id, r.parent_id, r.priority, r.leaf, r.title, r.summary, r.command " +
            "FROM " +
                REF_TABLE     + " r, " +
                SEARCH_TABLE  + " s  " +
            "WHERE r.rowid = s.docid AND " +
                SEARCH_TABLE  + " MATCH ?";

        File dbFile = dbFileLocator.findDbFile();

        SQLiteDatabase sqlite = null;
        Cursor cursor = null;

        try {
            sqlite = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);
            cursor = sqlite.rawQuery(sql, new String[]{query});

            if (cursor.moveToFirst()) {
                result = createList(cursor);
            }

        } catch (SQLiteException e) {
            throw new RepositoryException(context.getString(R.string.msg_reference_not_found), e);
        } finally {
            close(cursor);
            close(sqlite);
        }
        return result;
    }

    @NonNull
    private String[] selectColumns() {
        return new String[]{"id", "parent_id", "priority", "leaf", "title", "summary", "command"};
    }

    private  List<ReferenceItem> createList(Cursor cursor) {
        List<ReferenceItem> list = new ArrayList<>();

        do {

            ReferenceItem item = toReferenceItem(cursor);

            list.add(item);

        } while (cursor.moveToNext());

        return list;
    }

    @NonNull
    private ReferenceItem toReferenceItem(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndexOrThrow("id"));
        String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
        String parentId = cursor.getString(cursor.getColumnIndexOrThrow("parent_id"));
        boolean leaf = cursor.getLong(cursor.getColumnIndexOrThrow("leaf")) == 1;
        String summary = cursor.getString(cursor.getColumnIndexOrThrow("summary"));
        String command = cursor.getString(cursor.getColumnIndexOrThrow("command"));

        ReferenceItem item = new ReferenceItem();
        item.setId(id);
        item.setParentId(parentId);
        item.setLeaf(leaf);
        item.setTitle(title);
        item.setSummary(summary);
        item.setCommand(command);
        return item;
    }

    private void close(SQLiteDatabase sqlite) {
        if (sqlite != null) {
            sqlite.close();
        }
    }

    private void close(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }
}
