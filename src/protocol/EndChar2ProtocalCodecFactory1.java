package protocol;

import mainserver.RecvPacketRuleCfg;
import org.apache.log4j.Logger;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

/**
 * Протокол сообщения-разделителя
 */
public class EndChar2ProtocalCodecFactory1 implements ProtocolCodecFactory {

    public final static Logger logger = Logger.getLogger(EndChar2ProtocalCodecFactory1.class);
        
    private final ProtocolEncoderUspd encoder;
    private final ProtocolDecoderUspd decoder;

    public EndChar2ProtocalCodecFactory1(RecvPacketRuleCfg rule) {
        //Кодер передаваемых сообщений//
        encoder = new ProtocolEncoderUspd();
       
        //ДеКодер принимаемых сообщений//
        decoder = new ProtocolDecoderUspd(rule);
    }

    @Override
    public ProtocolEncoder getEncoder(IoSession session) throws Exception {
        return encoder;
    }

    @Override
    public ProtocolDecoder getDecoder(IoSession session) throws Exception {
        return decoder;
    }
    
}
