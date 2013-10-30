package net.ion.redocubed;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2013. 10. 29.
 * Time: 오후 5:26
 * To change this template use File | Settings | File Templates.
 */
public class MakeNav {

    public static String create(ReadSession session) throws IOException {
        StringWriter writer = new StringWriter() ;
        getNavList(session.pathBy("/craken"), writer);
        return writer.toString();
    }

    private static void getNavList(ReadNode node, Writer writer) throws IOException {
        for(ReadNode child : node.children().ascending("title")){
            writer.write("<li>");
            writer.write("<a href=\""+child.fqn().toString()+"\">"+child.property("title").stringValue());
            writer.write("</a>");
            if(child.children().hasNext()){
                writer.write("<ul>");
                getNavList(child, writer);
                writer.write("</ul>");
            }
            writer.write("</li>");
        }
    }
}
