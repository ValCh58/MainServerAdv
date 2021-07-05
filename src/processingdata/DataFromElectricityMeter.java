package processingdata;

import data.ElectricityMeter;

/**
 * 
 * @author chvaleriy
 */
public class DataFromElectricityMeter extends ElectricityMeter{
        @Override
    public boolean setDataMeter(final String message) {
        
        String[] str = message.split("\\)");
        String[] tmp;
        int checkData = 0;

        if (str.length < CHECK_DATA_ELECTRIC) {
            return false;
        }

        for (String s : str) {
            tmp = s.split("\\(");
            if(tmp[0].equals(FACTORY_NUMBER_USPD)) {break;}//Строка счетчиков воды
            switch (tmp[0]) {
                case ERROR_ELECTRIC_METER:
                    this.setErrorElectrycity(tmp[1]);
                    checkData++;
                    break;
                case PROGRAM_VERSION:
                    this.setProgVersion(tmp[1]);
                    checkData++;
                    break;
                case FACTORY_NUMBER:
                    this.setFactoryNumber(tmp[1]);
                    checkData++;
                    break;
                case DATE_TIME:
                    this.setDateTime(tmp[1]);
                    checkData++;
                    break;
                case ELECTRIC_METER_TARIFF:
                    this.setNightTariff(Double.parseDouble(tmp[1]));
                    checkData++;
                    break;
                default: {
                    this.setDailyTariff(Double.parseDouble(tmp[1]));
                    checkData++;
                    
                }
            }
        }
        return (checkData == CHECK_DATA_ELECTRIC) || (checkData == CHECK_DATA_ELECTRIC_ERR);//Если не равно 5 недоставерные данные//
    }
    

}
