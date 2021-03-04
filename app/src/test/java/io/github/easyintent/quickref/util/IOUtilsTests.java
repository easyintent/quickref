package io.github.easyintent.quickref.util;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.hamcrest.core.IsEqual.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class IOUtilsTests {

    private static final int TEST_DATA_LEN = 4567;

    @Test
    public void testCopy() throws IOException {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        for (int i=0; i<TEST_DATA_LEN; i++) {
            data.write((byte) (i % 255));
        }

        byte[] source = data.toByteArray();
        ByteArrayInputStream input = new ByteArrayInputStream(source);

        IOUtils.copy(input, output);

        byte[] result = output.toByteArray();

        assertThat(result, equalTo(source));
    }
}
