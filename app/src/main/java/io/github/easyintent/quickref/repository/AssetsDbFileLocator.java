package io.github.easyintent.quickref.repository;


import android.content.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import androidx.annotation.NonNull;
import io.github.easyintent.quickref.R;

import static io.github.easyintent.quickref.util.IOUtils.close;
import static io.github.easyintent.quickref.util.IOUtils.copy;

public class AssetsDbFileLocator implements DbFileLocator {

    private static final Logger logger = LoggerFactory.getLogger(AssetsDbFileLocator.class);

    // Reference database
    //
    private static final String DB_FILE = "quickref.sqlite";

    // Version metadata
    // Update version info in assets folder when content changes
    //
    private static final String DB_VERSION_FILE = "version.properties";

    private Context context;

    public AssetsDbFileLocator(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public File findDbFile() throws RepositoryException {

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
            copy(is, os);
        } catch (IOException e) {
            throw new RepositoryException(context.getString(R.string.msg_prepare_data_failed), e);
        } finally {
            close(is);
            close(os);
        }
    }

}
