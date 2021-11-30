package processingdata;

/**
 * Обработка сигналов аварий от УСПД
 *
 * @author chvaleriy
 */
public class ProcessAlarms {
    /**
     * Работа УСПД от сети outStatusLowPower = "0" - норма.
     * Работа УСПД от аккумулятора outStatusLowPower = "1" - признак аварии.
     */
    String outStatusLowPower = "";
    /**
     * Признаки аварии по двум выходным каналам.
     * outStatus = "01" - канал 0.
     * outStatus = "10" - канал 1.
     * outStatus = "11" - канал 0 и 1.
     */
    String outStatus = "";
    String retVal = "$OF";

    public ProcessAlarms(/*String strOut*/CounterDataProcessing cdp) {
        this.outStatus = cdp.getDfwfm().getOutputStatus();
        this.outStatusLowPower = cdp.getDfwfm().getLowPowerBattery();
    }
    
    /**
     * Есть ли авария по двум выходным каналам.
     * @return 
     */
    private String checkOut() {
        return outStatus.matches("[0-1][0-1]") ? outStatus : "";
    }
    
    /**
     * Есть ли авария по сетевому напряжению.
     * @return 
     */
    private String checkOutLowPower() {
        return outStatusLowPower.matches("[0-1]") ? outStatusLowPower : "";
    }
    
    /**
     * Обработаем outStatusLowPower для ответа.
     * @return 
     */
    public String sendAnswerLowPower(){
        String retStr = "";
        switch(checkOutLowPower()){
            case "1":
                retStr = "0";
                break;
        }
        return retStr.isEmpty()?retStr:(retVal + retStr);
    }
    
    /**
     * Обработаем outStatus для ответа.
     * @return 
     */
    public String sendAnswer() {
        String retStr = "";

        switch (checkOut()) {
            
            case "01":
                retStr = "0"; // Авария в канале 0//
                break;
            case "11":    // Авария в канале 0 и 1//
            case "10":
                retStr = "1"; // Авария в канале 1//
                break;
        }
        return retStr.isEmpty()?retStr:(retVal + retStr);//Обработка аварий. Версия 1
        //return retStr.isEmpty()?retStr:(retVal + "2"); //Временное решение!!!
    }
    
    public String retAlarm(){
        String retval = "";
        
        retval = sendAnswer();
        if(!retval.isEmpty()){
            return retval;
        }
        
        retval = sendAnswerLowPower();
        if(!retval.isEmpty()){
            return retval;
        }
        return retval;
    }

}
