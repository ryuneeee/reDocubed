package net.ion.redocubed;

import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.let.InnerRequest;
import org.apache.commons.io.FilenameUtils;
import org.restlet.representation.InputRepresentation;
import org.restlet.resource.Get;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2013. 10. 29.
 * Time: 오후 1:45
 * To change this template use File | Settings | File Templates.
 */
public class GetFileLet implements IServiceLet {



    @Get
    public InputRepresentation getFile(@AnRequest InnerRequest request) throws FileNotFoundException {
        FileInputStream fis = new FileInputStream("./static/"+request.getRemainPath());
        String fileExtension = FilenameUtils.getExtension(request.getRemainPath());
        return new InputRepresentation(fis, request.getPathService().getAradon().getMetadataService().getMediaType(fileExtension));
    }
}
