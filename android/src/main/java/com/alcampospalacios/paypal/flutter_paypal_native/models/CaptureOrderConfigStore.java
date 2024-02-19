package com.alcampospalacios.paypal.flutter_paypal_native.models;

import org.jetbrains.annotations.NotNull;

public class CaptureOrderConfigStore {
    public String accessToken;
    public String paypalRequestId;
    public String jsonData;

    public CaptureOrderConfigStore(
            String accessToken,
            String paypalRequestId,
            String jsonData
    ) {
        this.accessToken = accessToken;
        this.paypalRequestId = paypalRequestId;
        this.jsonData = jsonData;
    }
}
