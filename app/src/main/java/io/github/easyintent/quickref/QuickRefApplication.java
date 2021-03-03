package io.github.easyintent.quickref;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.easyintent.quickref.repository.RepositoryFactory;

public class QuickRefApplication extends android.app.Application implements ExecutorProvider {

    private ExecutorService executorService = Executors.newFixedThreadPool(4);
    private Handler handler = new Handler(Looper.getMainLooper());
    private RepositoryFactory repositoryFactory;

    @Override
    public void onCreate() {
        super.onCreate();
        repositoryFactory = RepositoryFactory.newInstance(this);
    }

    @Override
    public ExecutorService getBackgroundExecutor() {
        return executorService;
    }

    @Override
    public Handler getUiExecutor() {
        return handler;
    }

    public RepositoryFactory getRepositoryFactory() {
        return repositoryFactory;
    }
}
