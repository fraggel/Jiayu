package es.jiayu.jiayuid;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

/**
 * Created by Fraggel on 1/10/13.
 */
public class Utilidades {
    private static String fileToMD5(File filePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            byte[] buffer = new byte[1024];
            MessageDigest digest = MessageDigest.getInstance("MD5");
            int numRead = 0;
            while (numRead != -1) {
                numRead = inputStream.read(buffer);
                if (numRead > 0)
                    digest.update(buffer, 0, numRead);
            }
            byte [] md5Bytes = digest.digest();
            return convertHashToString(md5Bytes);
        } catch (Exception e) {
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) { }
            }
        }
    }

    private static String convertHashToString(byte[] md5Bytes) {
        String returnVal = "";
        for (int i = 0; i < md5Bytes.length; i++) {
            returnVal += Integer.toString(( md5Bytes[i] & 0xff ) + 0x100, 16).substring(1);
        }
        return returnVal;
    }
    public static boolean checkFileMD5(File filePath) {
        boolean iguales=false;
        try {
            String md5File=fileToMD5(filePath);
            String nombre=filePath.getName();

            if(nombre.toLowerCase().indexOf(md5File.toLowerCase())!=-1){
                iguales=true;
            }else{
                iguales=false;
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return iguales;
    }
}
