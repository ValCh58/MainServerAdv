package mainserver;

import java.net.InetSocketAddress;
import java.util.Hashtable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.DefaultReadFuture;
import org.apache.mina.core.future.ReadFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.service.SimpleIoProcessorPool;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioProcessor;
import org.apache.mina.transport.socket.nio.NioSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import protocol.ProtocolCodecFactoryFactory;
import shorttcp.ShortConnectorHandler;
import util.ExecutorUtils;


/**
 * Tcp разъем родительского класса
 * 
 */
abstract public class TcpConnector  {
	
	private final static Logger logger = Logger.getLogger(TcpConnector.class);

	protected NioSocketConnector connector;
	protected TcpConnectorHandler handler;
	protected int coreSize = Runtime.getRuntime().availableProcessors() + 1;
	/**Рабочий pool*/
	protected ExecutorService connectorExecutor;
	/**Пул потоков обработки ввода-вывода*/
	protected ExecutorService ioExecutor;
	protected RecvPacketRuleCfg rule;
	
	public TcpConnector(RecvPacketRuleCfg rule, TcpConnectorHandler handler) {
		this.rule = rule;
		this.handler = handler;
	}
	
	protected void buildExecutors() {
		// Создайте трех исполнителей потока здесь вместо использования по умолчанию, 
                //что удобно для регистрации информации доступа к инъекции MDC
		connectorExecutor = new MyThreadPoolExecutor(1);
		ioExecutor = new MyThreadPoolExecutor(coreSize);
	}
	
	
	protected byte[] doExecute(IoSession session, byte[] message, Object...args) throws Exception {
		DefaultReadFuture readFuture = new DefaultReadFuture(session);
		handler.addReadFuture(session, readFuture);
		// Отправить запрос 
		sendRequest(session, message);
		// Читать ответный пакет
//		ReadFuture readFuture = session.read();
		int timeout = args.length > 0 ? (Integer)args[0] : (Integer)rule.get("timeout")!=null?(Integer)rule.get("timeout"):10000;
		return recvResponse(readFuture, timeout);
	}
	
	protected void sendRequest(IoSession session, byte[] message) {
		if (logger.isInfoEnabled())
			logger.info("TcpConnector SEND:\r\n" + new String(message));
		WriteFuture future = session.write(message);
//		future.awaitUninterruptibly(/*10*1000L*/);
//		if (!future.isWritten())
//			throw new CommunicateException("Не удалось записать данные на удаленный TCP-сервер");
	}

	protected byte[] recvResponse(ReadFuture readFuture, int timeout) throws Exception  {
		// Время ожидания чтения истекло
		boolean b = readFuture.awaitUninterruptibly(timeout);
		if (!b)
			throw new Exception("Время ожидания получения данных от удаленного TCP-сервера");
		if (!readFuture.isRead()) {
			if (readFuture.getException() != null)
			     throw new Exception("Получено исключение данных удаленного TCP-сервера", readFuture.getException());
			throw new Exception("Получено исключение данных удаленного TCP-сервера");
		}
		
		Object ret = readFuture.getMessage();
		if (logger.isInfoEnabled())
			logger.info("TcpConnector RECEIVED:\r\n" + new String((byte[])ret));
		return (byte[]) ret;
	}
	
	public IoSession connect() throws Exception {
		ConnectFuture future = connector.connect(new InetSocketAddress((String)rule.get("ip"), (Integer)rule.get("port")));
		future.awaitUninterruptibly();
		/*boolean b = future.awaitUninterruptibly(cfg.getConTimeout());
		//Если возвращаемое значение b == true, это означает, что значение ConnectFuture установлено, 
                //то есть вызывается метод setException, setSession или cancel для ConnectFuture.
		if (!b)
			throw new CommunicateException("Тайм-аут подключения к удаленному TCP-серверу");*/
		
		if (!future.isConnected())
			//Это getValue () instanceof IoSession == false, то есть исключение или Cancered
			throw new Exception("Не удалось установить соединение с удаленным TCP-сервером: "
                                            + "тайм-аут или исключение", future.getException());
		return future.getSession();
	}
	
	protected void close(IoSession session){
		session.closeNow();
	}
	
	public void start() throws Exception {
		buildExecutors();
		IoProcessor<NioSession> processor = new SimpleIoProcessorPool<NioSession>(
				NioProcessor.class, /*ioExecutor,*/ coreSize);
		connector = new NioSocketConnector(connectorExecutor, processor);
		connector.setConnectTimeoutMillis((Integer) rule.get("timeout")); // Установите время ожидания соединения. 
                //Cмотреть AbstractPollingIoConnector.processTimedOutSessions() С классом ConnectionRequest
		// connector.getSessionConfig().setUseReadOperation(true); //
		// Вы также можете использовать этот метод для синхронной отправки и получения данных, 
                //поэтому вам не нужно настраивать обработчик и получать его через session.read ()
		handler = new ShortConnectorHandler();
		connector.setHandler(handler);
		DefaultIoFilterChainBuilder filterChain = connector.getFilterChain();
		filterChain.addLast("codec", new ProtocolCodecFilter(
				ProtocolCodecFactoryFactory.getInstance(rule)));
	}

	public void stop() throws Exception {
		ExecutorUtils.shutdownAndAwaitTermination(ioExecutor);
		connector.dispose();
		ExecutorUtils.shutdownAndAwaitTermination(connectorExecutor);
	}
	
	abstract protected String getConnectorName();
	
	class MyThreadPoolExecutor extends ThreadPoolExecutor {
		public MyThreadPoolExecutor(int corePoolSize) {
			super(corePoolSize, Integer.MAX_VALUE, 60, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
		}
		protected void beforeExecute(Thread t, Runnable r) {
			String GROUP_KEY = "GROUP_KEY";
			Object group = MDC.get(GROUP_KEY);
			// Привязать имя группы к текущему контексту потока log4j
			Hashtable<?, ?> ht = MDC.getContext();
			if (ht != null)
				ht.clear();
			if (group != null)
				MDC.put(GROUP_KEY, group);
		}
	}

}
