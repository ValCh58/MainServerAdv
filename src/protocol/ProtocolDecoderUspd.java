package protocol;

import java.io.ByteArrayOutputStream;
import mainserver.RecvPacketRuleCfg;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 *
 * @author chvaleriy
 */
public class ProtocolDecoderUspd extends CumulativeProtocolDecoder {

    public final static Logger logger = Logger.getLogger(ProtocolDecoderUspd.class);
    public static final String LAST_ALLDATA = "LAST_ALLDATA";
    /**
     * Начальный маркер символ - endChar_1, конечные маркер-символы - endChar_1,
     * endChar_2
     */
    private byte endChar_1;//предпоследний в сообщении
    private byte endChar_2;//последний ...

    public ProtocolDecoderUspd(RecvPacketRuleCfg rule) {
        setEndChar((String) rule.get("endChar1"), (String) rule.get("endChar2"));
    }

    /**
     * Обнаружение 0X(hex)) чисел и преобразование их в тип byte
     *
     * @param endChar_1
     * @param endChar_2
     */
    public void setEndChar(String endChar_1, String endChar_2) {
        if (endChar_1.matches("0[x,X][0-9,a-f,A-F]+") && endChar_2.matches("0[x,X][0-9,a-f,A-F]+")) {
            this.endChar_1 = Integer.decode(endChar_1).byteValue();
            this.endChar_2 = Integer.decode(endChar_2).byteValue();
        } else {
            this.endChar_1 = 0;
            this.endChar_2 = 0;
        }
    }

    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        //System.out.println("doDecode: " + new String(in.array()));
        int remain = in.remaining();
        byte[] temp = new byte[remain];
        in.get(temp);//запишем в temp bp IoBuffer in байты//
        //установим в аттрибутах сессии поток записи байт//
        if (session.getAttribute(LAST_ALLDATA) == null) {
            session.setAttribute(LAST_ALLDATA, new ByteArrayOutputStream(512));
        }
        //Исправить проблему, что данные в сеансе не удаляются, когда объем данных большой ???//
        ByteArrayOutputStream baos = (ByteArrayOutputStream) session.getAttribute(LAST_ALLDATA);
        int len = baos.size();
        baos.write(temp);
        byte[] allData = temp;

        /**
         * Проверка на конец обмена сообщениями
         */
        if (allData[0] == 0x21 && allData.length == 3) {
            return transmissionEndCheck(baos, out, session);
        }

        /**
         * Чтение информационных данных от USPD
         */
        if (readData(baos, allData, out, session, len)) {
            return true;
        }
        return false;
    }

    @Override
    public void dispose(IoSession session) throws Exception {
        super.dispose(session);
    }

    private boolean readData(ByteArrayOutputStream baos, byte[] allData, ProtocolDecoderOutput out,
            IoSession session, int len) throws Exception {
        //проверим признак начала сообщения и конец//
        //Содержит ли сообщение байты конца строки endChar_1 = 03, endChar_2 = 02  и первый байт в сообщении endChar_2 ?//
        //System.out.println("allData "+allData.length);//DEBUG!!!
        if ((allData.length < 2) || (allData[0] != endChar_2)){// || (allData[0] == endChar_2 && allData[1] == endChar_1)) {
            return false;
        }

        for (int i = 1; i < allData.length; i++) {
            if (allData[i - 1] == endChar_1){// && allData[i] == endChar_2) {
                byte[] buf = baos.toByteArray();
                byte[] towrite = new byte[len + i + 1];
                System.arraycopy(buf, 0, towrite, 0, len + i + 1);
                out.write(towrite);
                baos.flush();
                baos.close();
                session.removeAttribute(LAST_ALLDATA);
                return true;
            }
        }
        return false;
    }

    private boolean transmissionEndCheck(ByteArrayOutputStream baos, ProtocolDecoderOutput out,
            IoSession session) throws Exception {
        byte[] buf = baos.toByteArray();
        out.write(buf);
        baos.flush();
        baos.close();
        session.removeAttribute(LAST_ALLDATA);
        return true;
    }
}
