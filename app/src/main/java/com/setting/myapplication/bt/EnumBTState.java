package com.setting.myapplication.bt;

public enum EnumBTState {
    disconnected,       // BT 연결 되지 않음.
    scanning,           // OBD 검색 중.
    gatt_connecting,    // OBD 연결 중.
    gatt_connected,     // gatt 서버에 연결 되었음. (BT 장비와 연결 되었음.)
    services_discovered,
    service_connected,  // 데이터를 주고 받을 수 있는 상태
    obd_logging,        // OBD 로그인 중.
    obd_logging_fail,   // OBD 로그인 실패
    obd_key_off,        // 차량의 시동이 꺼져 있는 상태
    obd_connected,      // OBD 정상 연결 되었음. (로그인 성공)
}
