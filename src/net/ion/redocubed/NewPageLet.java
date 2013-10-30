package net.ion.redocubed;

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
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2013. 10. 29.
 * Time: 오후 5:10
 * To change this template use File | Settings | File Templates.
 */
public class NewPageLet implements IServiceLet {

    @Get
    public StringRepresentation newPageGet(@AnContext TreeContext context, @AnRequest InnerRequest request) throws IOException {

        ReadSession session = context.getAttributeObject(CrakenRepo.class.getCanonicalName(), CrakenRepo.class).docSession();
        Engine engine = context.getAttributeObject("engine", Engine.class);

        Debug.line(request.getHeaders());
        URL url = new URL(request.getHeader("Referer"));
        String path = URLDecoder.decode(url.getPath().substring(1), "UTF-8");

        FileInputStream fis = new FileInputStream("./template/newPage.html");
        String template = IOUtil.toStringWithClose(fis, "UTF-8");

        Map map = MapUtil.chainMap().put("navList", MakeNav.create(session)).put("path", path).toMap();

        String newPage = engine.transform(template, map);
        return new StringRepresentation(newPage, MediaType.TEXT_HTML, Language.valueOf("UTF-8"));

    }

    @Post
    public StringRepresentation newPagePost(@AnContext TreeContext context, @AnRequest InnerRequest request, @AnResponse InnerResponse response) throws Exception {

        ReadSession session = context.getAttributeObject(CrakenRepo.class.getCanonicalName(), CrakenRepo.class).docSession();

        final String path = request.getFormParameter().getFirstValue("path").toString();
        final String title = request.getFormParameter().getFirstValue("title").toString();
        final String content = request.getFormParameter().getFirstValue("content").toString();
        final String uri = createURI(title);

        session.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession writeSession) throws Exception {
                writeSession.pathBy(path).addChild(uri).property("title", title).property("content", content);
                return null;
            }
        });

        response.redirectPermanent("/"+path+"/"+uri);

        return null;

    }

    public static String createURI(String string){
        return string.replaceAll("\\p{Punct}", "").replaceAll(" ", "_").toLowerCase();
    }

}
