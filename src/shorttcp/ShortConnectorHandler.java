package shorttcp;

import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

import mainserver.TcpConnectorHandler;


/**
 * Этот класс в основном отвечает за чтение данных для возврата коннектора
 */
public class ShortConnectorHandler extends TcpConnectorHandler {
	
	public ShortConnectorHandler() {
	}

        @Override
	public void sessionIdle(IoSession session, IdleStatus status) {
		super.sessionIdle(session, status);
    	ReadFuture future = getReadFuture(session);
		if (future != null)
	        future.setException(new Exception("Подключение к удаленному серверу SOCKET не используется: " + status));
        session.closeNow();
    }
	
        @Override
	 public void messageSent(IoSession session, Object message) throws Exception {
    	super.messageSent(session, message);
    }
	
        @Override
	public void messageReceived(IoSession session, Object message) {
    	super.messageReceived(session, message);
    	session.closeNow();
    }
}
