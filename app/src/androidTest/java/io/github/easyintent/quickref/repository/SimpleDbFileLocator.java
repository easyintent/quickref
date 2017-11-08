package io.github.easyintent.quickref.repository;


import java.io.File;

class SimpleDbFileLocator implements DbFileLocator {

    private File target;

    public SimpleDbFileLocator(File target) {
        this.target = target;
    }

    @Override
    public File findDbFile() throws RepositoryException {
        return target;
    }

}
