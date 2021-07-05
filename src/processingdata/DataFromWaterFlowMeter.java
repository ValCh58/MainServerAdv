package processingdata;

import data.WaterFlowMeter;

/**
 *
 * @author chvaleriy
 */
public class DataFromWaterFlowMeter  extends WaterFlowMeter{
    /**
     * water для записи показаний счетчиков воды
     */
    double[] water = new double[7];

    @Override
    public boolean setDataMeter(String message) {
        String[] str = message.split("\\)");
        String[] tmp;
                
        if (str.length < CHECK_DATA_WATER) {
            return false;
        }

        int cntWater = 0;
        for (String s : str) {
            tmp = s.split("\\(");
            switch (tmp[0]) {
                case FACTORY_NUMBER_USPD:
                    setFactoryNumber(tmp[1]);
                    break;
                case WATER_METER_DATA:
                    setCount_1(Double.parseDouble(tmp[1]));
                    break;
                case ENTRANCE_STATUS:
                    setInputStatus(tmp[1]);
                    break;
                case EXIT_STATUS:
                    setOutputStatus(tmp[1]);
                    break;
                case BATTERY_VOLTAGE:
                    setPowerBattery(Double.parseDouble(tmp[1]));
                    break;
                case LOW_POWER_BATTARY:
                    setLowPowerBattery(tmp[1]);
                    break;
                case THRESHOLD_POSITION_COUNTER:
                    setThresholdCounter(tmp[1]);
                    break;
                case ALARM_STATUS_ANALOG_SENSOR:
                    setAlarmStatusAnalogSensors(tmp[1]);
                    break;
                default: {
                    water[cntWater] = Double.parseDouble(tmp[1]);
                    cntWater++;
                }
            }
        }
        addCountToWater();
        return cntWater == CHECK_DATA_WATER;
    }

    /**
     * заполнить счетчики воды с 2 по 8
     */
    void addCountToWater() {
        setCount_2(water[0]);
        setCount_3(water[1]);
        setCount_4(water[2]);
        setCount_5(water[3]);
        setCount_6(water[4]);
        setCount_7(water[5]);
        setCount_8(water[6]);
    }
}
