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

    
    public static RecvPacketRuleCfg getRecvPacketRuleOfEndChar2() {
        RecvPacketRuleCfg rule = new RecvPacketRuleCfg();
        rule.setType(TcpConstants.RecvPacketRuleConstants.TYPE_ENDCHAR_2);
        rule.set("endChar1", "0X03");//Предпоследний байт посылки
        rule.set("endChar2", "0X02");//Последний байт посылки
        //rule.set("escapeChar", "\\");
        return rule;
    }

}
