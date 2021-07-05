package mainserver;

import java.util.Properties;
import protocol.TcpConstants;
import shorttcp.ShortAcceptorHandler;
import shorttcp.ShortTcpAcceptor;

/**
 *
 * @author chvaleriy
 */
public class MainServer {

    public final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MainServer.class);
    private static final Properties prop = ConfigServer.getInstance();
    
    /**
     * @param args the command line arguments
     */

    public static void main(String[] args) {
        
        try {
            startServer();
        } catch (Exception ex) {
            logger.error("Server initialization error: " + ex);
        }
        
    }

    public static void startServer() throws Exception {
        /**
         * Установка типа принимаемого пакета
         */
        RecvPacketRuleCfg rule = getRecvPacketRuleOfEndChar2();
        rule.set("ip", prop.getProperty("ipHostServer"));
        rule.set("port", Integer.parseInt(prop.getProperty("portServer")));
        
        /**
         * Инициализация аттрибутов rule и обработчика событий
         * ShortAcceptorHandler сокета
         */
        TcpAcceptor acceptor = new ShortTcpAcceptor(rule, new ShortAcceptorHandler());
        acceptor.start();
        System.out.println(prop.getProperty("ipHostServer") + ":" + 
                prop.getProperty("portServer") + 
                "\nServer started successfully ... " + 
                (prop.getProperty("modeSSL").equals("1") ? "SSL mode Enabled. Data is encrypted." : "SSL mode Disabled. Data is not encrypted!"));
    }

    /**
     * Сообщение фиксированной длины <br>
     * Пример сообщения: «12345», сообщение фиксированной длины длиной 5
     *
     * @return
     */
    public static RecvPacketRuleCfg getRecvPacketRuleOfFixLength() {
        RecvPacketRuleCfg rule = new RecvPacketRuleCfg();
        rule.setType(TcpConstants.RecvPacketRuleConstants.TYPE_FIXLENGTH);
        rule.set("len", 5);
        return rule;
    }

    /**
     * Длина тега сообщения <br>
     * Пример сообщения: "$$$000008abcdefgh__" <br>
     * Тип значения поля длины: valType - char2, смещение позиции поля длины -
     * 3, длина - 6, а отступ - 0, поэтому 000008 - длина сообщения. Длина,
     * которую необходимо прочитать после получения длины сообщения, составляет
     * 8 - (- 2) = 10, поэтому все сообщение abcdefgh__, 8 букв плюс 2
     * подчеркивания.
     *
     * @return
     */
    public static RecvPacketRuleCfg getRecvPacketRuleOfLengthIdentify() {
        RecvPacketRuleCfg rule = new RecvPacketRuleCfg();
        rule.setType(TcpConstants.RecvPacketRuleConstants.TYPE_LENGTHIDENTIFY);
        rule.set("valType", TcpConstants.LengthIdentifyConstants.VALTYPE_CHARS);// Тип значения поля длины
        rule.set("fillType", TcpConstants.LengthIdentifyConstants.FILLTYPE_CHAR);// Способ заполнения
        rule.set("fillChar", "0");// Заполнить Char
        rule.set("fillLocation", TcpConstants.LengthIdentifyConstants.FILLLOCATION_LEFT);// Направление заполнения
        rule.set("offset", 3/*3*/);// Где находится поле длины
        rule.set("len", 6/*6*/);// Длина поля len
        rule.set("reduce", -2/*-2*/);// Число отброшенных байтов, 
        //значение поля длины -reduce - длина пакета, который должен быть получен
        return rule;//$$$000008abcdefgh__
    }

    /**
     * Разделитель сообщений <br>
     * Пример сообщения: «00000008 \ # cdefghi #», перед первым # экранируется
     * #, поэтому он будет пропущен
     *
     * @return
     */
    public static RecvPacketRuleCfg getRecvPacketRuleOfEndChar() {
        RecvPacketRuleCfg rule = new RecvPacketRuleCfg();
        rule.setType(TcpConstants.RecvPacketRuleConstants.TYPE_ENDCHAR);
        rule.set("endChar", "\n");
        rule.set("escapeChar", "\\");
        return rule;
    }

    public static RecvPacketRuleCfg getRecvPacketRuleOfEndChar2() {
        RecvPacketRuleCfg rule = new RecvPacketRuleCfg();
        rule.setType(TcpConstants.RecvPacketRuleConstants.TYPE_ENDCHAR_2);
        rule.set("endChar1", "0X03");//Предпоследний байт посылки
        rule.set("endChar2", "0X02");//Последний байт посылки
        //rule.set("escapeChar", "\\");
        return rule;
    }

}
