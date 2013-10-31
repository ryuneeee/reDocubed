package net.ion.redocubed;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.annotation.AnContext;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.annotation.AnResponse;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.InnerResponse;

import org.apache.commons.io.FilenameUtils;
import org.restlet.representation.InputRepresentation;
import org.restlet.resource.Get;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2013. 10. 29.
 * Time: �､弡�1:45
 * To change this template use File | Settings | File Templates.
 */
public class GetFileLet implements IServiceLet {



    @Get
    public InputRepresentation getFile(@AnContext TreeContext context, @AnRequest InnerRequest request, @AnResponse InnerResponse response) throws IOException {
        if(request.getPathReference().getPath().substring(1,12).equals("attachments")){

            ReadSession session = context.getAttributeObject(CrakenRepo.class.getCanonicalName(), CrakenRepo.class).docSession();
            ReadNode fileNode = session.pathBy("/attachments").child(request.getRemainPath());
            InputStream is = fileNode.property("blob").asBlob().toInputStream();
            response.setHeader("Content-Type", fileNode.property("contentType").stringValue());
            response.setHeader("Content-disposition", "attachment; filename=" + fileNode.property("filename").stringValue());


            return new InputRepresentation(is);
        }
        FileInputStream fis = new FileInputStream("./static/"+request.getRemainPath());
        String fileExtension = FilenameUtils.getExtension(request.getRemainPath());
        return new InputRepresentation(fis, request.getPathService().getAradon().getMetadataService().getMediaType(fileExtension));
    }
}
