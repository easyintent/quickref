package io.github.easyintent.quickref.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import io.github.easyintent.quickref.model.ReferenceItem;

public interface ReferenceRepository {

    /** List references for given parentId.
     *
     * @param parentId
     *      The parent id. Null for top level reference list.
     * @return
     * @throws RepositoryException
     */
    @NonNull
    List<ReferenceItem> list(@Nullable String parentId) throws RepositoryException;

    /** Get item by item ids.
     *
     * @param ids
     * @return
     * @throws RepositoryException
     */
    @NonNull
    List<ReferenceItem> listByIds(@NonNull List<String> ids) throws RepositoryException;


    /** Search reference by text query.
     *
     * @param query
     * @return
     * @throws RepositoryException
     */
    @NonNull
    List<ReferenceItem> search(@NonNull String query) throws RepositoryException;

}
