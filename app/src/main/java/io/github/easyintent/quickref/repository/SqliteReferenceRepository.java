package io.github.easyintent.quickref.repository;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import io.github.easyintent.quickref.R;
import io.github.easyintent.quickref.data.ReferenceItem;

public class SqliteReferenceRepository implements ReferenceRepository {

    private static final Logger logger = LoggerFactory.getLogger(SqliteReferenceRepository.class);

    private static final String REF_TABLE = "quickref";

    private static final String SEARCH_TABLE = "quicksearch";

    // Reference database
    //
    private static final String DB_FILE = "quickref.sqlite";

    // Version metadata
    // Update version info in assets folder when content changes
    //
    private static final String DB_VERSION_FILE = "version.properties";

    private Context context;

    public SqliteReferenceRepository(Context context) {
        this.context = context;
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

        File dbFile = getDbFile();

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
    public List<ReferenceItem> listByIds(List<String> ids) throws RepositoryException {

        List<ReferenceItem> result = Collections.emptyList();

        File dbFile = getDbFile();

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
    public List<ReferenceItem> search(String query) throws RepositoryException {

        List<ReferenceItem> result = Collections.emptyList();

        String sql = "SELECT " +
                "r.id, r.parent_id, r.priority, r.leaf, r.title, r.summary, r.command " +
            "FROM " +
                REF_TABLE     + " r, " +
                SEARCH_TABLE  + " s  " +
            "WHERE r.rowid = s.docid AND " +
                SEARCH_TABLE  + " MATCH ?";

        File dbFile = getDbFile();

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

            list.add(item);

        } while (cursor.moveToNext());

        return list;
    }

    @NonNull
    private File getDbFile() throws RepositoryException {

        Properties asset = new Properties();
        String version = null;
        try {
            asset.load(context.getAssets().open(DB_VERSION_FILE));
            version = asset.getProperty("version");
            if (version == null) {
                throw new RepositoryException(context.getString(R.string.msg_prepare_data_failed));
            }
        } catch (IOException e) {
            throw new RepositoryException(context.getString(R.string.msg_prepare_data_failed), e);
        }

        File dbDir = new File(context.getFilesDir(), "repository");
        if (!dbDir.exists()) {
            dbDir.mkdirs();
        }

        File dbFile = new File(dbDir, DB_FILE + "." + version);
        if (dbFile.exists()) {
            logger.debug("database already exists: {}", dbFile.getName());
            return dbFile;
        }

        cleanUp(dbDir);
        copyFromAssetTo(dbFile);

        return dbFile;
    }

    private void cleanUp(File dbDir) {
        File[] files = dbDir.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        for (File f: files) {
            if (f.isFile()) {
                logger.debug("deleting old database: {}", f.getName());
                f.delete();
            }
        }
    }

    private void copyFromAssetTo(File dbFile) throws RepositoryException {
        OutputStream os = null;
        InputStream is = null;
        try {
            logger.debug("copying new database: {}", dbFile.getName());
            is = context.getAssets().open(DB_FILE);
            os = new FileOutputStream(dbFile);
            IOUtils.copy(is, os);
        } catch (IOException e) {
            throw new RepositoryException(context.getString(R.string.msg_prepare_data_failed), e);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
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
