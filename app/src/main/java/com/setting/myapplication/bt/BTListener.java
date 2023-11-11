package com.setting.myapplication.bt;

import java.util.UUID;

/**
 * 블루투스 상태 및 패킷 수신
 */
public interface BTListener {

    void onBTState(EnumBTState state);

    void onReceive(UUID uuid, byte[] data);

    void onChanged(byte[] data);

    void setBleMtu(int mtu);

}
