package net.ion.redocubed;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.framework.mte.Engine;
import net.ion.framework.util.IOUtil;
import net.ion.framework.util.MapUtil;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.annotation.AnContext;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.let.InnerRequest;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2013. 10. 29.
 * Time: 오후 5:45
 * To change this template use File | Settings | File Templates.
 */
public class EditPageLet implements IServiceLet {

    @Get
    public StringRepresentation editPage(@AnContext TreeContext context, @AnRequest InnerRequest request) throws IOException {

        ReadSession session = context.getAttributeObject(CrakenRepo.class.getCanonicalName(), CrakenRepo.class).docSession();
        Engine engine = context.getAttributeObject("engine", Engine.class);


        URL url = new URL(request.getHeader("Referer"));
        String path = URLDecoder.decode(url.getPath().substring(1), "UTF-8");
        ReadNode editNode = session.pathBy(path);

        FileInputStream fis = new FileInputStream("./template/editPage.html");
        String template = IOUtil.toStringWithClose(fis, "UTF-8");

        Map map = MapUtil.chainMap().put("navList", MakeNav.create(session)).put("path", path).put("editNode", editNode).toMap();

        String newPage = engine.transform(template, map);
        return new StringRepresentation(newPage, MediaType.TEXT_HTML, Language.valueOf("UTF-8"));

    }
}
