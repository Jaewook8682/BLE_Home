package com.setting.myapplication.userUtil;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 파일에 로그 저장
 */
public class Util_Log {

    private static volatile Util_Log singletonInstance = null;

    public static Util_Log getInstance() {
        if (singletonInstance == null) {
            synchronized (Util_Log.class) {
                if (singletonInstance == null) {
                    singletonInstance = new Util_Log();
                }
            }
        }
        return singletonInstance;
    }

    public static void releaseInstance() {
        synchronized (Util_Log.class) {
            singletonInstance = null;
        }
    }


    private FileOutputStream fos;
    private String logName = "log";
    private String logPath = "";
    public String logFullPath = "";
    public Context context;

    public void init(Context context, String name, String path) {
        this.context = context;
        logName = name;
        logPath = path;
    }

    public void release() {
        close();
    }

    public boolean open() {

        // 로그 루트 폴더
       ;

//        출처: https://crazykim2.tistory.com/488 [잡다한 프로그래밍]
        File logRootDir = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)), logPath);

        // 오늘 날짜의 폴더 생성
        File logDir = new File(logRootDir, new SimpleDateFormat("yyyyMMdd").format(new Date()));

        Log.e("logDir",logDir.toString());

        if (!logDir.exists())
            logDir.mkdirs();

        File logFile = new File(logDir, logName + "_" + new SimpleDateFormat("HHmm").format(new Date()) + ".txt");

        try {
            fos = new FileOutputStream(logFile);
        } catch (FileNotFoundException e) {
        //    e.printStackTrace();

            fos = null;
            return false;
        }

        return true;
    }

    public void close() {
        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(String msg) {
        write(msg.getBytes());
    }

    public void write(byte[] bytes) {
        write(bytes, 0, bytes.length);
    }

    public void write(byte[] bytes, int off, int len) {
        if (fos == null) {
            if (!open())
                return;
        }

        try {
            fos.write(bytes, off, len);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 파일에 로그 저장
     *
     * @param strLog      저장 할 내용
     * @param isTime      저장 시간 표시 여부
     * @param isLineBreak 줄 바꿈 여부
     */
    public void addLog(String strLog, boolean isTime, boolean isLineBreak) {
        StringBuilder sb = new StringBuilder();

        if (isTime) {
            sb.append("[");
            sb.append(new SimpleDateFormat("HH:mm:dd").format(new Date()));
            sb.append("] ");
        }

        sb.append(strLog);

        if (isLineBreak)
            sb.append("\r\n");

        write(sb.toString());
    }

    /**
     * 파일에 로그 저장
     *
     * @param strLog      저장 할 내용
     * @param isTime      저장 시간 표시 여부
     * @param isLineBreak 줄 바꿈 여부
     * @param append      true : 하루단위로 저장, false 파일 새로 생성 해서 저장
     */
    public void addLog(String strLog, boolean isTime, boolean isLineBreak, boolean append) {
        StringBuilder sb = new StringBuilder();

        if (isTime) {
            sb.append("[");
            sb.append(new SimpleDateFormat("HH:mm:ss").format(new Date()));
            sb.append("] ");
        }

        sb.append(strLog);

        if (isLineBreak)
            sb.append("\r\n");

        write(sb.toString(), append);
    }
    public boolean open(boolean append) {

        // 로그 루트 폴더
        File logRootDir = new File(Environment.getExternalStorageDirectory().toString(), logPath);

        if (!logRootDir.exists())
            logRootDir.mkdirs();

        File logFile;

        if (append) {
            logFile = new File(logRootDir, logName + "_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".txt");
        } else {
            // 오늘 날짜의 폴더 생성
            File logDir = new File(logRootDir, new SimpleDateFormat("yyyyMMdd").format(new Date()));
            if (!logDir.exists())
                logDir.mkdirs();
            logFile = new File(logDir, logName + "_" + new SimpleDateFormat("HHmm").format(new Date()) + ".txt");
        }

        if (logFile != null)
            logFullPath = logFile.getPath();

        try {
            fos = new FileOutputStream(logFile, append);
        } catch (FileNotFoundException e) {
            e.printStackTrace();

            fos = null;
            return false;
        }

        return true;
    }
    public void write(String msg, boolean append) {
        write(msg.getBytes(), append);
    }

    public void write(byte[] bytes, boolean append) {
        write(bytes, 0, bytes.length, append);
    }

    public void write(byte[] bytes, int off, int len, boolean append) {
        if (fos == null) {
            if (!open(append))
                return;
        }

        try {
            fos.write(bytes, off, len);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
