package com.alcampospalacios.paypal.flutter_paypal_native.models.approvaldata;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutResult;


public class PPApprovalData {

    @SerializedName("payerId")
    @Expose
    private String payerId;
    @SerializedName("orderId")
    @Expose
    private String orderId;
    @SerializedName("intent")
    @Expose
    private String intent;

    public static PPApprovalData fromPayPalObject(PayPalNativeCheckoutResult approval ) {
        PPApprovalData app = new PPApprovalData();
        app.setOrderId(approval.getOrderId());
        app.setPayerId(approval.getPayerId());
        app.setIntent("PURCHASE");

        return app;
    }

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }


}