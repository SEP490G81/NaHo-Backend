package org.naho.shared.constant;

import java.time.ZoneId;

public final class SystemZoneId {
    public static final String HO_CHI_MINH_ZONE_ID_NAME = "Asia/Ho_Chi_Minh";
    public static final ZoneId HO_CHI_MINH_ZONE_ID = ZoneId.of(HO_CHI_MINH_ZONE_ID_NAME);

    private SystemZoneId() {
    }
}
