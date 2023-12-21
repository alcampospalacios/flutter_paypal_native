package com.alcampospalacios.paypal.paypal_native_checkout;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ICaptureOrderApi {
    @POST("/v2/checkout/orders/{orderId}/capture")
    Call<Void> captureOrder(@Path("orderId") String orderId);
}
