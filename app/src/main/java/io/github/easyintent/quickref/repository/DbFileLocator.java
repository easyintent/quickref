package io.github.easyintent.quickref.repository;


import java.io.File;

public interface DbFileLocator {
    File findDbFile() throws RepositoryException;
}
