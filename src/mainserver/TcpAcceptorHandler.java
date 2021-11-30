package mainserver;

import java.util.Optional;
import org.apache.log4j.Logger;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import processingdata.CounterDataProcessing;
import processingdata.ProcessAlarms;
import uspdsql.HikariConectionFactory;
import uspdsql.ThreadeInsertSQL;

/**
 *
 * @author chvaleriy
 */
public class TcpAcceptorHandler extends IoHandlerAdapter {

    public final static Logger logger = Logger.getLogger(TcpAcceptorHandler.class);

    public TcpAcceptorHandler() {
    }

    /**
     * Вызывается из потока процессора ввода-вывода при создании нового
     * соединения
     *
     * @param session
     * @throws Exception 
     */
    @Override
    public void sessionCreated(IoSession session) throws Exception {
        // Этот метод выполняется потоком NioProcessor
        //logger.debug("TcpAcceptor sessionCreated:" + session.getId());
        //logger.info("TcpAcceptor sessionCreated: " + session.getId());
        //logger.info(session.getService().toString());
    }

    /**
     *
     * @param session
     */
    @Override
    public void sessionOpened(IoSession session) {
        // ExecutorFilter Вызовет executor.execute (событие) для выполнения текущего оператора,
        //Поскольку пул потоков будет использоваться,    
        //поток выполнения не обязательно будет таким же, как exceptionCaught, sessionClosed и т. Д.
        //То же самое относится к messageSent, messageReceived и exceptionCaught.
        //logger.debug("TcpAcceptor sessionOpened: " + session.getId());
        //logger.info("TcpAcceptor sessionOpened: " + session.getId());
        //logger.info(session.getService().toString());
    }

    /**
     *
     * @param session
     * @param t
     * @throws Exception
     */
    @Override
    public void exceptionCaught(IoSession session, Throwable t) throws Exception {
        
        @SuppressWarnings("ReplaceStringBufferByString")
        StringBuffer sb = new StringBuffer("TcpAcceptor exceptionCaught: ").append(session.getId());
        logger.error(sb.toString(), t);
    }

    /**
     *
     * @param session
     */
    @Override
    public void sessionClosed(IoSession session) {
        //logger.info("SessionClosed: " + session.getId() + " total " + session.getReadBytes() + " bytes " + session.getService().toString());
        //logger.info(session.getService().toString());
    }

    /**
     * Вызывается при простое соединения, IdleStatus когда соединение становится свободным
     *
     * @param session
     * @param status
     */
    @Override
    public void sessionIdle(IoSession session, IdleStatus status) {
        @SuppressWarnings("ReplaceStringBufferByString")
        StringBuffer sb = new StringBuffer("TcpAcceptor sessionIdle: ").append(session.getId())
                                                                       .append(" status is ")
                                                                       .append(status)
                                                                       .append(". IP:PORT ")
                                                                       .append(session.getRemoteAddress());
        //logger.debug("TcpAcceptor sessionIdle:" + session.getId() + " status is " + status + ". ");
        logger.info(sb.toString());
        //logger.info( "IDLE " + session.getIdleCount( status ));
    }

