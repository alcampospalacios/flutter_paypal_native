package com.alcampospalacios.paypal.paypal_native_checkout.models;

import com.paypal.android.corepayments.Environment;


public class CheckoutConfigStore {

    public String clientId ;
    public Environment payPalEnvironment ;
    public String returnUrl;
    public Boolean autoCaptureFromClient;
    public String accessToken;
    public String paypalRequestId;


    public CheckoutConfigStore(
            String clientId,
            Environment payPalEnvironment,
            String returnUrl,
            Boolean autoCaptureFromClient,
            String accessToken,
            String paypalRequestId

    ) {
        this.payPalEnvironment = payPalEnvironment;
        this.clientId = clientId;
        this.returnUrl = returnUrl;
        this.autoCaptureFromClient = autoCaptureFromClient;
        this.accessToken = accessToken;
        this.paypalRequestId = paypalRequestId;
    }


}
