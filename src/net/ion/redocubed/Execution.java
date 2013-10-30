package net.ion.redocubed;

import net.ion.nradon.let.IServiceLet;
import net.ion.radon.core.TreeContext;
import net.ion.radon.core.annotation.AnContext;
import net.ion.radon.core.annotation.AnRequest;
import net.ion.radon.core.annotation.AnResponse;
import net.ion.radon.core.let.InnerRequest;
import net.ion.radon.core.let.InnerResponse;
import net.ion.scriptexecutor.handler.ResponseHandler;
import net.ion.scriptexecutor.manager.ScriptManager;
import net.ion.scriptexecutor.script.ScriptResponse;
import org.restlet.data.Language;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Post;

import java.io.IOException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Ryun
 * Date: 2013. 10. 29.
 * Time: 오후 6:55
 * To change this template use File | Settings | File Templates.
 */
public class Execution implements IServiceLet {

    @Post
    public static StringRepresentation getExecuteScript(@AnContext TreeContext context, @AnRequest InnerRequest request, @AnResponse InnerResponse response2) throws IOException {

        ScriptManager manager = context.getAttributeObject(ScriptManager.class.getCanonicalName(), ScriptManager.class);
        String script = request.getEntityAsText();

        System.out.println("Before: " + script);

        script = script.replace("\\", "\\\\");
        script = script.replace("\"", "\\\"");
        script = script.replace(System.getProperty("line.separator"), "\\n");

        System.out.println("After: " + script);



        ScriptResponse scriptResponse = manager.createRhinoScript("<" + new Date().toString() + ">")
                .defineScript("var compiled = _.template(\"" + script + "\"); compiled({session: session});")
                .bind("session", context.getAttributeObject(CrakenRepo.class.getCanonicalName(), CrakenRepo.class).scriptSession())
//                .bind("response", response())
                .execute(new ResponseHandler<ScriptResponse>() {
                    @Override
                    public ScriptResponse success(ScriptResponse scriptResponse) {
                        return scriptResponse;
                    }

                    @Override
                    public ScriptResponse failure(ScriptResponse scriptResponse, Exception e) {
                        return scriptResponse;
                    }
                });

//
//
//        if (response.isSuccess() && !(response.getObject(Object.class) instanceof Undefined)){
//            if(response.getObject(Object.class) instanceof NativeJavaObject){
//                if(response.getObject(NativeJavaObject.class).unwrap() instanceof byte[])
//                    return ok((byte[])response.getObject(NativeJavaObject.class).unwrap());
//            }else{
//                return ok((String) response.getObject(Object.class));
//            }
//        }
        return new StringRepresentation((String) scriptResponse.getObject(Object.class), Language.valueOf("UTF-8"));

    }
}
