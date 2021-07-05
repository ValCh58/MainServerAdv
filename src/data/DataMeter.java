package data;

import java.time.Instant;

/**
 * Интерфейс для классов данных по счетчикам
 * @chvaleriy
 */
public interface DataMeter {
    /** Теплосчетчик */
    public static final String DEVICE_NAME_HEATER_CNT = "KARAT";
        
    /** Маркеры электросчетчиков*/
    /** Признак аварии Эл. счетчика*/
    public static final String ERROR_ELECTRIC_METER = "!500";//Признак не известен точно. Мельцова О.В.//
    /** Версия программного обеспечения */ 
    public static final String PROGRAM_VERSION = "5002";
    /** Заводской номер  */
    public static final String FACTORY_NUMBER = "5003";
    /** Дата и время */
    public final static String DATE_TIME = "0001";
    /** Показания по ночному и дневному тарифам */
    public final static String ELECTRIC_METER_TARIFF = "1001";
    /** Контроль данных в рабочем нормальном режиме */
    public static final int CHECK_DATA_ELECTRIC= 5;
    /** Контроль данных в рабочем аварийном режиме */
    public static final int CHECK_DATA_ELECTRIC_ERR= 3;
    
    /** Маркеры USPD*/
    /** Заводской номер 'USPD' */
    public static final String FACTORY_NUMBER_USPD = "USPD";
    /** Показания счетчиков воды 8 каналов 'A000' */
    public static final String WATER_METER_DATA = "A000";
    /** Состояние входов 'Inp' */
    public static final String ENTRANCE_STATUS = "Inp";
    /** Состояние выходов 'Out' */
    public static final String EXIT_STATUS = "Out";
    /** Напряжение батареи 'Power' */
    public static final String BATTERY_VOLTAGE = "Power";
    /** Низкий заряд батареи 'LowPower' */
    public static final String LOW_POWER_BATTARY = "LowPower";
    /** Позиция счётчика с превышением порога 'AlrCnt' */
    public static final String THRESHOLD_POSITION_COUNTER = "AlrCnt"; 
    /** Состояние аварии аналоговых датчиков 'ErrIn' */
    public static final String ALARM_STATUS_ANALOG_SENSOR = "ErrIn";
    /** Контроль данных  */
    public static final int CHECK_DATA_WATER = 7;
    
    /**
     *
     * @param message
     * @return
     */
    boolean setDataMeter(final String message);
    
    /** Получение штампа времени
     * @return 
     */
    default Instant getDataCurrent(){
        return Instant.now();
    }
}
