package es.jiayu.jiayuid;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.Calendar;
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
    public static boolean controlRoot(Context context,Resources res,String origen) {
        boolean rootB = false;
        File f = new File("/system/bin/su");
        if (!f.exists()) {
            f = new File("/system/xbin/su");
            if (f.exists()) {
                rootB = true;
            }
        } else {
            rootB = true;
        }
        if (rootB) {
            try {
                Runtime rt = Runtime.getRuntime();
                java.lang.Process p = rt.exec("su");
                BufferedOutputStream bos = new BufferedOutputStream(
                        p.getOutputStream());
                Calendar instance = Calendar.getInstance();
                String fecha=String.valueOf(instance.get(Calendar.DAY_OF_MONTH))+
                String.valueOf((instance.get(Calendar.MONTH)+1))+
                String.valueOf(instance.get(Calendar.YEAR))+
                String.valueOf(instance.get(Calendar.HOUR))+
                String.valueOf(instance.get(Calendar.MINUTE))+
                String.valueOf(instance.get(Calendar.SECOND))+
                String.valueOf(instance.get(Calendar.MILLISECOND));
                bos.write(("echo " + fecha + " > /data/jiayu.txt").getBytes());
                bos.write(("exit").getBytes());
                bos.flush();
                bos.close();
                p.waitFor();
                Runtime rt2 = Runtime.getRuntime();
                java.lang.Process p2 = rt.exec("su");
                StreamGobbler errorGobbler = new StreamGobbler(p2.getErrorStream(),
                        "ERR");
                StreamGobbler outputGobbler = new StreamGobbler(p2.getInputStream(),
                        "OUT");
                errorGobbler.start();
                outputGobbler.start();

                BufferedOutputStream bos2 = new BufferedOutputStream(
                        p2.getOutputStream());

                bos2.write(("rm /data/jiayu.txt").getBytes());

                bos2.write(("exit").getBytes());

                bos2.flush();

                bos2.close();

                int ret=p2.waitFor();
                if(ret==0){
                    rootB=true;
                }else{
                    rootB=false;
                }
            } catch (Exception e) {
                rootB=false;
            }
        }
        return rootB;
    }
    public static boolean controlRootSinExec(Context context,Resources res,String origen) {
        boolean rootB = false;
        File f = new File("/system/bin/su");
        if (!f.exists()) {
            f = new File("/system/xbin/su");
            if (f.exists()) {
                rootB = true;
            }
        } else {
            rootB = true;
        }
        return rootB;
    }
    public static boolean checkFileMD5(File filePath) {
        boolean iguales=false;
        try {
            String md5File=fileToMD5(filePath);
            String nombre=filePath.getName();
            /*String decomp =md5File;
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
            }*/
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(Environment.getExternalStorageDirectory() + "/JIAYUES/CHECKSUM.md5"))));
            String line=in.readLine();
            while (line!=null){
                String[] md5=line.split("  ");
                if(nombre.toLowerCase().equals(md5[1].toLowerCase())){
                    if(md5File.toUpperCase().equals(md5[0].toUpperCase())){
                        iguales=true;
                        break;
                    }else{
                        iguales=false;
                    }
                }else{
                    if(md5File.toUpperCase().equals(md5[0].toUpperCase())){
                        iguales=true;
                        break;
                    }else{
                        iguales=false;
                    }
                }
                line=in.readLine();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return iguales;
    }
    public static String comprobarRecovery(Context context,Resources res,String origen) {
        String cwm="cwm";
        String carl="crl";
        String ori="ori";
        String recovery="";
        try {
            File f=new File(Environment.getExternalStorageDirectory()+"/JIAYUES/last_log");
            if(f.exists()){
                f.delete();
            }
            Runtime rt = Runtime.getRuntime();
            java.lang.Process p = rt.exec("su");
            BufferedOutputStream bos = new BufferedOutputStream(
                    p.getOutputStream());
            bos.write(("busybox cp /cache/recovery/last_log " + Environment.getExternalStorageDirectory() + "/JIAYUES/last_log" + "\n")
                    .getBytes());
            bos.write(("cp /cache/recovery/last_log " + Environment.getExternalStorageDirectory() + "/JIAYUES/last_log" + "\n")
                    .getBytes());
            bos.write(("exit").getBytes());
            bos.flush();
            bos.close();
            p.waitFor();
            f=new File(Environment.getExternalStorageDirectory()+"/JIAYUES/last_log");
            if(f.exists()){
                FileInputStream fis=new FileInputStream(f);
                byte bb[]=new byte[1024];
                fis.read(bb);
                String str=new String(bb);
                if(str.toUpperCase().lastIndexOf("CWM")!=-1){
                    recovery=cwm;
                }else if(str.toUpperCase().lastIndexOf("CARLIV")!=-1){
                    recovery=carl;
                }else{
                    recovery=ori;
                }
            }else{
                Toast.makeText(context, res.getString(R.string.msgNecesarioReiniDetect), Toast.LENGTH_LONG).show();
            }

        }catch(Exception e){
            e.printStackTrace();
            recovery="";
        }
        return recovery;
    }
    public static int[] descomponerFecha(String fechaPasada) {
        int day=Integer.parseInt(fechaPasada.trim().split("/")[0]);
        int month=Integer.parseInt(fechaPasada.trim().split("/")[1])-1;
        int year=Integer.parseInt(fechaPasada.trim().split("/")[2]);
        int fecha[]=new int[3];
        fecha[0]=day;
        fecha[1]=month;
        fecha[2]=year;
        return fecha;
    }
}
