package shorttcp;

import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import mainserver.TcpAcceptorHandler;

public class ShortAcceptorHandler extends TcpAcceptorHandler {

    public ShortAcceptorHandler() {
    }

    /**
     *
     * @param session
     * @param status
     */
    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {
        super.sessionIdle(session, status);
        // Закрыть соединение, когда соединение не используется
        session.closeNow();
    }

    /**
     *
     * @param session
     * @param t
     * @throws Exception
     */
    @Override
    public void exceptionCaught(IoSession session, Throwable t) throws Exception {
        super.exceptionCaught(session, t);
        // Закрыть соединение независимо от того, какое исключение получеno
        session.closeNow();
    }

    /**
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
        // Закрыть соединение после отправки данных!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //session.closeNow();
    }
  
}
