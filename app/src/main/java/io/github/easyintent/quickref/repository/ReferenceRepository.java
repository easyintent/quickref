package io.github.easyintent.quickref.repository;

import android.support.annotation.Nullable;

import java.util.List;

import io.github.easyintent.quickref.data.ReferenceItem;

public interface ReferenceRepository {

    /** Get reference based on category
     *
     * @param category
     *      The category. If null, it will list main category.
     * @return
     * @throws RepositoryException
     */
    List<ReferenceItem> list(@Nullable  String category) throws RepositoryException;

}
