package net.ion.redocubed;

import net.ion.craken.loaders.lucene.CentralCacheStoreConfig;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.node.crud.RepositoryImpl;
import net.ion.framework.mte.Engine;
import net.ion.framework.util.Debug;
import net.ion.framework.util.InfinityThread;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.EnumClass;
import net.ion.radon.util.AradonTester;
import net.ion.scriptexecutor.manager.ManagerBuilder;
import net.ion.scriptexecutor.manager.ScriptManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2013. 10. 29.
 * Time: 오전 11:14
 * To change this template use File | Settings | File Templates.
 */
public class BasicTest {

    public static void main(String[] args) throws Exception {


        ScriptManager manager = ManagerBuilder.createBuilder().languages(ManagerBuilder.LANG.JAVASCRIPT).build();

        try {
            FileReader fr = new FileReader("./resource/underscore-min.js");
            manager.createRhinoScript("underscore").defineScript(fr).setPreScript();
        } catch (IOException e) {
            e.printStackTrace();
        }

        manager.start();

        Engine engine = Engine.createDefaultEngine();
        Aradon aradon = AradonTester.create()
                .putAttribute("engine", engine)
                .putAttribute(ScriptManager.class.getCanonicalName(), manager)
                .putAttribute(CrakenRepo.class.getCanonicalName(), new CrakenRepo())
                .register("", "/static/", EnumClass.IMatchMode.STARTWITH, GetFileLet.class)
                .register("", "/execute", Execution.class)
                .register("craken", "/newPage", EnumClass.IMatchMode.STARTWITH, NewPageLet.class)
                .register("craken", "/editPage", EnumClass.IMatchMode.STARTWITH, EditPageLet.class)
                .register("craken", "/", EnumClass.IMatchMode.STARTWITH, PageLet.class).getAradon();
        aradon.startServer(9000);

        new InfinityThread().startNJoin();
    }

}