package io.github.easyintent.quickref.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class IOUtils {

    private static final Logger logger = LoggerFactory.getLogger(IOUtils.class);
    private static final int BUFFER_SIZE = 1024;

    public static void copy(@NonNull final InputStream input, @NonNull final OutputStream output) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int n = 0;
        while (n >= 0) {
            n = input.read(buffer);
            if (n > 0) {
                output.write(buffer);
            }
        }
    }

    public static void close(@Nullable Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                logger.debug("Failed to close", e);
            }
        }
    }
}
