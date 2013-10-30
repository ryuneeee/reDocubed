package net.ion.redocubed;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.mte.Engine;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.MapUtil;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.annotation.AnContext;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.annotation.AnResponse;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.InnerResponse;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2013. 10. 29.
 * Time: 오전 11:07
 * To change this template use File | Settings | File Templates.
 */
public class PageLet implements IServiceLet{


    @Get
    public StringRepresentation render(@AnContext TreeContext context, @AnRequest InnerRequest request) throws IOException {

        ReadSession session = context.getAttributeObject(CrakenRepo.class.getCanonicalName(), CrakenRepo.class).docSession();

        Engine engine = context.getAttributeObject("engine", Engine.class);

        ReadNode node = session.pathBy("/craken/" + request.getRemainPath());
        Debug.line(node);

        FileInputStream fis = new FileInputStream("./template/main.html");
        String template = IOUtil.toStringWithClose(fis, "UTF-8");

        Map map = MapUtil.chainMap().put("navList", MakeNav.create(session)).put("node", node).toMap();

        String main = engine.transform(template, map);
        return new StringRepresentation(main, MediaType.TEXT_HTML ,Language.valueOf("UTF-8"));
    }

    @Post
    public StringRepresentation edit(@AnContext TreeContext context, @AnRequest InnerRequest request, @AnResponse InnerResponse response) throws Exception {

        ReadSession session = context.getAttributeObject(CrakenRepo.class.getCanonicalName(), CrakenRepo.class).docSession();

        final String path = request.getFormParameter().getFirstValue("path").toString();
        final String title = request.getFormParameter().getFirstValue("title").toString();
        final String content = request.getFormParameter().getFirstValue("content").toString();

        session.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession writeSession) throws Exception {

                writeSession.pathBy(path).property("title", title).property("content", content);
                return null;
            }
        });

        response.redirectPermanent("/" + path);

        return null;

    }

    @Delete
    public StringRepresentation delete(@AnContext TreeContext context, @AnRequest final InnerRequest request) throws IOException {

        ReadSession session = context.getAttributeObject(CrakenRepo.class.getCanonicalName(), CrakenRepo.class).docSession();

        try {
            session.tranSync(new TransactionJob<Object>() {
                @Override
                public Object handle(WriteSession writeSession) throws Exception {
                    writeSession.pathBy("/craken/"+request.getRemainPath()).removeSelf();
                    return null;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new StringRepresentation("/craken/", MediaType.TEXT_HTML, Language.valueOf("UTF-8"));
    }

    public static String createURI(String string){
        return string.replaceAll("\\p{Punct}", "").replaceAll(" ", "_").toLowerCase();
    }


}
