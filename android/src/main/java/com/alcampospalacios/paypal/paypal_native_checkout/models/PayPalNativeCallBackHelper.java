package com.alcampospalacios.paypal.paypal_native_checkout.models;

import com.paypal.android.corepayments.PayPalSDKError;
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutListener;
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutResult;

import io.flutter.plugin.common.MethodChannel.Result;

import androidx.annotation.NonNull;
import com.google.gson.Gson;



public class PayPalNativeCallBackHelper implements PayPalNativeCheckoutListener{
    private Result result;

    // Call this method to init the instance of result that communicates with flutter channel
    public void setResult(@NonNull Result result) {
        this.result = result;
    }

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
        // Convert the result to JSON
        String jsonResult = convertToJson(payPalNativeCheckoutResult);
        this.result.success(jsonResult);
    }

    private String convertToJson(PayPalNativeCheckoutResult result) {
        Gson gson = new Gson();
        String json = gson.toJson(result);
        return json;
    }
}
