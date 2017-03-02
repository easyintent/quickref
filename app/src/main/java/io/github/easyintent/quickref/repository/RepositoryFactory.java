package io.github.easyintent.quickref.repository;


import android.content.Context;
import android.support.annotation.NonNull;

public abstract class RepositoryFactory {
    public static RepositoryFactory newInstance(Context context) {
        return new SqliteRepositoryFactory(context);
    }

    @NonNull
    public abstract ReferenceRepository createCategoryRepository();
}

class SqliteRepositoryFactory extends RepositoryFactory {

    private Context context;

    public SqliteRepositoryFactory(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ReferenceRepository createCategoryRepository() {
        return new SqliteReferenceRepository(context);
    }
}
