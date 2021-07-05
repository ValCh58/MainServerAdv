package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Properties;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 *
 * @author chvaleriy
 */
public class SafeReadWriteProp {

    public final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(SafeReadWriteProp.class);
    public final static int FILE_OK = 0;
    public final static int FILE_NOT_FOUND = 1;
    public final static int FILE_NOT_ENCRIPT = 2;

    private String fKey = null;//файл ключа//
    private String FileStream = null;//Файл свойств//
    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;
    private SecretKey key = null;
    private CipherInputStream cis = null;

    public SafeReadWriteProp(String fkey, String fStream) {
        fKey = fkey;
        FileStream = fStream;

    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (ois != null) {
                ois.close();
            }
            if (oos != null) {
                oos.close();
            }
            if (cis != null) {
                cis.close();
            }
        } finally {
            super.finalize();
        }
    }

    private boolean getKeyFile() {//Чтение ключа///////////////////////////////
        if (!(new File(fKey)).isFile()) {
            logger.error("file not found " + fKey);
            return false;
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(fKey);
            ois = new ObjectInputStream(fileInputStream);
            DESKeySpec ks = null;
            try {
                ks = new DESKeySpec((byte[]) ois.readObject());
            } catch (InvalidKeyException | ClassNotFoundException ex) {
                logger.error(ex);
            }
            SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
            key = skf.generateSecret(ks);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
            logger.error(ex);
            return false;
        }
        return true;
    }///////////////////////////////////////////////////////////////////////////

    private void setKeyFile() {//Запись ключа в файл/////////////////////////////
        try {
            KeyGenerator kg = KeyGenerator.getInstance("DES");
            kg.init(new SecureRandom());
            key = kg.generateKey();
            SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
            Class spec = Class.forName("javax.crypto.spec.DESKeySpec");
            DESKeySpec ks = (DESKeySpec) skf.getKeySpec(key, spec);
            oos = new ObjectOutputStream(new FileOutputStream(fKey));
            oos.writeObject(ks.getKey());
        } catch (NoSuchAlgorithmException | ClassNotFoundException | InvalidKeySpecException | IOException ex) {
            logger.error(ex);
        }
    }///////////////////////////////////////////////////////////////////////////

    public InputStreamReader getPropFromFile() {
        if (!getKeyFile()) {
            logger.error("file not found " + fKey);
            return null;
        }
        if (!(new File(FileStream)).isFile()) {
            logger.error("file not found " + FileStream);
            return null;
        }
        try {
            Cipher c = Cipher.getInstance("DES/CFB8/NoPadding");
            c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec((byte[]) ois.readObject()));
            cis = new CipherInputStream(new FileInputStream(FileStream), c);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IOException | ClassNotFoundException ex) {
            logger.error(ex);
        }
        return (new InputStreamReader(cis));
    }

    public void setPropToFile(Properties p) {
        setKeyFile();
        try {
            Cipher c = null;
            try {
                c = Cipher.getInstance("DES/CFB8/NoPadding");
                c.init(Cipher.ENCRYPT_MODE, key);
                FileOutputStream fOS = new FileOutputStream(FileStream);
                CipherOutputStream cos = new CipherOutputStream(fOS, c);
                OutputStreamWriter oSW = new OutputStreamWriter(cos);
                p.store(oSW, null);
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | FileNotFoundException ex) {
                logger.error(ex);
            }
            oos.writeObject(c.getIV());
            oos.close();
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

}
