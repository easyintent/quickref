package io.github.easyintent.quickref.repository;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.easyintent.quickref.data.ReferenceItem;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(RobolectricTestRunner.class)
public class SqliteRepositoryTest {

    private ReferenceRepository repository;

    @Before
    public void setUp() {
        // test from known data
        Context context = RuntimeEnvironment.application;
        repository = new SqliteReferenceRepository(context);
    }

    @Test
    public void testListTopLevel() throws Exception {
        List<ReferenceItem> list = repository.list(null);
        assertThat(list.size(), is(2));
        assertThat(list.get(0).getId(), is("06edc567-65a2-4376-a735-2192e06600a0"));
        assertThat(list.get(1).getId(), is("c6ed7517-1f1e-4745-bd62-e6306283c7cf"));
    }

    @Test
    public void testChildList() throws Exception {
        List<ReferenceItem> list = repository.list("c6ed7517-1f1e-4745-bd62-e6306283c7cf");
        assertThat(list.size(), is(3));
        assertThat(list.get(0).getId(), is("d19b141f-e239-4cf5-9751-416b3be4a1d9"));
        assertThat(list.get(1).getId(), is("8876c244-6fc1-42b1-816b-7bcef2cfada3"));
        assertThat(list.get(2).getId(), is("f0f01621-a16e-47ee-8dee-540c57112959"));
    }

    @Test
    public void testListOrder() throws Exception {
        List<ReferenceItem> list = repository.listByIds(Arrays.asList(
                "7f4e7389-6dfa-447c-9714-0fcee7768669",
                "06edc567-65a2-4376-a735-2192e06600a0",
                "f0f01621-a16e-47ee-8dee-540c57112959",
                "a864faf8-fb4c-4097-8207-65bf2489a4f9"
        ));

        assertThat(list.size(), is(4));
        assertThat(list.get(0).getId(), is("7f4e7389-6dfa-447c-9714-0fcee7768669"));
        assertThat(list.get(1).getId(), is("06edc567-65a2-4376-a735-2192e06600a0"));
        assertThat(list.get(2).getId(), is("f0f01621-a16e-47ee-8dee-540c57112959"));
        assertThat(list.get(3).getId(), is("a864faf8-fb4c-4097-8207-65bf2489a4f9"));
    }

    @Test
    public void testSearch() throws Exception {
        String query = "2-2";
        List<ReferenceItem> list = repository.search(query);
        List<String> ids = new ArrayList<>();
        for (ReferenceItem item: list) {
            ids.add(item.getId());
        }
        assertThat(ids.contains("8876c244-6fc1-42b1-816b-7bcef2cfada3"), is(true));
    }
}
