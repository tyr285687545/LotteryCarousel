package com.become.lottery.utils;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Util {

    public static int dpToPx(int dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int) (dp * metrics.density);
    }

    public static int pxToDp(int px) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int) (px / metrics.density);
    }

    public static int spToPx(int sp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, metrics);
    }

    public static int pxToSp(int px) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return px / (int) metrics.scaledDensity;
    }


    /**
     * 将javaBean转换成Map
     *
     * @param bean javaBean
     * @return Map对象
     */
    public static HashMap<String, Object> beanToHash(Object bean) {

        HashMap<String, Object> hashMap = new HashMap<>();
        Class clazz = bean.getClass();
        List<Class> clazzs = new ArrayList<>();

        do {
            clazzs.add(clazz);
            clazz = clazz.getSuperclass();
        } while (!clazz.equals(Object.class));

        for (Class iClazz : clazzs) {

            Field[] fields = iClazz.getDeclaredFields();

            try {
                for (Field field : fields) {
                    Object objVal;
                    field.setAccessible(true);
                    objVal = field.get(bean);
                    if (objVal != null) {
                        hashMap.put(field.getName(), objVal);
                    }

                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return hashMap;
    }

    public static double changToTwoDecimal(double in) {
        DecimalFormat df = new DecimalFormat("#0.00");
        String out = df.format(in);
        return Double.parseDouble(out);
    }

    /**
     * 转换文件大小单位
     *
     * @param fileS
     * @return
     */
    public static String formatFileSizeForK(long fileS) {
        DecimalFormat df = new DecimalFormat("#0.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "K";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "M";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "G";
        }
        return fileSizeString;
    }

    /**
     * 转换文件大小单位
     *
     * @param fileS
     * @return
     */
    public static String formatFileSizeForByte(long fileS) {//转换文件大小
        DecimalFormat df = new DecimalFormat("#0.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else if (fileS < 1099511627776L) {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    /**
     * 获取存储可用空间
     *
     * @return
     */
    public static String getAvailableMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        //获取可用区块数量
        long availableBlocks = stat.getAvailableBlocks();
        long size = availableBlocks * blockSize;
        return Util.formatFileSizeForByte(size);
    }

    /**
     * 获取目录下文件总大小
     *
     * @return
     */
    public static String getFileSize(String path) {
        long size = 0;
        File dir = new File(path);
        File[] files = dir.listFiles();
        if (files == null) return "0.00B";
        for (File file : files) {
            size += file.length();
        }
        return Util.formatFileSizeForByte(size);
    }



    /**
     * 应用是否安装
     *
     * @param pageName
     * @param context
     * @return 该方法容易报错：java.lang.RuntimeException: Package manager has died
     */
    public static boolean isInstallApp(String pageName, Context context) {
        PackageInfo packageInfo;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(pageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            packageInfo = null;
//			e.printStackTrace();
        }
        return packageInfo != null;
    }

    /**
     * 应用是否安装
     *
     * @param packageName
     * @param context
     * @return
     */
    public static boolean isAvilible(String packageName, Context context) {
        PackageManager packageManager = context.getPackageManager();
        //获取手机系统的所有APP包名，然后进行一一比较
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if ((pinfo.get(i)).packageName.equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }


    /**
     * 安装应用
     *
     * @param apkFile
     * @param context
     */
    public static void installNormal(File apkFile, Context context) {
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            installIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, "com.jdc.bank.fileprovider", apkFile);
            installIntent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            installIntent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
//            installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(installIntent);
        apkFile.deleteOnExit();
    }


    /**
     * 判断某一个应用程序是不是系统的应用程序，
     * 如果是返回true，否则返回false。
     */
    public static boolean filterApp(ApplicationInfo info) {
        //有些系统应用是可以更新的，如果用户自己下载了一个系统的应用来更新了原来的，它还是系统应用，这个就是判断这种情况的
        if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
            return true;
            //判断是不是系统应用
        } else return (info.flags & ApplicationInfo.FLAG_SYSTEM) == 0;
    }

    /**
     * 保存文本
     *
     * @param strcontent
     */
    public static void writeTextToFile(String strcontent) {
        //生成文件夹之后，再生成文件，不然会出错
        String filePath = Environment.getExternalStorageDirectory().getPath();
        String fileName = "/JsRequestLog.log";
        makeFilePath(filePath, fileName);
        String strFilePath = filePath + fileName;

        // 每次写入时，都换行写
        String strContent = getTime() + "---" + strcontent + "\r\n";
        try {
            File file = new File(strFilePath);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + strFilePath);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    // 生成文件
    public static File makeFilePath(String filePath, String fileName) {
        File file = null;
        makeRootDirectory(filePath);
        try {
            file = new File(filePath + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    // 生成文件夹
    public static void makeRootDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            Log.i("error:", e + "");
        }
    }

    /**
     * 获取时间
     *
     * @return
     */
    public static String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }

    public static String getData() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(new Date());
    }

    public static String convertTime(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }



    /**
     * 清除web缓存
     *
     * @param dir
     * @param numDays
     * @return
     */
    public static int clearCacheFolder(File dir, long numDays) {
        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, numDays);
                    }
                    if (child.lastModified() < numDays) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }

    /**
     * 手机号中间隐藏
     *
     * @param mobile
     * @return
     */
    public static StringBuffer hideMobile(String mobile) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(mobile, 0, 4);
        stringBuffer.append("****" + mobile.substring(mobile.length() - 3, mobile.length()));
        return stringBuffer;
    }

    /**
     * 姓名部分隐藏
     *
     * @param name
     * @return
     */
    public static StringBuffer hideName(String name) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < name.length() - 1; i++) {
            stringBuffer.append("*");
        }
        stringBuffer.append(name, name.length() - 1, name.length());
        return stringBuffer;
    }

    /**
     * 身份证部分隐藏
     *
     * @param idCardNum
     * @return
     */
    public static StringBuffer hideIdNum(String idCardNum) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(idCardNum, 0, 3);
        stringBuffer.append("***********" + idCardNum.substring(idCardNum.length() - 3, idCardNum.length()));
        return stringBuffer;
    }

    /**
     * 转base64
     *
     * @param path
     * @return
     * @throws Exception
     */
    public static String encodeBase64File(String path) {
        File file = new File(path);
        FileInputStream inputFile = null;
        try {
            inputFile = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            inputFile.read(buffer);
            inputFile.close();
            return Base64.encodeToString(buffer, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 复制文本
     *
     * @param context
     * @param text
     */
    public static void copyToClipboard(Context context, String text) {
        ClipboardManager clip = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clip.setText(text); // 复制
    }

    /**
     * 拨打电话（跳转到拨号界面，用户手动点击拨打）
     *
     * @param phoneNum 电话号码
     */
    public static void callPhone(Context context,String phoneNum) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        context.startActivity(intent);
    }

    //--------------------------------------------------------------------------------

    public static String secondToMinute(int second) {
        int minute = second / 60;
        int lastSecond = second % 60;
        return (minute < 10 ? "0" + minute : minute) + ":" + (lastSecond < 10 ? "0" + lastSecond : lastSecond);
    }
}
