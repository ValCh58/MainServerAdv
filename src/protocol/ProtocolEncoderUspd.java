package protocol;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 *
 * @author chvaleriy
 */
public class ProtocolEncoderUspd extends ProtocolEncoderAdapter {
    
public final static Logger logger = Logger.getLogger(ProtocolEncoderUspd.class);
    
    @Override
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
        int lenMessage = ((byte[]) message).length;
        byte[] bs = new byte[lenMessage + 2];
        //byte[] bs = new byte[lenMessage + 3];  
        //  bs[0] = (byte)2; 
        byte[] tmp = (byte[]) message;
        System.arraycopy(tmp, 0, bs, 0, lenMessage);
        bs[lenMessage] = (byte) 0X0D;
        bs[lenMessage + 1] = (byte) 0X0A;
        IoBuffer buffer = IoBuffer.allocate(bs.length).setAutoExpand(true);//Буфер с авторасширением
        buffer.put(bs);
        buffer.flip();
        out.write(buffer);
    }

}
