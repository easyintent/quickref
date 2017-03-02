package io.github.easyintent.quickref.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

import io.github.easyintent.quickref.R;
import io.github.easyintent.quickref.data.ReferenceItem;
import io.github.easyintent.quickref.config.BookmarkConfig;
import io.github.easyintent.quickref.repository.ReferenceRepository;
import io.github.easyintent.quickref.repository.RepositoryException;
import io.github.easyintent.quickref.repository.RepositoryFactory;

@EFragment
public class BookmarkListFragment extends ListFragment {

    private static final Logger logger  = LoggerFactory.getLogger(BookmarkListFragment.class);

    private List<ReferenceItem> list;

    public static BookmarkListFragment newInstance() {
        Bundle args = new Bundle();
        BookmarkListFragment fragment = new BookmarkListFragmentEx();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText(getString(R.string.msg_empty_ref));
        if (list == null) {
            loadList(getActivity());
        } else {
            show(list);
        }
    }

    @Background
    protected void loadList(Context context) {
        BookmarkConfig bookmark = new BookmarkConfig(context);
        List<String> ids = bookmark.list();
        RepositoryFactory factory = RepositoryFactory.newInstance(context);
        ReferenceRepository repo = factory.createCategoryRepository();
        try {
            List<ReferenceItem> newData = repo.listByIds(ids);
            onLoadDone(true, newData, null);
        } catch (RepositoryException e) {
            logger.debug("Failed to get list", e);
            onLoadDone(false, Collections.<ReferenceItem>emptyList(), e.getMessage());
        }

    }

    @UiThread
    protected void onLoadDone(boolean success, List<ReferenceItem> newList, String message) {
        list = newList;
        if (!isAdded()) {
            return;
        }

        show(list);

        if (!success) {
            Dialog.info(getFragmentManager(), "bookmark_error", message);
        }
    }

    protected void show(List<ReferenceItem> list) {
        ArrayAdapter<ReferenceItem> adapter = new ReferenceAdapter(getActivity(), list);
        setListAdapter(adapter);
    }

}
