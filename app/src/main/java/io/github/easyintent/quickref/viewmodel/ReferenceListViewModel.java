package io.github.easyintent.quickref.viewmodel;

import android.app.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.github.easyintent.quickref.ExecutorProvider;
import io.github.easyintent.quickref.QuickRefApplication;
import io.github.easyintent.quickref.model.ReferenceItem;
import io.github.easyintent.quickref.model.ReferenceListData;
import io.github.easyintent.quickref.repository.ReferenceRepository;
import io.github.easyintent.quickref.repository.RepositoryException;
import io.github.easyintent.quickref.repository.RepositoryFactory;

public class ReferenceListViewModel extends AndroidViewModel {

    private static final Logger logger = LoggerFactory.getLogger(ReferenceListViewModel.class);

    private final ReferenceRepository repository;
    private final MutableLiveData<ReferenceListData> liveData;

    private final ExecutorProvider executorProvider;

    public ReferenceListViewModel(@NonNull Application application) {
        super(application);
        liveData = new MutableLiveData<>();
        executorProvider = (ExecutorProvider) application;
        RepositoryFactory factory = ((QuickRefApplication) application).getRepositoryFactory();
        repository = factory.createCategoryRepository();
    }

    public LiveData<ReferenceListData> getLiveData() {
        return liveData;
    }

    public void search(String query) {
        executorProvider.getBackgroundExecutor().execute(()->searchInBackground(query));
    }

    public void loadCategory(String parentId) {
        executorProvider.getBackgroundExecutor().execute(()->loadCategoryInBackground(parentId));
    }

    private void loadCategoryInBackground(String parentId) {
        try {
            List<ReferenceItem> list = repository.list(parentId);
            this.liveData.postValue(ReferenceListData.of(list));
        } catch (RepositoryException e) {
            logger.debug("Failed to get reference list", e);
            this.liveData.postValue(ReferenceListData.of(e.getMessage()));
        }
    }

    private void searchInBackground(String query) {
        try {
            List<ReferenceItem> list = repository.search(query);
            this.liveData.postValue(ReferenceListData.of(list));
        } catch (RepositoryException e) {
            logger.debug("Failed to search reference", e);
            this.liveData.postValue(ReferenceListData.of(e.getMessage()));
        }
    }
}
