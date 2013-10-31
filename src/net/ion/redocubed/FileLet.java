package net.ion.redocubed;

import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.framework.util.Debug;
import net.ion.framework.util.IOUtil;
import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.annotation.AnContext;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.annotation.AnResponse;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.InnerResponse;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.entity.mime.MultipartEntity;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

import java.io.*;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2013. 10. 29.
 * Time: 오후 1:45
 * To change this template use File | Settings | File Templates.
 */
public class FileLet implements IServiceLet {



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

    @Post
    public StringRepresentation writeFile(@AnContext TreeContext context, @AnRequest InnerRequest request) throws Exception {

        ReadSession session = context.getAttributeObject(CrakenRepo.class.getCanonicalName(), CrakenRepo.class).docSession();
        final DiskFileItem diskFileItem = (DiskFileItem) request.getFormParameter().get("attachment");


        final String fileHash = String.valueOf(diskFileItem.getName().hashCode());

        session.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession writeSession) throws Exception {
                writeSession.pathBy("/attachments").addChild(fileHash)
                        .property("filename", diskFileItem.getName())
                        .property("contentType", diskFileItem.getContentType())
                        .blob("blob", diskFileItem.getInputStream());
                return null;
            }
        });


        String response = "{\"contentType\":\""+ diskFileItem.getContentType()+"\","
          ㅋ      + "\"filename\":\""+ diskFileItem.getName()+ "\","
                + "\"filepath\":\""+ "/attachments/" + fileHash + "\"}";


        return new StringRepresentation(response, MediaType.APPLICATION_JSON, Language.valueOf("UTF-8"));
    }
}
