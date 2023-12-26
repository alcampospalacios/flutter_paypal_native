package com.alcampospalacios.paypal.flutter_paypal_native;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ICaptureOrderApi {
    @POST("/v2/checkout/orders/{orderId}/capture")
    Call<Void> captureOrder(@Path("orderId") String orderId);
}
