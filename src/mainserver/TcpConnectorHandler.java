package mainserver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;


public class TcpConnectorHandler extends IoHandlerAdapter {
	
	private final static Logger logger = Logger.getLogger(TcpConnectorHandler.class);
	
	/**Назначение обработчиков - обработка многопоточного параллелизма с использованием обработчика, 
        но для каждого соединения есть ReadFuture*/
	protected Map<IoSession, ReadFuture> handlers = //Collections.synchronizedMap(new HashMap<IoSession, ReadFuture>());
	                         			new ConcurrentHashMap<IoSession, ReadFuture>();

	
	public TcpConnectorHandler() {
	}
	
	public void sessionCreated(IoSession session) throws Exception {
		logger.debug("TcpConnector sessionCreated:" + session.getId());
	}

    public void sessionOpened(IoSession session) {
        logger.debug("TcpConnector sessionOpened:" + session.getId());
    }

    // После того, как исключение поймано, текущий будущий поток автоматически выполняется.
    public void exceptionCaught(IoSession session, Throwable t){
    	logger.error("TcpConnector exceptionCaught:" + session.getId(), t);
    	ReadFuture future = getReadFuture(session);
    	if (future != null)
    		future.setException(new Exception(t));
        session.closeNow();
    }

    public void sessionClosed(IoSession session) {
        logger.info("TcpConnector sessionClosed:" + session.getId() +" total " + session.getReadBytes() + " byte(s)");
        ReadFuture future = null;
    	// Возможно, удаленный хост закрыл соединение, но клиент сокета не получил данные, 
        //поэтому здесь нужно сгенерировать исключение
    	future = getReadFuture(session);
    	if (future != null)
    		future.setException(new Exception("Удаленный сервер TCP закрыл соединение"));
    }

    public void sessionIdle(IoSession session, IdleStatus status) {
		logger.debug("TcpConnector sessionIdle:" + session.getId() +" the status is " + status + ". ");
    }

    public void messageSent(IoSession session, Object message) throws Exception {
//    	if (logger.isInfoEnabled())
//			logger.info("TcpConnector SENT:\r\n" + CommHelper.messageToString(cfg, message));
    	logger.debug("TcpConnector SENT:" + session.getId());
    }

    public void messageReceived(IoSession session, Object message) {
    	// Журналы помещаются в TcpConnector, чтобы избежать того, 
        //к какому потоку доступа относится журнал, так как соединитель использует пул потоков.
    //    	if (logger.isInfoEnabled())
    //			logger.info("TcpConnector RECEIVED:\r\n" + CommHelper.messageToString(cfg, message));
    	logger.debug("TcpConnector RECEIVED:" + session.getId());
    	ReadFuture future = getReadFuture(session);
    	if (future != null)
    		future.setRead(message);
    }
    
    protected ReadFuture getReadFuture(IoSession s) {
    	return handlers.get(s);
    }
    
    public void addReadFuture(IoSession s, ReadFuture f) {
    	handlers.put(s, f);
    }
    
    public void removeReadFuture(IoSession s) {
    	handlers.remove(s);
    }
    
}
