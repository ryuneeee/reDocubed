package net.ion.redocubed;

import net.ion.craken.loaders.lucene.CentralCacheStoreConfig;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.crud.RepositoryImpl;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2013. 10. 29.
 * Time: 오후 4:16
 * To change this template use File | Settings | File Templates.
 */
class CrakenRepo implements Closeable {

    private final RepositoryImpl repo;

    public CrakenRepo(){
        this.repo = RepositoryImpl.create();
        repo.defineWorkspace("docubed", CentralCacheStoreConfig.createDefault());
        repo.defineWorkspace("execute", CentralCacheStoreConfig.create().location(""));
        repo.start();

    }

    public ReadSession docSession() throws IOException {
        return repo.login("docubed") ;
    }

    public ReadSession scriptSession() throws IOException {
        return repo.login("execute") ;
    }

    public void close(){
        repo.shutdown();
    }
}