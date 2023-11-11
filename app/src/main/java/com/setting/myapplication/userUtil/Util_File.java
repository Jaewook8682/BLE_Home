package com.setting.myapplication.userUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Util_File {


    public byte[] read(String path) {
        File f = new File(path);
        return read(f);
    }

    public byte[] read(File f) {

        byte[] buf = null;

        // 존재 여부?
        if (!f.exists())
            return buf;

        buf = new byte[(int) f.length()];

        try {
            FileInputStream fis = new FileInputStream(f);

            fis.read(buf);

            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return buf;
    }

    public boolean write(String path, byte[] buf, int len) {
        return write(new File(path), buf, len);
    }

    public boolean write(File f, byte[] buf, int len) {
        boolean rtn = false;

        try {
            FileOutputStream fos = new FileOutputStream(f);

            fos.write(buf, 0, len);

            rtn = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rtn;
    }

}
