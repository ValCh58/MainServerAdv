package data;

import java.time.Instant;


/**
 * Данные по счетчику эл. энергии
 * @chvaleriy
 */
public abstract class ElectricityMeter implements DataMeter{

    public ElectricityMeter() {
    }

    public ElectricityMeter(String progVersion, String factoryNumber, String dateTime, double dailyTariff, double nightTariff) {
        this.progVersion = progVersion;
        this.factoryNumber = factoryNumber;
        this.dateTime = dateTime;
        this.dailyTariff = dailyTariff;
        this.nightTariff = nightTariff;
    }
    
    private final Instant idTimestamp = getDataCurrent();
    /** Признак аварии эл.счетчика */
    private String errorElectrycity;
    /** Версия программного обеспечения */
    private String progVersion;
    /** Заводской номер  */
    private String factoryNumber;
    /** Дата и время */
    private String dateTime;
     /** Показания по дневному тарифу */
    private double dailyTariff = 0.0;
     /** Показания по ночному тарифу */
    private double nightTariff = 0.0;

    public String getErrorElectrycity() {
        return errorElectrycity;
    }

    public void setErrorElectrycity(String errorElectrycity) {
        this.errorElectrycity = errorElectrycity;
    }

    public Instant getIdTimestamp() {
        return idTimestamp;
    }
    
    public String getProgVersion() {
        return progVersion;
    }

    public void setProgVersion(String progVersion) {
        this.progVersion = progVersion;
    }

    public String getFactoryNumber() {
        return factoryNumber;
    }

    public void setFactoryNumber(String factoryNumber) {
        this.factoryNumber = factoryNumber;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public double getDailyTariff() {
        return dailyTariff;
    }

    public void setDailyTariff(double dailyTariff) {
        this.dailyTariff = dailyTariff;
    }

    public double getNightTariff() {
        return nightTariff;
    }

    public void setNightTariff(double nightTariff) {
        this.nightTariff = nightTariff;
    }
}
