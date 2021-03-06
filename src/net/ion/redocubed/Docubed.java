package net.ion.redocubed;

import net.ion.framework.mte.Engine;
import net.ion.framework.util.InfinityThread;
import net.ion.radon.core.Aradon;
import net.ion.radon.core.EnumClass;
import net.ion.radon.util.AradonTester;
import net.ion.scriptexecutor.manager.ManagerBuilder;
import net.ion.scriptexecutor.manager.ScriptManager;

import java.io.FileReader;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2013. 10. 29.
 * Time: �､��11:14
 * To change this template use File | Settings | File Templates.
 */
public class Docubed {

    public static void main(String[] args) throws Exception {

        int port = 9000;

        if(args.length!=0)
            port = Integer.parseInt(args[0]);

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
                .register("", "/static/", EnumClass.IMatchMode.STARTWITH, FileLet.class)
                .register("", "/uploadFile", EnumClass.IMatchMode.STARTWITH, FileLet.class)
                .register("", "/execute", Execution.class)
                .register("craken", "/newPage", EnumClass.IMatchMode.STARTWITH, NewPageLet.class)
                .register("craken", "/editPage", EnumClass.IMatchMode.STARTWITH, EditPageLet.class)
                .register("craken", "/", EnumClass.IMatchMode.STARTWITH, PageLet.class).getAradon();
        aradon.startServer(port);

        new InfinityThread().startNJoin();
    }

}