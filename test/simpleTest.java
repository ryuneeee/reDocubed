import net.ion.craken.node.ReadSession;
import net.ion.craken.node.TransactionJob;
import net.ion.craken.node.WriteSession;
import net.ion.craken.node.crud.RepositoryImpl;
import net.ion.framework.util.Debug;
import net.ion.redocubed.CrakenRepo;
import org.junit.Test;

import java.io.IOException;

/**
 * Author: Ryunhee Han
 * Date: 2013. 10. 31.
 */
public class simpleTest {

    @Test
    public void removeTest() throws Exception {

        CrakenRepo repo = new CrakenRepo();
        ReadSession session = repo.docSession();
        Debug.line(session.pathBy("/craken").child("http:"));

        session.tranSync(new TransactionJob<Object>() {
            @Override
            public Object handle(WriteSession writeSession) throws Exception {
                writeSession.pathBy("/craken/http:").removeSelf();
                return null;
            }
        });
    }
}
