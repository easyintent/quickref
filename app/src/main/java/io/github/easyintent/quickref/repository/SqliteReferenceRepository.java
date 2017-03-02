package io.github.easyintent.quickref.repository;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import io.github.easyintent.quickref.R;
import io.github.easyintent.quickref.data.ReferenceItem;

public class SqliteReferenceRepository implements ReferenceRepository {

    private static final Logger logger = LoggerFactory.getLogger(SqliteReferenceRepository.class);

    private static final String REF_TABLE = "quickref";

    // the main category name
    //
    private static final String MAIN_CATEGORY = "main";

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

    @NonNull
    @Override
    public List<ReferenceItem> list(@Nullable String category) throws RepositoryException {

        SQLiteDatabase sqlite = null;
        if (category == null) {
            category = MAIN_CATEGORY;
        }

        List<ReferenceItem> result = null;

        File dbFile = getDbFile();

        Cursor cursor = null;
        Cursor titleCursor = null;

        try {
            sqlite = SQLiteDatabase.openDatabase(dbFile.getAbsolutePath(), null, SQLiteDatabase.OPEN_READONLY);

            cursor = sqlite.query(REF_TABLE,
                    new String[]{"id", "category", "children", "title", "summary", "command", "priority"},
                    "category = ?",
                    new String[]{category},
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
            close(titleCursor);
            close(sqlite);
        }

        return result;
    }


    private  List<ReferenceItem> createList(Cursor cursor) {
        List<ReferenceItem> list = new ArrayList<>();

        do {
            String id = cursor.getString(cursor.getColumnIndexOrThrow("category"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
            String children = cursor.getString(cursor.getColumnIndexOrThrow("children"));
            String summary = cursor.getString(cursor.getColumnIndexOrThrow("summary"));
            String command = cursor.getString(cursor.getColumnIndexOrThrow("command"));

            ReferenceItem item = new ReferenceItem();
            item.setId(id);
            item.setCategory(category);
            item.setChildren(children);
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
