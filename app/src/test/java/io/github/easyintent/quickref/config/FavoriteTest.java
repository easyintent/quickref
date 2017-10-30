package io.github.easyintent.quickref.config;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(RobolectricTestRunner.class)
public class FavoriteTest {

    private FavoriteConfig favoriteConfig;

    @Before
    public void setUp() {
        Context context = RuntimeEnvironment.application;
        favoriteConfig = new FavoriteConfig(context);
        favoriteConfig.clear();
    }

    @Test
    public void testSaveOne() {
        // it does not care the id format
        favoriteConfig.add("1");
        favoriteConfig.add("2");
        favoriteConfig.add("3");

        // will be retrieved first saved last
        List<String> ids = favoriteConfig.list();
        assertThat(ids, is(Arrays.asList("3", "2", "1")));
    }

    @Test
    public void testSaveMany() {
        favoriteConfig.add(Arrays.asList("1", "2", "3"));

        List<String> ids = favoriteConfig.list();
        assertThat(ids, is(Arrays.asList("3", "2", "1")));
    }

    @Test
    public void testDelete() {
        favoriteConfig.add(Arrays.asList("1", "2", "3", "4"));
        favoriteConfig.delete("3");

        List<String> ids = favoriteConfig.list();
        assertThat(ids, is(Arrays.asList("4", "2", "1")));
    }


    @Test
    public void testRearrange() {

        favoriteConfig.add(Arrays.asList("1", "2", "3", "4"));
        favoriteConfig.add("2");

        List<String> ids = favoriteConfig.list();
        assertThat(ids, is(Arrays.asList("2", "4", "3", "1")));
    }

}
