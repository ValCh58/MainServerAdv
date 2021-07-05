package processingdata;

import static data.DataMeter.ERROR_ELECTRIC_METER;
import static data.DataMeter.FACTORY_NUMBER;
import static data.DataMeter.FACTORY_NUMBER_USPD;
import static data.DataMeter.PROGRAM_VERSION;

/**
 * Обработка данных от счетчиков эл. энергии и воды
 *
 * @author chvaleriy
 */
public class CounterDataProcessing {

    private String strMessage = "";
    private String[] arrMessage;
    
    /**
     *  Обработка данных по счетчикам тепла
     */
    private DataFromHeaterMeter dfhm;
    /**
     * Обработка данных по счетчикам воды
     */
    private DataFromWaterFlowMeter dfwfm;
    /**
     * Обработка данных по эл. счетчикам
     */
    private DataFromElectricityMeter dfem;

    public CounterDataProcessing(String message) {
        this.strMessage = message;
    }

    public String getStrMessage() {
        return strMessage;
    }

    public void setStrMessage(String strMessage) {
        this.strMessage = strMessage;
    }
    
    /**
     * Обработка данных по тепловым счетчикам
     *
     * @return
     */
    public boolean processingHeaterMeter(){
        boolean result = false;
        
        dfhm = new DataFromHeaterMeter();
        result = dfhm.setDataMeter(strMessage);
        return result;
    }
    

    /**
     * Обработка данных по эл. счетчикам
     *
     * @return
     */
    public boolean processingElectricityMeter() {
        boolean result = false;

        if (isCounterData5003()) {//Обработаем данные в норм режиме
            arrMessage = strMessage.split(PROGRAM_VERSION);
            arrMessage[1] = PROGRAM_VERSION + arrMessage[1];
            if (arrMessage.length > 0 && !arrMessage[1].isEmpty()) {
                dfem = new DataFromElectricityMeter();
                result = dfem.setDataMeter(arrMessage[1]);
            }
        }
        else if (isErrorData500()) {//Обработаем данные в аварийном режиме 
            arrMessage = strMessage.split(ERROR_ELECTRIC_METER);
            arrMessage[1] = ERROR_ELECTRIC_METER + arrMessage[1];
            if (arrMessage.length > 0 && arrMessage[0].isEmpty() && !arrMessage[1].isEmpty()) {
                dfem = new DataFromElectricityMeter();
                result = dfem.setDataMeter(arrMessage[1]);
            }
        }

        return result;
    }

    /**
     * Обработка данных по тепл. сч.
     * @return 
     */
    public DataFromHeaterMeter getDfhm() {
        return dfhm;
    }
    
    /**
     * Обработка данных по счетчикам воды
     *
     * @return
     */
    public DataFromWaterFlowMeter getDfwfm() {
        return dfwfm;
    }

    /**
     * Обработка данных по эл. счетчикам
     *
     * @return
     */
    public DataFromElectricityMeter getDfem() {
        return dfem;
    }

    /**
     * Обработка данных по счетчикам воды
     *
     * @return
     */
    public boolean processingWaterFlowMeter() {
        boolean result = false;

        if (!isCounterDataUSPD()) {
            return result;
        }

        arrMessage = strMessage.split(FACTORY_NUMBER_USPD);
        arrMessage[1] = FACTORY_NUMBER_USPD + arrMessage[1];

        if (arrMessage.length > 1 && !arrMessage[1].isEmpty()) {
            dfwfm = new DataFromWaterFlowMeter();
            result = dfwfm.setDataMeter(arrMessage[1]);
        }
        return result;
    }

    /**
     * Cодержатся ли данные от счетчиков воды в сообщении
     *
     * @return
     */
    private boolean isCounterDataUSPD() {
        return strMessage.contains(FACTORY_NUMBER_USPD);
    }

    /**
     * Cодержатся ли данные от счетчико эл. энергии в сообщении
     *
     * @return
     */
    private boolean isCounterData5003() {
        return strMessage.contains(FACTORY_NUMBER);
    }

    /**
     * Есть ли признак аварии эл. счетчика
     */
    private boolean isErrorData500() {
        return strMessage.startsWith(ERROR_ELECTRIC_METER, 0);
    }
}
