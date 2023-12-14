package com.alcampospalacios.paypal.paypal_native_checkout.models;

import com.alcampospalacios.paypal.paypal_native_checkout.PaypalNativeCheckoutPlugin;
import com.alcampospalacios.paypal.paypal_native_checkout.models.approvaldata.PPApprovalData;
import com.paypal.android.corepayments.PayPalSDKError;
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutListener;
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutResult;

import io.flutter.plugin.common.MethodChannel.Result;

import androidx.annotation.NonNull;
import com.google.gson.Gson;
import java.util.HashMap;
import com.google.gson.GsonBuilder;

import android.util.Log;




public class PayPalNativeCallBackHelper implements PayPalNativeCheckoutListener{
    private Result result;
    private PaypalNativeCheckoutPlugin flutterPaypalPlugin;

    public PayPalNativeCallBackHelper(PaypalNativeCheckoutPlugin flutterPaypalPlugin) {
        this.flutterPaypalPlugin = flutterPaypalPlugin;
    }

    // Call this method to init the instance of result that communicates with flutter channel
    public void setResult(@NonNull Result result) {
        this.result = result;
    }

    // Call this method to init the instance of flutterPaypalPlugin
    public void setFlutterPaypalPlugin(@NonNull PaypalNativeCheckoutPlugin flutterPaypalPlugin) {
        this.flutterPaypalPlugin = flutterPaypalPlugin;
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
//        String jsonResult = convertToJson(payPalNativeCheckoutResult);
//        this.result.success(jsonResult);
        Log.d("onPayPalCheckoutSuccessNative", "onPayPalCheckoutSuccessNative");
        HashMap<String, Object> data = new HashMap<>();

        Gson gson = (new GsonBuilder()).create();
        Log.d("onPayPalCheckoutSuccessNative after GsonBuilder", "GsonBuilder");
        String json = gson.toJson(PPApprovalData.fromPayPalObject(payPalNativeCheckoutResult));
        data.put("approvalData", json);

        Log.d("onPayPalCheckoutSuccessNative before invoke", "invoke");
        if (flutterPaypalPlugin != null) {
            flutterPaypalPlugin.invokeMethodOnUiThread("FlutterPaypal#onSuccess", data);
        } else {
            Log.e("PayPalNativeCallBackHelper", "flutterPaypalPlugin is null");
        }

        Log.d("onPayPalCheckoutSuccessNative after invoke", "after invoke");
        this.result.success("completed");
    }

    private String convertToJson(PayPalNativeCheckoutResult result) {
        Gson gson = new Gson();
        String json = gson.toJson(result);
        return json;
    }
}
