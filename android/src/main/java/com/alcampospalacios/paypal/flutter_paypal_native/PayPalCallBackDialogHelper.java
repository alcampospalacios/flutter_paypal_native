package com.alcampospalacios.paypal.flutter_paypal_native;

import android.util.Log;

import androidx.annotation.NonNull;

import com.alcampospalacios.paypal.flutter_paypal_native.models.CheckoutConfigStore;
import com.alcampospalacios.paypal.flutter_paypal_native.models.approvaldata.PPApprovalData;
import com.alcampospalacios.paypal_dialog.PaypalDialogListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.paypal.android.corepayments.PayPalSDKError;
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutListener;
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutResult;

import java.util.HashMap;

import io.flutter.plugin.common.MethodChannel.Result;


public class PayPalCallBackDialogHelper implements PaypalDialogListener {
    private Result result;
    private PaypalNativePlugin flutterPaypalPlugin;

    public PayPalCallBackDialogHelper(PaypalNativePlugin flutterPaypalPlugin) {
        this.flutterPaypalPlugin = flutterPaypalPlugin;
    }

    // Call this method to init the instance of result that communicates with flutter channel
    public void setResult(@NonNull Result result) {
        this.result = result;
    }

    @Override
    public void onSuccessCapture(String s) {
//        firedOnCapturedCallBackFromDialog();
    }

    @Override
    public void onSuccessConfirmedPayment() {
        firedOnCapturedCallBackFromDialog();

    }

    @Override
    public void onErrorCapture(String s) {
        this.result.error("completed", s, s);
    }

    @Override
    public void onCancelPayOrder() {

    }

    // This method is fired in the capture callback from the money
    private void firedOnCapturedCallBackFromDialog() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("onCapture", "fired");

        // Invoking method called after
        flutterPaypalPlugin.invokeMethodOnUiThread("FlutterPaypal#onCapture", data);
        this.result.success("completed");
    }
}
