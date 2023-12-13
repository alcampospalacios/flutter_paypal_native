package com.alcampospalacios.paypal.paypal_native_checkout.models;

import com.paypal.android.corepayments.Environment;


public class CheckoutConfigStore {

   public String clientId ;
    public  Environment payPalEnvironment ;
    public  String returnUrl;


    public CheckoutConfigStore(
            String clientId,
            Environment payPalEnvironment,
            String returnUrl

    ) {
        this.payPalEnvironment = payPalEnvironment;
        this.clientId = clientId;
        this.returnUrl = returnUrl;

    }


}
