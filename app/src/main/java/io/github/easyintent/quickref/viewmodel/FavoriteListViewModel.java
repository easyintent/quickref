package io.github.easyintent.quickref.viewmodel;

import android.app.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import io.github.easyintent.quickref.ExecutorProvider;
import io.github.easyintent.quickref.QuickRefApplication;
import io.github.easyintent.quickref.config.FavoriteConfig;
import io.github.easyintent.quickref.model.ReferenceItem;
import io.github.easyintent.quickref.repository.ReferenceRepository;
import io.github.easyintent.quickref.repository.RepositoryException;
import io.github.easyintent.quickref.repository.RepositoryFactory;

public class FavoriteListViewModel extends AndroidViewModel  {

    private static final Logger logger = LoggerFactory.getLogger(FavoriteListViewModel.class);

    private final ReferenceRepository repository;
    private final FavoriteConfig favoriteConfig;
    private final MutableLiveData<List<ReferenceItem>> listLiveData;
    private final ExecutorProvider executorProvider;

    public FavoriteListViewModel(@NonNull Application application) {
        super(application);

        RepositoryFactory factory = ((QuickRefApplication) application).getRepositoryFactory();
        repository = factory.createCategoryRepository();
        favoriteConfig = new FavoriteConfig(application);
        listLiveData = new MutableLiveData<>();

        executorProvider = (ExecutorProvider) application;
    }

    public MutableLiveData<List<ReferenceItem>> getListLiveData() {
        return listLiveData;
    }

    public void refresh() {
        executorProvider.getBackgroundExecutor().execute(this::loadList);
    }

    public void delete(List<String> favorites) {
        favoriteConfig.delete(favorites);
        refresh();
    }

    private void loadList() {
        List<String> ids = favoriteConfig.list();
        try {
            List<ReferenceItem> newData = repository.listByIds(ids);
            listLiveData.postValue(newData);
        } catch (RepositoryException e) {
            logger.debug("Failed to get list", e);
        }
    }
}
