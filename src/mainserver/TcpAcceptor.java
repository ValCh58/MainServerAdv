package mainserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.spi.SelectorProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.mina.core.service.IoProcessor;
import org.apache.mina.core.service.SimpleIoProcessorPool;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.transport.socket.nio.NioProcessor;
import org.apache.mina.transport.socket.nio.NioSession;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import util.ExecutorUtils;

/**
 * Tcp Абстрактный родительский класс адаптера доступа
 *
 */
abstract public class TcpAcceptor {

    public final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(TcpAcceptor.class);
    protected NioSocketAcceptor acceptor;
    protected RecvPacketRuleCfg rule;
    protected TcpAcceptorHandler handler;
    protected int coreSize = Runtime.getRuntime().availableProcessors() + 1;
    /**
     * Доступ к рабочему пулу 
     */
    protected ExecutorService acceptorExecutor;
    /**
     * IO Пул потоков обработки ввода-вывода
     */
    protected ExecutorService ioExecutor;
    /**
     * Пул потоков бизнес-обработки
     */
    protected ExecutorService businessExecutor;

    public TcpAcceptor(RecvPacketRuleCfg rule, TcpAcceptorHandler handler) {
        this.handler = handler;
        this.rule = rule;
    }

    protected void buildExecutors() {
        // Создаеm трех исполнителей потока здесь вместо использования по умолчанию, 
        //что удобно для регистрации информации доступа к инъекции MDC (Mapped Diagnostic Context)
        //https://www.codeflow.site/ru/article/mdc-in-log4j-2-logback для справки по MDC!
        acceptorExecutor = Executors.newCachedThreadPool();
        ioExecutor = Executors.newCachedThreadPool();
        businessExecutor = Executors.newCachedThreadPool();
    }

    protected void start() throws Exception {
        buildExecutors();
        /**
         * IoProcessor - внутренний интерфейс для представления «процессора
         * ввода-вывода», который выполняет фактические операции ввода-вывода
         * для IoSessions. Он еще раз абстрагирует существующие реакторные
         * структуры, такие как Java NIO, для упрощения реализации транспорта.
         * IoProcessor pool , который распределяет IoSessions в одну или
         * несколько IoProcessorS
         */
        IoProcessor<NioSession> processor = new SimpleIoProcessorPool<>(NioProcessor.class, ioExecutor, coreSize,  SelectorProvider.provider());
        //System.out.println("coreSize: " + coreSize);
        //NioSocketAcceptor, является реализацией IoAcceptor для TCP/IP//
        //acceptorExecutor для обработки событий подключения и данными//
        //IoProcessor processor для обработки событий ввода-вывода//
        acceptor = new NioSocketAcceptor(acceptorExecutor, processor);
        //Период бездействия сессии = 7 сек. после чего сессия удалается.//
        acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE, 7);
        //Очередь поступающих запросов на соединение//
        acceptor.setBacklog(100);
//	  acceptor.setBacklog(cfg.getBacklog());
        buildFilterChain();
        acceptor.setHandler(handler);//установка обработчика событий на acceptor//
        try {
            List<SocketAddress> address = new ArrayList<SocketAddress>();
            //Несколько SocketAddress могут быть добавлены
            //RecvPacketRuleCfg rule Установка типа принимаемого пакета 
            address.add(new InetSocketAddress((String) rule.get("ip"), (Integer) rule.get("port")));
            acceptor.bind(address);//Socket готов слушать//
        } catch (IOException e) {
            logger.error(e.getMessage());
            stop();
            throw e;
        }

    }

    protected void stop() {
        // Останавливает  порт для приема запросов на подключение FIXME 
        //Параметр dispose (boolean) недопустим, поскольку исполнитель извне.
        // Проверено, что при вызове acceptor.dispose () внутрн вызывается selector.wakeup () 
        //нарушит select () и порт не будет связан с этим
        // Поэтому исходный необработанный запрос не может быть отправлен из-за сетевых причин, 
        //когда обработка завершена, поэтому вам нужно остановить исполнителя перед удалением
        // Остановить прием запросов на выполнение процессов и закрыть пул потоков выполнения 
        //после выполнения всех запущенных в данный момент процессов
        ExecutorUtils.shutdownAndAwaitTermination(businessExecutor);
        ExecutorUtils.shutdownAndAwaitTermination(ioExecutor);
        acceptor.dispose();
        ExecutorUtils.shutdownNowAndAwaitTermination(acceptorExecutor);

    }

    /**
     * Построить цепочку фильтров обработки запросов ввода-вывода
     */
    abstract protected void buildFilterChain();

}
