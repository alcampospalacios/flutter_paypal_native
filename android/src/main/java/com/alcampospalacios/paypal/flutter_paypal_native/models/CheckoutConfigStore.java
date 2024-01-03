package com.alcampospalacios.paypal.flutter_paypal_native.models;

import com.paypal.android.corepayments.Environment;


public class CheckoutConfigStore {

    public String clientId ;
    public Environment payPalEnvironment ;
    public String returnUrl;
    public Boolean autoCaptureFromClient;



    public CheckoutConfigStore(
            String clientId,
            Environment payPalEnvironment,
            String returnUrl,
            Boolean autoCaptureFromClient


    ) {
        this.payPalEnvironment = payPalEnvironment;
        this.clientId = clientId;
        this.returnUrl = returnUrl;
        this.autoCaptureFromClient = autoCaptureFromClient;

    }


}