    /**
     * Вызывается , когда сообщение записано методом IoSession.write(Object)
     * отправляется
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        // Журнал изменен на messageReceived, здесь только указывается, что информация была отправлена, 
        //чтобы избежать противоречивой информации журнала из-за многопоточности
        //logger.info("TcpAcceptor SENT: " + session.getId() + new String((byte[]) message));
        //logger.info(session.getService().toString());
    }

    /**
     * Обработчик вызывается при получении сообщения
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
     
        Optional<IoSession> opSession = Optional.ofNullable(session);
        if (opSession.isEmpty()) {
            session.closeNow();
            return;
        }
        
        Optional<Object> opMessage = Optional.ofNullable(message);
        if (opMessage.isEmpty()) {//Для клиента нет данных
            session.closeNow();
            return;
        }
        
        @SuppressWarnings("ReplaceStringBufferByString")
        StringBuffer stb = new StringBuffer().append("Session ID:").append(session.getId())
                                             .append(" message:").append(new String((byte[]) message));
        logger.info(stb.toString());
        
        @SuppressWarnings("UnusedAssignment")
        byte[] ret = null;
        try {
            //Выполнить бизнес-логику для возврата данных к клиенту 
            ret = doHandler((byte[]) message, session);
        } catch (Exception e) {
            logger.error(new StringBuffer("TcpAcceptor InvokeFlow Error: ").append(session.getId()).toString(), e);
            session.closeNow();
            return;
        }
        if (ret == null) {//Для клиента нет данных
            session.closeNow();
            return;
        }
        if (ret.length > 0) {
            // При обработке ответа, если соединение закрыто // 
            if (session.isClosing() || !session.isConnected()) {
                logger.error(new StringBuffer("Connection dropped, sending data is ignored: ")
                             .append(session.getId()).append(ret).toString());
                return;
            }
            logger.info(new StringBuffer("TcpAcceptor SEND:").append(session.getId()).append(" ").append(ret).toString());
            
            session.write(ret);//Отправим данные клиенту
        }
    }

    
    /**
     * Обработка принятого сообщения от клиента 
     * @param recv
     * @param session
     * @return 
     */
    protected byte[] doHandler(byte[] recv, IoSession session) {
        String data = (recv != null)?new String(recv):"";
        
        if(isResponseCheck(data)){//приход квитанции от успд?
            return null;//Квитанция. Закончим обработку данных.
        }
        if(data.length() < 3){//Пакет данных не наш.
           return null; //Закончим обработку данных.
        }
        
        data = data.substring(1, data.length() - 2);//Уберем служебные символы//
        @SuppressWarnings("UnusedAssignment")
        String answer = "";
        if(data.contains("pinggnip")){//Запрос проверки работы сервера связи 
            answer = "gnipping";      //Ответ сервера связи
        }
        else{
               answer = processingMeter(data, session);//Обработка данных от клиента//
        }
        return  answer.isEmpty()?null:answer.getBytes();//Если есть ответ для клиента пошлем его//
    }
    
    /**
     * Проверка прихода квитанции от УСПД
     * Исправлено 06-10-2020
     * @param isCheck
     * @return 
     */
    private boolean isResponseCheck(String isCheck){
         return (isCheck != null && isCheck.indexOf("!") == 0 && isCheck.length() < 5);
    }
    
    /**
     * Обработка данных от клиента
     * @param data
     * @param session
     * @return 
     */
    @SuppressWarnings("UnusedAssignment")
    protected String processingMeter(String data, IoSession session){
        
        boolean result = false;
        String writeSession = "";
        if(data == null || data.isEmpty()){
            return (data = "");
        }
        
        CounterDataProcessing cdp = new CounterDataProcessing(data);
        
        if(cdp.processingHeaterMeter()){//Обработка переданых показаний теплосчетчиков//
            result = true; 
        }
        
        if(cdp.processingElectricityMeter()){//Обработка переданых показаний эл. счетчиков//
            result = true;
        }
        if(cdp.processingWaterFlowMeter()){//Обработка переданых показаний водянных счетчиков//
            result = true;
        }
        //Запись данных в БД MySql//////////////////////////////////////////////////
        if(result){
           ThreadeInsertSQL tis = new ThreadeInsertSQL(HikariConectionFactory.getConnection(), cdp);
            tis.run();//Запись в БД данных.
            ProcessAlarms pa = new ProcessAlarms(cdp);
            writeSession = pa.retAlarm();//Обработка аварии.
            if(writeSession != null && !writeSession.isEmpty()){
               return writeSession; //Если есть авария сначала обработаем ее//
            }
        }
        return writeSession;
    }

}
