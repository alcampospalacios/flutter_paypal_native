package com.alcampospalacios.paypal.paypal_native_checkout.models;

import com.paypal.android.corepayments.PayPalSDKError;
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutListener;
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutResult;


public class PayPalNativeCallBackHelper implements PayPalNativeCheckoutListener{

    @Override
    public void onPayPalCheckoutCanceled() {
        // TODO: Implementation
    }

    @Override
    public void onPayPalCheckoutFailure(PayPalSDKError payPalSDKError) {
        // TODO: Implementation

    }

    @Override
    public void onPayPalCheckoutStart() {
        // TODO: Implementation

    }

    @Override
    public void onPayPalCheckoutSuccess(PayPalNativeCheckoutResult payPalNativeCheckoutResult) {
        // TODO: Implementation

    }
}
