package net.ion.redocubed;

import net.ion.nradon.let.IServiceLet;
import org.junit.Before;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2013. 10. 29.
 * Time: 오전 10:55
 * To change this template use File | Settings | File Templates.
 */
public class Page implements IServiceLet{

    @Get
    public StringRepresentation requestGet(){



        return new StringRepresentation("");
    }


}