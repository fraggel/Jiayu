package es.jiayu.jiayuid;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

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
            String decomp =md5File;
            String compres=null;
            try {
                // Encode a String into bytes
                String inputString = md5File;
                byte[] input = inputString.getBytes("UTF-8");

                // Compress the bytes
                byte[] output = new byte[100];
                Deflater compresser = new Deflater();
                compresser.setInput(input);
                compresser.finish();
                int compressedDataLength = compresser.deflate(output);
                compresser.end();
                compres=new String(output);
                // Decompress the bytes
                Inflater decompresser = new Inflater();
                decompresser.setInput(output, 0, compressedDataLength);
                byte[] result = new byte[100];
                int resultLength = decompresser.inflate(result);
                decompresser.end();

                // Decode the bytes into a String
                decomp = new String(result, 0, resultLength, "UTF-8");
            } catch(java.io.UnsupportedEncodingException ex) {
                // handle
            } catch (java.util.zip.DataFormatException ex) {
                // handle
            }


            if(nombre.toLowerCase().indexOf(compres.toLowerCase())!=-1){
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
