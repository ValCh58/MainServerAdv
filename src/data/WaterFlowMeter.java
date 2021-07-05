package data;

import java.time.Instant;

/**
 *Данные по счетчику расхода воды
 * @author chvaleriy
 */
public abstract class WaterFlowMeter  implements DataMeter{

    public WaterFlowMeter() {
        
    }

    public WaterFlowMeter(String factoryNumber, 
                          double count_1, double count_2, double count_3, double count_4,
                          double count_5, double count_6, double count_7, double count_8,
                          String inputStatus, String outputStatus, double powerBattery, 
                          String lowPowerBattery, String thresholdCounter) {
        
        this.factoryNumber = factoryNumber;
        this.count_1 = count_1;
        this.count_2 = count_2;
        this.count_3 = count_3;
        this.count_4 = count_4;
        this.count_5 = count_5;
        this.count_6 = count_6;
        this.count_7 = count_7;
        this.count_8 = count_8;
        this.inputStatus = inputStatus;
        this.outputStatus = outputStatus;
        this.powerBattery = powerBattery;
        this.lowPowerBattery = lowPowerBattery;
        this.thresholdCounter = thresholdCounter;        
    }
    
    private final Instant idTimestamp = getDataCurrent();
    /** Заводской номер  */
    private String factoryNumber;
    /** Показания счетчиков воды 1 канал  */
    private double count_1 = 0.0;
    /** Показания счетчиков воды 2 канал  */
    private double count_2 = 0.0;
    /** Показания счетчиков воды 3 канал  */
    private double count_3 = 0.0;
    /** Показания счетчиков воды 4 канал  */
    private double count_4 = 0.0;
    /** Показания счетчиков воды 5 канал  */
    private double count_5 = 0.0;
    /** Показания счетчиков воды 6 канал  */
    private double count_6 = 0.0;
    /** Показания счетчиков воды 7 канал  */
    private double count_7 = 0.0;
    /** Показания счетчиков воды 8 канал  */
    private double count_8 = 0.0;
    /** Состояние входов  */
    private String inputStatus = "00";
    /** Состояние выходов  */
    private String outputStatus = "00";
     /** Напряжение батареи */
    private double powerBattery = 0.0;
    /** Низкий заряд батареи  */
    private String lowPowerBattery = "0";
    /** Позиция счётчика с превышением порога  */
    private String thresholdCounter = "00";
    /** Состояние аваарии аналоговых датчиков */
    private String alarmStatusAnalogSensors = "00";

    public String getAlarmStatusAnalogSensors() {
        return alarmStatusAnalogSensors;
    }

    public void setAlarmStatusAnalogSensors(String alarmStatusAnalogSensors) {
        this.alarmStatusAnalogSensors = alarmStatusAnalogSensors;
    }

    public String getFactoryNumber() {
        return factoryNumber;
    }

    public void setFactoryNumber(String factoryNumber) {
        this.factoryNumber = factoryNumber;
    }

    public double getCount_1() {
        return count_1;
    }

    public void setCount_1(double count_1) {
        this.count_1 = count_1;
    }

    public double getCount_2() {
        return count_2;
    }

    public void setCount_2(double count_2) {
        this.count_2 = count_2;
    }

    public double getCount_3() {
        return count_3;
    }

    public void setCount_3(double count_3) {
        this.count_3 = count_3;
    }

    public double getCount_4() {
        return count_4;
    }

    public void setCount_4(double count_4) {
        this.count_4 = count_4;
    }

    public double getCount_5() {
        return count_5;
    }

    public void setCount_5(double count_5) {
        this.count_5 = count_5;
    }

    public double getCount_6() {
        return count_6;
    }

    public void setCount_6(double count_6) {
        this.count_6 = count_6;
    }

    public double getCount_7() {
        return count_7;
    }

    public void setCount_7(double count_7) {
        this.count_7 = count_7;
    }

    public double getCount_8() {
        return count_8;
    }

    public void setCount_8(double count_8) {
        this.count_8 = count_8;
    }

    public String getInputStatus() {
        return inputStatus;
    }

    public void setInputStatus(String inputStatus) {
        this.inputStatus = inputStatus;
    }

    public String getOutputStatus() {
        return outputStatus;
    }

    public void setOutputStatus(String outputStatus) {
        this.outputStatus = outputStatus;
    }

    public double getPowerBattery() {
        return powerBattery;
    }

    public void setPowerBattery(double powerBattery) {
        this.powerBattery = powerBattery;
    }

    public String getLowPowerBattery() {
        return lowPowerBattery;
    }

    public void setLowPowerBattery(String lowPowerBattery) {
        this.lowPowerBattery = lowPowerBattery;
    }

    public String getThresholdCounter() {
        return thresholdCounter;
    }

    public void setThresholdCounter(String thresholdCounter) {
        this.thresholdCounter = thresholdCounter;
    }
}
