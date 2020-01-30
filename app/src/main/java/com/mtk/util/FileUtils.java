package com.mtk.util;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DecimalFormat;

public class FileUtils {
    public static void write(Context context, String fileName, String content) {
        if (content == null) {
            content = "";
        }
        try {
            FileOutputStream fos = context.openFileOutput(fileName, 0);
            fos.write(content.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String read(Context context, String fileName) {
        try {
            return readInStream(context.openFileInput(fileName));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String readInStream(FileInputStream inStream) {
        try {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            while (true) {
                int length = inStream.read(buffer);
                if (length != -1) {
                    outStream.write(buffer, 0, length);
                } else {
                    outStream.close();
                    inStream.close();
                    return outStream.toString();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File createFile(String folderPath, String fileName) {
        File destDir = new File(folderPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return new File(folderPath, fileName + fileName);
    }

    public static void copyFile(InputStream in, File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (file.exists()) {
            try {
                FileOutputStream os = new FileOutputStream(file);
                byte[] b = new byte[1024];
                while (true) {
                    int len = in.read(b);
                    if (len != -1) {
                        os.write(b, 0, len);
                    } else {
                        in.close();
                        os.close();
                        return;
                    }
                }
            } catch (FileNotFoundException e2) {
                e2.printStackTrace();
            } catch (IOException e3) {
                e3.printStackTrace();
            }
        }
    }

    public static boolean writeFile(byte[] buffer, String folder, String fileName) throws Throwable {
        Exception e;
        Throwable th;
        boolean writeSucc = false;
        String folderPath = "";
        if (Environment.getExternalStorageState().equals("mounted")) {
            folderPath = Environment.getExternalStorageDirectory() + File.separator + folder + File.separator;
        } else {
            writeSucc = false;
        }
        File fileDir = new File(folderPath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        FileOutputStream out = null;
        try {
            FileOutputStream out2 = new FileOutputStream(new File(folderPath + fileName));
            try {
                out2.write(buffer);
                writeSucc = true;
                try {
                    out2.close();
                    out = out2;
                } catch (IOException e2) {
                    e2.printStackTrace();
                    out = out2;
                }
            } catch (Exception e3) {
                e = e3;
                out = out2;
                try {
                    e.printStackTrace();
                    try {
                        out.close();
                    } catch (IOException e22) {
                        e22.printStackTrace();
                    }
                    return writeSucc;
                } catch (Throwable th2) {
                    th = th2;
                    try {
                        out.close();
                    } catch (IOException e222) {
                        e222.printStackTrace();
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                out = out2;
                out.close();
                throw th;
            }
        } catch (Exception e4) {
            e = e4;
            e.printStackTrace();
            out.close();
            return writeSucc;
        }
        return writeSucc;
    }

    public static String getFileName(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }

    public static String getFileNameNoFormat(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1, filePath.lastIndexOf(46));
    }

    public static String getFileFormat(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(46) + 1);
    }

    public static long getFileSize(String filePath) {
        File file = new File(filePath);
        if (file == null || !file.exists()) {
            return 0;
        }
        return file.length();
    }

    public static String getFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        DecimalFormat df = new DecimalFormat("##.##");
        float temp = ((float) size) / 1024.0f;
        if (temp >= 1024.0f) {
            return df.format((double) (temp / 1024.0f)) + "M";
        }
        return df.format((double) temp) + "K";
    }

    public static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) {
            return df.format((double) fileS) + "B";
        }
        if (fileS < 1048576) {
            return df.format(((double) fileS) / 1024.0d) + "KB";
        }
        if (fileS < 1073741824) {
            return df.format(((double) fileS) / 1048576.0d) + "MB";
        }
        return df.format(((double) fileS) / 1.073741824E9d) + "G";
    }

    public static long getDirSize(File dir) {
        long j = 0;
        if (dir != null && dir.isDirectory()) {
            j = 0;
            for (File file : dir.listFiles()) {
                if (file.isFile()) {
                    j += file.length();
                } else if (file.isDirectory()) {
                    j = (j + file.length()) + getDirSize(file);
                }
            }
        }
        return j;
    }

    public long getFileList(File dir) {
        File[] files = dir.listFiles();
        long count = (long) files.length;
        for (File file : files) {
            if (file.isDirectory()) {
                count = (count + getFileList(file)) - 1;
            }
        }
        return count;
    }

    public static byte[] toBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while (true) {
            int ch = in.read();
            if (ch != -1) {
                out.write(ch);
            } else {
                byte[] buffer = out.toByteArray();
                out.close();
                return buffer;
            }
        }
    }

    public static boolean checkFileExists(String name) {
        if (name.equals("")) {
            return false;
        }
        return new File(Environment.getExternalStorageDirectory().toString() + name).exists();
    }

    public static boolean existFile(String path) {
        File file = new File(path);
        return file.isFile() && file.exists();
    }

    public static long getFreeDiskSpace() {
        long freeSpace = 0;
        if (!Environment.getExternalStorageState().equals("mounted")) {
            return -1;
        }
        try {
            StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
            freeSpace = (((long) stat.getAvailableBlocks()) * ((long) stat.getBlockSize())) / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return freeSpace;
    }

    public static boolean createDirectory(String directoryName) {
        if (directoryName.equals("")) {
            return false;
        }
        boolean mkdir = new File(Environment.getExternalStorageDirectory().toString() + directoryName).mkdir();
        return true;
    }

    public static boolean checkSaveLocationExists() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            return true;
        }
        return false;
    }

    public static boolean deleteDirectory(String fileName) {
        SecurityManager checker = new SecurityManager();
        if (fileName.equals("")) {
            return false;
        }
        File newPath = new File(Environment.getExternalStorageDirectory().toString() + fileName);
        checker.checkDelete(newPath.toString());
        if (!newPath.isDirectory()) {
            return false;
        }
        String[] listfile = newPath.list();
        int i = 0;
        while (i < listfile.length) {
            try {
                new File(newPath.toString() + "/" + listfile[i].toString()).delete();
                i++;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        newPath.delete();
        return true;
    }

    public static boolean deleteFile(String fileName) {
        SecurityManager checker = new SecurityManager();
        if (fileName.equals("")) {
            return false;
        }
        File newPath = new File(Environment.getExternalStorageDirectory().toString() + fileName);
        checker.checkDelete(newPath.toString());
        if (!newPath.isFile()) {
            return false;
        }
        try {
            newPath.delete();
            return true;
        } catch (SecurityException se) {
            se.printStackTrace();
            return false;
        }
    }

    public static final String getTempFilePath(Context context) {
        return context.getCacheDir() + File.separator + "temp";
    }

    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        if (!file.isDirectory()) {
            return false;
        }
        String[] tempList = file.list();
        for (int i = 0; i < tempList.length; i++) {
            File temp;
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(temp.getAbsolutePath());
                temp.delete();
                flag = true;
            }
        }
        return flag;
    }

    public static String getFileMD5(File file) {
        Exception e;
        if (!file.isFile()) {
            return null;
        }
        byte[] buffer = new byte[1024];
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            FileInputStream in = new FileInputStream(file);
            while (true) {
                try {
                    int len = in.read(buffer, 0, 1024);
                    if (len != -1) {
                        digest.update(buffer, 0, len);
                    } else {
                        in.close();
                        return new BigInteger(1, digest.digest()).toString(16);
                    }
                } catch (Exception e2) {
                    e = e2;
                    FileInputStream fileInputStream = in;
                }
            }
        } catch (Exception e3) {
            e = e3;
            e.printStackTrace();
            return null;
        }
    }
}
