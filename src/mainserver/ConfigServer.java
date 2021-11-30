package mainserver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Set;
import util.SafeReadWriteProp;

/**
 *
 * @author chvaleriy
 */
public class ConfigServer {

    public final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ConfigServer.class);
    private FileInputStream in = null;
    public static Properties prop = null;
    private final String keyFile = "./config/config.key";
    private final String propFile = "./config/config.cfg";
    SafeReadWriteProp SRW = new SafeReadWriteProp(keyFile, propFile);
    private static volatile ConfigServer cnfServer = null;

    private ConfigServer() {
        init();
    }

    /**
     * Проверим зашифрован ли файл конфигурации
     *
     * @param fileCheck
     * @return
     */
    public int isFileEncrypt(String fileCheck) {
        int result = SafeReadWriteProp.FILE_NOT_FOUND;
        Properties localProp = new Properties();
        try {
            in = new FileInputStream(fileCheck);
            localProp.load(in);
            Set<Object> set = localProp.keySet();
            result = set.contains("ipHostServer") ? SafeReadWriteProp.FILE_NOT_ENCRIPT : SafeReadWriteProp.FILE_OK;
        } catch (FileNotFoundException ex) {
            logger.error("File " + fileCheck + " Not Found");
        } catch (IOException ex) {
            logger.error(ex);
        } finally {
            if (in != null) try {
                in.close();
            } catch (IOException ex) {
                logger.error(ex);
            }
        }
        return result;
    }

    /**
     * Получение свойств из не зашифрованного файла
     *
     * @return
     */
    private Properties getLocalProp(String fileProp) {
        Properties locProp = null;

        try {
            in = new FileInputStream(fileProp);
            locProp = new Properties();
            locProp.load(in);
        } catch (FileNotFoundException ex) {
            logger.error("File " + fileProp + " Not Found" + ex);
        } catch (IOException ex) {
            logger.error(ex);
        } finally {
            if (in != null) try {
                in.close();
            } catch (IOException ex) {
                logger.error(ex);
            }
        }
        return locProp;
    }

    /**
     * Создание ключа и шифрование файла конфигурации
     */
    private void createEncriptFile() {
        Properties locProp = getLocalProp(propFile);
        SRW.setPropToFile(locProp);
        if (locProp != null) {
            readPropConfig();
        }
    }

    /**
     * Чтение свойств из файла свойств
     *
     */
    private void readPropConfig() {
        InputStreamReader isr = SRW.getPropFromFile();
        if (isr == null) {
            logger.error("Ошибка инициализации файла конфигурации " + propFile);
            return;
        }
        prop = new Properties();
        try {
            prop.load(isr);
        } catch (IOException ex) {
            logger.error(ex);
        } finally {
            if (isr != null)
                try {
                isr.close();
            } catch (IOException ex) {
                logger.error(ex);
            }
        }
    }

    /**
     * Инициализация свойств сервера
     *
     */
    private void init() {

        int sel = isFileEncrypt(propFile);
        switch (sel) {
            case SafeReadWriteProp.FILE_OK:
                readPropConfig();
                break;
            case SafeReadWriteProp.FILE_NOT_FOUND:
                System.exit(sel);
                break;
            case SafeReadWriteProp.FILE_NOT_ENCRIPT:
                createEncriptFile();
                break;
        }

        /* try {
            in = new FileInputStream("./config/config.cfg");
            prop = new Properties();
            prop.load(in);
        } catch (FileNotFoundException ex) {
            logger.error("File \"config/config.cfg\" Not Found" + ex);
        } catch (IOException ex) {
            logger.error(ex);
        } finally {
            if (in != null) try {
                in.close();
            } catch (IOException ex) {
                logger.error(ex);
            }
        }
         */
    }

    @SuppressWarnings("DoubleCheckedLocking")
    public static Properties getInstance() {
        if (prop == null) {
            synchronized (ConfigServer.class) {
                if (prop == null) {
                    cnfServer = new ConfigServer();
                }
            }
        }
        return prop;
    }

    public static ConfigServer getCnfServer() {
        return cnfServer;
    }

}
