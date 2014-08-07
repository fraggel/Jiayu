package es.jiayu.jiayuid;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Environment;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.security.MessageDigest;
import java.util.ArrayList;
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
    public static boolean controlRootConExec(Context context,Resources res,String origen) {
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
        String twrp="twrp";
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
                }else if(str.toUpperCase().lastIndexOf("CARLIV")!=-1) {
                    recovery = carl;
                }else if(str.toUpperCase().lastIndexOf("TWRP")!=-1){
                        recovery=twrp;
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
    public static boolean compExtendedSDcard(String modelo){
     boolean extendedMemory=false;
        if("G4A".equals(modelo)||
                "G5A".equals(modelo)||
                "S1".equals(modelo)||
                "S2A".equals(modelo)||
                "G4S".equals(modelo)||
                "G5S".equals(modelo)||
                "G6A".equals(modelo)){
            extendedMemory=true;
        }else{
            extendedMemory=false;
        }
        return extendedMemory;
    }
    public static String[] comprobarSD(Context ctx,Resources res) {
        String[] listaMems=null;
        String a0="";
        String a1="";
        try {

            BufferedInputStream bis=new BufferedInputStream(new FileInputStream("//proc/mounts"));
            byte[] a=new byte[4096];
            bis.read(a);
            String asd=new String(a);
            String dev0="";
            String mount0="";
            String formato0="";
            String dev1="";
            String mount1="";
            String formato1="";
            String mmcblk0="";
            String mmcblk1="";
            if(asd.indexOf("sdcard0")!=-1){
                //Hay al menos una memoria
                BufferedReader reader = new BufferedReader(new StringReader(asd));
                String load ="";
                load = reader.readLine();
                while(load!=null){
                    if(load.indexOf("sdcard0")!=-1){
                        String[] split = load.split(" ");
                        if(split[0].indexOf("tmpfs")==-1 && split[0].indexOf("vold")!=-1) {
                            dev0 =  split[0].split("/")[4];
                            mount0 = split[1];
                            formato0 = split[2];
                        }
                        if(split[0].indexOf("fuse")!=-1) {
                            mount0 = split[1];
                        }
                    }
                    load = reader.readLine();
                }
                BufferedInputStream bis2=new BufferedInputStream(new FileInputStream("//proc/partitions"));
                byte[] a2=new byte[4096];
                bis2.read(a2);
                String asd2=new String(a2);
                BufferedReader reader2 = new BufferedReader(new StringReader(asd2));
                String load2="";
                load2=reader2.readLine();

                while(load2!=null){
                    load2=load2.replaceAll("  "," ");
                    load2=load2.replaceAll("  "," ");
                    load2=load2.replaceAll("  "," ");
                    load2=load2.replaceAll("  "," ");
                    load2=load2.replaceAll("  "," ");
                    load2=load2.replaceAll("  "," ");
                    load2=load2.replaceAll("  "," ");
                    load2=load2.replaceAll("  "," ");
                    load2=load2.replaceAll("\t"," ");
                    load2=load2.replaceAll("\t"," ");
                    load2=load2.replaceAll("\t"," ");
                    load2=load2.replaceAll("\t"," ");
                    load2=load2.replaceAll("\t"," ");
                    load2=load2.replaceAll("\t"," ");
                    load2=load2.replaceAll("\t"," ");
                    load2=load2.replaceAll(" ",":");
                    if(load2.indexOf(dev0)!=-1){
                        mmcblk0=load2.split(":")[4];
                    }

                    load2=reader2.readLine();
                }


                if(asd.indexOf("sdcard1")!=-1){
                    //Hay dos memorias
                    reader = new BufferedReader(new StringReader(asd));
                    load ="";
                    load = reader.readLine();
                    while(load!=null){
                        if(load.indexOf("sdcard1")!=-1){
                            String[] split = load.split(" ");
                            if(split[0].indexOf("tmpfs")==-1 && (split[0].indexOf("vold")!=-1)) {
                                dev1 = split[0].split("/")[4];
                                mount1 = split[1];
                                formato1 = split[2];
                            }
                            if(split[0].indexOf("fuse")!=-1){
                                mount1 = split[1];
                            }
                        }

                        load = reader.readLine();
                    }
                    bis2=new BufferedInputStream(new FileInputStream("//proc/partitions"));
                    a2=new byte[4096];
                    bis2.read(a2);
                    asd2=new String(a2);
                    reader2 = new BufferedReader(new StringReader(asd2));
                    load2="";
                    load2=reader2.readLine();

                    while(load2!=null){
                        load2=load2.replaceAll("  "," ");
                        load2=load2.replaceAll("  "," ");
                        load2=load2.replaceAll("  "," ");
                        load2=load2.replaceAll("  "," ");
                        load2=load2.replaceAll("  "," ");
                        load2=load2.replaceAll("  "," ");
                        load2=load2.replaceAll("  "," ");
                        load2=load2.replaceAll("  "," ");
                        load2=load2.replaceAll("\t"," ");
                        load2=load2.replaceAll("\t"," ");
                        load2=load2.replaceAll("\t"," ");
                        load2=load2.replaceAll("\t"," ");
                        load2=load2.replaceAll("\t"," ");
                        load2=load2.replaceAll("\t"," ");
                        load2=load2.replaceAll("\t"," ");
                        load2=load2.replaceAll(" ",":");
                        if(load2.indexOf(dev1)!=-1){
                            mmcblk1=load2.split(":")[4];
                        }
                        load2=reader2.readLine();
                    }

                }

                if(mmcblk0.indexOf("mmcblk0")!=-1) {
                    a0=res.getString(R.string.memoriaInterna)+":"+mount0;
                }else if(mmcblk0.indexOf("mmcblk1")!=-1) {
                    a0=res.getString(R.string.memoriaExterna)+":"+mount0;
                }
                if(mmcblk1.indexOf("mmcblk1")!=-1){
                    a1=res.getString(R.string.memoriaExterna)+":"+mount1;
                }else if(mmcblk1.indexOf("mmcblk0")!=-1){
                    a1=res.getString(R.string.memoriaInterna)+":"+mount1;
                }

            }/*else if(sdcard) {
                //Aquí sería para android 4.0.4 por ejemplo....

            }*/else{
            }
            if(!"".equals(a0) && !"".equals(a1)){
                listaMems= new String[3];
                listaMems[0] =res.getString(R.string.selectDefaultMemory)+": ";
                listaMems[1]=a0;
                listaMems[2]=a1;
            }else if(!"".equals(a0) && "".equals(a1)){
                listaMems= new String[2];
                listaMems[0] =res.getString(R.string.selectDefaultMemory)+": ";
                listaMems[1]=a0;
            }else if("".equals(a0) && !"".equals(a1)) {
                listaMems = new String[2];
                listaMems[0] =res.getString(R.string.selectDefaultMemory)+": ";
                listaMems[1] = a1;
            }
        }catch(Exception e){
            Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return listaMems;
    }
    public static String[] comprobarSDText(Context ctx,Resources res){
        String[] tmp=comprobarSD(ctx,res);
        String[] tmp2=new String[tmp.length];
        for(int x=0;x<tmp.length;x++){
            tmp2[x]= tmp[x].split(":")[0];
        }
        return tmp2;
    }

    public static void crearDirectorios(Context ctx,Resources res,SharedPreferences ajustes) {
        String[] stringsCompletos = Utilidades.comprobarSD(ctx, res);
        String[] soloMountPoint = new String[stringsCompletos.length];
        for (int x = 0; x < stringsCompletos.length; x++) {
            soloMountPoint[x] = stringsCompletos[x].split(":")[1];
        }
        ajustes.edit().putString("selectedMemory", soloMountPoint[ajustes.getInt("defaultMem", 1)]);
        ajustes.edit().commit();
        if (soloMountPoint.length > 1) {
            File f1 = new File(soloMountPoint[ajustes.getInt("defaultMem", 1)] + "/JIAYUES/APP/");
            if (!f1.exists()) {
                f1.mkdirs();
            }
            File f2 = new File(soloMountPoint[ajustes.getInt("defaultMem", 1)] + "/JIAYUES/ROMS/");
            if (!f2.exists()) {
                f2.mkdirs();
            }
            File f3 = new File(soloMountPoint[ajustes.getInt("defaultMem", 1)] + "/JIAYUES/RECOVERY/");
            if (!f3.exists()) {
                f3.mkdirs();
            }
            File f4 = new File(soloMountPoint[ajustes.getInt("defaultMem", 1)] + "/JIAYUES/DOWNLOADS/");
            if (!f4.exists()) {
                f4.mkdirs();
            }
            File f5 = new File(soloMountPoint[ajustes.getInt("defaultMem", 1)] + "/JIAYUES/IMEI/");
            if (!f5.exists()) {
                f5.mkdirs();
            }
            File f6 = new File(soloMountPoint[ajustes.getInt("defaultMem", 1)] + "/JIAYUES/BOOTANIMATION/");
            if (!f6.exists()) {
                f6.mkdirs();
            }
        }
    }
}
