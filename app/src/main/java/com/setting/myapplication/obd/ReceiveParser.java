package com.setting.myapplication.obd;

import android.os.Handler;
import android.os.HandlerThread;

public class ReceiveParser {

    Handler handler;

    private final Object mSync = new Object();

    private boolean m_bExit = false;
    private ReceiveListener m_Listener = null;
    private ReceivePacket m_ReceivePacket;

    public ReceiveParser() {
        m_ReceivePacket = new ReceivePacket(PacketCheck.PACKET_MAX_SIZE);
    }

    public void setListener(ReceiveListener listener) {
        m_Listener = listener;
    }

    public void start() {
        HandlerThread handlerThread = new HandlerThread("ReceiveParsingThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

        handler.post(receiveRunnable);
    }

    Runnable receiveRunnable = new Runnable() {
        @Override
        public void run() {

            while (!m_bExit) {

                byte[] packet;

                synchronized (mSync) {
                    packet = m_ReceivePacket.popPacket();
                }

                if (packet == null) {
                    // 파싱할 데이터가 없으면 대기
                    try {
                        synchronized (mSync) {
                            mSync.wait();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (m_Listener != null) {
                        m_Listener.onReceivedPacket(packet, packet.length);
                    }
                }
            }
        }
    };

    /**
     * 종료 시 호출 바람.
     */
    public void exit() {
        synchronized (mSync) {
            mSync.notifyAll();
        }

        if (handler != null) {
            handler.getLooper().quit();
        }
    }

    /**
     * 수신 데이터
     *
     * @param data
     */
    public void push(byte[] data, int nSize) {
        synchronized (mSync) {
            m_ReceivePacket.push(data, nSize);
            mSync.notifyAll();
        }
    }

}
