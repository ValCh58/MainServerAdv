package uspdsql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import mainserver.ConfigServer;
import processingdata.CounterDataProcessing;

public class ThreadeInsertSQL implements Runnable {
    
    public final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ThreadeInsertSQL.class);

    Connection connection = null;
    CounterDataProcessing cdp = null;
    private static final Properties prop = ConfigServer.getInstance();

    public ThreadeInsertSQL(Connection connection, CounterDataProcessing cdp) {
        this.connection = connection;
        this.cdp = cdp;
    }
    
    
    /**
     * Запись обработанных данных и сырых данных в таблицы Measuring и raw_data 
     */
    void writeRow() {
        
        if(isWriteable()){
           return;
        }
        
                 
        try {
          PreparedStatement  pstmt = connection.prepareStatement("INSERT INTO eisystems.measuring ("
                  + "ERROR_EN"
                  + ",PROG_VERSION_EN"
                  + ",FACTORY_NUMBER_EN"
                  + ",DATETIME_EN"
                  + ",DAILY_TARIFF_EN"
                  + ",NIGHT_TARIFF_EN"
                  + ",FACTORY_NUMBER_USPD"
                  + ",COUNT_1_W"
                  + ",COUNT_2_W"
                  + ",COUNT_3_W"
                  + ",COUNT_4_W"
                  + ",COUNT_5_W"
                  + ",COUNT_6_W"
                  + ",COUNT_7_W"
                  + ",COUNT_8_W"
                  + ",INPUT_STATUS_USPD"
                  + ",OUTPUT_STATUS_USPD"
                  + ",POWER_BATTERY_USPD"
                  + ",LOW_POWER_BATTERY_USPD"
                  + ",THRESHOLD_COUNTER_USPD"
                  + ",ALARM_STATUS_ANALOG_SENSOR"
                  + ",HEAT_METER_NUM"
                  + ",G_KALOR"
                  + ",HEAT_TIME"
                  + ",HEAT_DATE"
                  + ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            //Запись показаний эл. счетчиков//
            pstmt.setString(1, cdp.getDfem() == null ? "" : cdp.getDfem().getErrorElectrycity());
            pstmt.setString(2, cdp.getDfem() == null ? "offline" : cdp.getDfem().getProgVersion());
            pstmt.setString(3, cdp.getDfem() == null ? "" : cdp.getDfem().getFactoryNumber());
            pstmt.setString(4, cdp.getDfem() == null ? "" : cdp.getDfem().getDateTime());
            pstmt.setDouble(5, cdp.getDfem() == null ? (double)0.0 : cdp.getDfem().getDailyTariff());
            pstmt.setDouble(6, cdp.getDfem() == null ? (double)0.0 : cdp.getDfem().getNightTariff());
            //Запись показаний водянных счетчиков//
            pstmt.setString(7, cdp.getDfwfm() == null ? "" : cdp.getDfwfm().getFactoryNumber());
            pstmt.setDouble(8, cdp.getDfwfm() == null ? 0.0 : cdp.getDfwfm().getCount_1());
            pstmt.setDouble(9, cdp.getDfwfm() == null ? 0.0 : cdp.getDfwfm().getCount_2());
            pstmt.setDouble(10, cdp.getDfwfm() == null ? 0.0 : cdp.getDfwfm().getCount_3());
            pstmt.setDouble(11, cdp.getDfwfm() == null ? 0.0 : cdp.getDfwfm().getCount_4());
            pstmt.setDouble(12, cdp.getDfwfm() == null ? 0.0 : cdp.getDfwfm().getCount_5());
            pstmt.setDouble(13, cdp.getDfwfm() == null ? 0.0 : cdp.getDfwfm().getCount_6());
            pstmt.setDouble(14, cdp.getDfwfm() == null ? 0.0 : cdp.getDfwfm().getCount_7());
            pstmt.setDouble(15, cdp.getDfwfm() == null ? 0.0 : cdp.getDfwfm().getCount_8());
            pstmt.setString(16, cdp.getDfwfm() == null ? "" : cdp.getDfwfm().getInputStatus());
            pstmt.setString(17, cdp.getDfwfm() == null ? "" : cdp.getDfwfm().getOutputStatus());
            pstmt.setDouble(18, cdp.getDfwfm() == null ? 0.0 : cdp.getDfwfm().getPowerBattery());
            pstmt.setString(19, cdp.getDfwfm() == null ? "" : cdp.getDfwfm().getLowPowerBattery());
            pstmt.setString(20, cdp.getDfwfm() == null ? "" : cdp.getDfwfm().getThresholdCounter());
            pstmt.setString(21, cdp.getDfwfm() == null ? "" : cdp.getDfwfm().getAlarmStatusAnalogSensors());
            //Запись данных тепл. сч.//
            pstmt.setString(22, cdp.getDfhm()== null ? "" : (cdp.getDfhm().getHeaterFactoryNum().isEmpty() ? "offline" : cdp.getDfhm().getHeaterFactoryNum()));
            pstmt.setDouble(23, cdp.getDfhm()== null ? 0.0 : cdp.getDfhm().getgKalor());
            pstmt.setString(24, cdp.getDfhm()== null ? "" : cdp.getDfhm().getHeatTime());
            pstmt.setString(25, cdp.getDfhm()== null ? "" : cdp.getDfhm().getHeatDate());
            
            pstmt.executeUpdate();
            connection.commit();
            ///////////////////////////////////////////////////////////////////////////////////////////////
            //Запись данных в таблицу raw_data///////////////////////////////////////////////////////////// 
            PreparedStatement  pstmt_1 = connection.prepareStatement("INSERT INTO eisystems.raw_data " +
                                                                   "(ip_server, port, num_uspd, raw_data)" +
                                                                    " VALUES(?, ?, ?, ?);");
            
            pstmt_1.setString(1, prop.getProperty("ipHostServer"));
            pstmt_1.setInt(2, Integer.parseInt(prop.getProperty("portServer")));
            pstmt_1.setString(3, cdp.getDfwfm() == null ? "" : cdp.getDfwfm().getFactoryNumber());
            pstmt_1.setString(4, cdp.getStrMessage() == null ? "" : cdp.getStrMessage());
            pstmt_1.executeUpdate();
            connection.commit();
            ////////////////////////////////////////////////////////////////////////////////////////////////
        } catch (SQLException ex) {
            try {
                connection.rollback();
            } catch (SQLException ex1) {
                logger.error(ex1.getMessage());
            }
             logger.error(ex.getMessage());
        } finally {
            try {
                if(connection != null){
                   connection.close();
                }
            } catch (SQLException ex) {
                 logger.error(ex.getMessage());
            }
        }
    }
    
    private boolean isWriteable(){
        if(connection == null || cdp == null){
            logger.error("Ошибка записи данных на MySql сервер. NULL сообщение от обработчика данных USPD или ошибка соединения.");
            return true;
        }
        return false;
    }
    

    @Override
    public void run() {
        writeRow();
    }

}
