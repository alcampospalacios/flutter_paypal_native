package com.alcampospalacios.paypal.flutter_paypal_native.models;

import com.alcampospalacios.paypal.flutter_paypal_native.PaypalNativePlugin;
import com.alcampospalacios.paypal.flutter_paypal_native.models.approvaldata.PPApprovalData;
import com.alcampospalacios.paypal.flutter_paypal_native.models.CheckoutConfigStore;
import com.paypal.android.corepayments.PayPalSDKError;
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutListener;
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutResult;

import io.flutter.plugin.common.MethodChannel.Result;

import androidx.annotation.NonNull;
import com.google.gson.Gson;
import java.util.HashMap;
import com.google.gson.GsonBuilder;

import android.os.Build;
import android.util.Log;

import org.jetbrains.annotations.NotNull;


public class PayPalNativeCallBackHelper implements PayPalNativeCheckoutListener{
    private Result result;
    private PaypalNativePlugin flutterPaypalPlugin;
    private CheckoutConfigStore checkoutConfigStore;

    public PayPalNativeCallBackHelper(PaypalNativePlugin flutterPaypalPlugin, CheckoutConfigStore checkoutConfigStore) {
        this.flutterPaypalPlugin = flutterPaypalPlugin;
        this.checkoutConfigStore = checkoutConfigStore;
    }

    // Call this method to init the instance of result that communicates with flutter channel
    public void setResult(@NonNull Result result) {
        this.result = result;
    }

    // Call this method to init the instance of flutterPaypalPlugin
    public void setFlutterPaypalPlugin(@NonNull PaypalNativePlugin flutterPaypalPlugin) {
        this.flutterPaypalPlugin = flutterPaypalPlugin;
    }

    @Override
    public void onPayPalCheckoutCanceled() {
        // Invoking new function on cancel
        flutterPaypalPlugin.invokeMethodOnUiThread("FlutterPaypal#onCancel", null);

        this.result.success("completed");;
    }

    @Override
    public void onPayPalCheckoutFailure(PayPalSDKError payPalSDKError) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("code", String.valueOf(payPalSDKError.getCode()) );
        data.put("error", payPalSDKError.getMessage());
        data.put("errorDescription", payPalSDKError.getErrorDescription());

        // Invoking new function on error
        flutterPaypalPlugin.invokeMethodOnUiThread("FlutterPaypal#onError", data);

        this.result.error("completed", payPalSDKError.getMessage(), payPalSDKError.getMessage());;


    }

    @Override
    public void onPayPalCheckoutStart() {
        // TODO: Implementation

    }

    @Override
    public void onPayPalCheckoutSuccess(PayPalNativeCheckoutResult payPalNativeCheckoutResult) {
        HashMap<String, Object> data = new HashMap<>();
        Gson gson = (new GsonBuilder()).create();
        String json = gson.toJson(PPApprovalData.fromPayPalObject(payPalNativeCheckoutResult));
        data.put("approvalData", json);

        // Invoking new function on success
        flutterPaypalPlugin.invokeMethodOnUiThread("FlutterPaypal#onSuccess", data);

        // Checking if auto capture from client is not active them finished the process with the result.success
        // if is active them the process continue to make the capture from money and result.success is not sent yet
        if (!checkoutConfigStore.autoCaptureFromClient) {
            this.result.success("completed");
        }
    }


    // This method is fired in the capture callback from the money
    public void firedOnCapturedCallBack(@NotNull Result result) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("onCapture", "fired");

        // Invoking method called after
        flutterPaypalPlugin.invokeMethodOnUiThread("FlutterPaypal#onCapture", data);
        result.success("completed");



    }

}
