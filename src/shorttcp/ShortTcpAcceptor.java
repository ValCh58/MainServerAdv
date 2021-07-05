package shorttcp;

import mainserver.RecvPacketRuleCfg;
import mainserver.TcpAcceptor;
import mainserver.TcpAcceptorHandler;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.session.IoEventType;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import protocol.ProtocolCodecFactoryFactory;

/**
 * Tcp адаптер доступа к короткому соединению
 *
 */
public class ShortTcpAcceptor extends TcpAcceptor {

    public ShortTcpAcceptor(RecvPacketRuleCfg rule, TcpAcceptorHandler handler) {
        super(rule, handler);
    }

    @Override
    protected void buildFilterChain() {
        DefaultIoFilterChainBuilder filterChain = acceptor.getFilterChain();
        filterChain.addLast("codec", new ProtocolCodecFilter(ProtocolCodecFactoryFactory.getInstance(rule)));
        //IoEventType {SESSION_CREATED, SESSION_OPENED, SESSION_CLOSED, MESSAGE_RECEIVED, MESSAGE_SENT, 
        //SESSION_IDLE, EXCEPTION_CAUGHT, WRITE, CLOSE, INPUT_CLOSED, EVENT}
        IoEventType[] DEFAULT_EVENT_SET = new IoEventType[]{IoEventType.MESSAGE_RECEIVED};
        // businessPool используется для обработки события DEFAULT_EVENT_SET, здесь только получение сообщения
        //ExecutorFilter перенаправляет события ввода/вывода Executor для принудительного применения определенной модели потока, 
        //одновременно позволяя обрабатывать события для каждого сеанса
        filterChain.addLast("businessPool", new ExecutorFilter(businessExecutor, DEFAULT_EVENT_SET));
    }

    protected String getAcceptorName() {
        return "TCP short connection";
    }

}
