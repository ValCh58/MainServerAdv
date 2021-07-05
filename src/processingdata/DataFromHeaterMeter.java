package processingdata;

import data.HeaterMeter;

/**
 *
 * @author Valeriy
 */
public class DataFromHeaterMeter extends HeaterMeter{
    
    
    
    public DataFromHeaterMeter(){}

    /**
     * Обработка данных теплосч.
     * @param message
     * @return 
     */
    @Override
    public boolean setDataMeter(String message) {
        String temp = getMsgKarat(message);
        String[] msg = parsKarat(temp);
        if(msg != null && msg.length < 4) {return false;}
        this.setHeaterFactoryNum(msg[0]);//Номер прибора
        this.setgKalor(Double.parseDouble(msg[1]));//Показания в Г. калориях
        this.setHeatTime(msg[2]);//Time
        this.setHeatDate(msg[3]);//Date
        
        return true;
    }
    
    /**
     * Выделение строки по тп.сч.
     * @param msg
     * @return 
     */
    private String getMsgKarat(String msg){
        
        String[] tmpMsg = msg.split("\\)");
        int start = 0;
        String retStr = "";
        
        for(int i=0; i<tmpMsg.length; i++){
            if(tmpMsg.length > 0 && tmpMsg[i].contains("KARAT")){
               start = tmpMsg[i].indexOf('(');
               retStr = tmpMsg[i].substring(start+1);
               break;
            }   
        }
        return retStr;
    }
    
    /**
     * Парсим строку тп. сч.
     * @param str
     * @return 
     */
     private  String[] parsKarat(String str){
        String[] parser;
        parser = str.split("[\\,\\s]+");
        return parser;
    }
    
}
