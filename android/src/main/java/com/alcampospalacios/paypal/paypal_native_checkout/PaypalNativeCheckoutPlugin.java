package com.alcampospalacios.paypal.paypal_native_checkout;


import androidx.annotation.NonNull;




import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import okhttp3.Interceptor;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;


import com.alcampospalacios.paypal.paypal_native_checkout.models.ErrorInterceptor;
import com.google.gson.Gson;
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutClient;
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutRequest;
import com.paypal.android.corepayments.CoreConfig;
import com.paypal.android.corepayments.Environment;

import com.alcampospalacios.paypal.paypal_native_checkout.models.PayPalNativeCallBackHelper;
import com.alcampospalacios.paypal.paypal_native_checkout.models.CheckoutConfigStore;
import com.alcampospalacios.paypal.paypal_native_checkout.models.EnvironmentHelper;
import com.alcampospalacios.paypal.paypal_native_checkout.ICaptureOrderApi;


import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** PaypalNativeCheckoutPlugin */
public class PaypalNativeCheckoutPlugin extends FlutterRegistrarResponder
 implements FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
//  private MethodChannel channel;

  
// val config = CoreConfig("CLIENT_ID", environment = Environment.SANDBOX)

// val cardClient = CardClient(config)

    private Application application;
    private CheckoutConfigStore checkoutConfigStore;
//    private PayPalCallBackHelper payPalCallBackHelper;
    boolean initialisedPaypalConfig = false;

    PayPalNativeCheckoutClient payPalNativeClient;

    // Creating a instance of listener to receive callback fron the listener client
    PayPalNativeCallBackHelper payPalNativeCallBackHelper;


  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "paypal_native_checkout");
    channel.setMethodCallHandler(this);
    setChannel(channel);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("FlutterPaypal#initiate")) {
            initiatePackage(call, result);
            return;
        } else if (call.method.equals("FlutterPaypal#makeOrder")) {
            makeOrder(call, result);
            return;
        } else if (call.method.equals("FlutterPaypal#captureMoney")) {
            captureMoney(call, result);
    }
        result.notImplemented();
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  // I need study on android what does mean this
  @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {

        application = binding.getActivity().getApplication();
        initialisePaypalConfig();
    }

    @Override
    public void onDetachedFromActivity() {
        application = null;
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
    }

    private void initiatePackage(@NonNull MethodCall call, @NonNull Result result) {
        String returnUrl = call.argument("returnUrl");
        String clientId = call.argument("clientId");
        String payPalEnvironmentStr = call.argument("payPalEnvironment");

        Environment payPalEnvironment = (new EnvironmentHelper()).getEnumFromString(payPalEnvironmentStr);

        Log.d("initiatePackage", returnUrl);
        Log.d("initiatePackage", clientId);
        Log.d("initiatePackage", payPalEnvironmentStr);

        // store in checkoutconfigstore because application is sometimes null
        checkoutConfigStore = new CheckoutConfigStore(
                clientId,
                payPalEnvironment,
                returnUrl
               );
        result.success("completed");
    }

    void initialisePaypalConfig() {
        if (application == null)
            return;
        if (checkoutConfigStore == null)
            return;

        Log.d("initialisePaypalConfig", "ok");


        // Getting the new payPalNativeClient instance
        CoreConfig coreConfig = new CoreConfig(checkoutConfigStore.clientId, checkoutConfigStore.payPalEnvironment);
        payPalNativeClient = new PayPalNativeCheckoutClient(
                application,
                coreConfig,
                checkoutConfigStore.returnUrl
        );


//        final PayPalCallBackHelper payPalCallBackHelper = new PayPalCallBackHelper(this);

        payPalNativeCallBackHelper = new PayPalNativeCallBackHelper(this);

        // Setting the client with our listener
        payPalNativeClient.setListener(payPalNativeCallBackHelper);






//        PayPalCheckout.registerCallbacks(
//                approval -> {
//                    // Order successfully captured
//                    payPalCallBackHelper.onPayPalApprove(approval);
//                },
//                (shippingData, shippingAction) -> {
//                    // called when shippinginfo changes
//                    payPalCallBackHelper.onPayPalShippingChange(shippingData, shippingAction);
//                },
//                () -> {
//                    // Optional callback for when a buyer cancels the paysheet
//                    payPalCallBackHelper.onPayPalCancel();
//                },
//                errorInfo -> {
//                    // Optional error callback
//                    payPalCallBackHelper.onPayPalError(errorInfo);
//                });
        initialisedPaypalConfig = true;
    }

    private void makeOrder(@NonNull MethodCall call, @NonNull Result result) {
        String orderId = call.argument("orderId");

        if (!initialisedPaypalConfig) {
            initialisePaypalConfig();
        }

        try {
            final PayPalNativeCheckoutRequest request = new PayPalNativeCheckoutRequest(orderId, null);
            payPalNativeClient.startCheckout(request);
            payPalNativeCallBackHelper.setResult(result);
        } catch (Exception e) {
            Toast.makeText(application, "error occurred while the checkout is processing", Toast.LENGTH_SHORT).show();
            result.error("completed", e.getMessage(), e.getMessage());
        }
    }

    private void captureMoney(@NonNull MethodCall call, @NonNull Result result) {
        String orderId = call.argument("orderId");
        String url;
        if (checkoutConfigStore.payPalEnvironment == Environment.SANDBOX) {
            url = "https://api-m.sandbox.paypal.com";
        } else {
            url = "https://api.paypal.com";
        }

        // Intance to log the url and data retrofit
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
//        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        // Adding token to request
        // Adding token to request
        String authToken = "A21AAKaKiOonVMOry0H8WHXB1V02cROsvUkRqbgnnLt9X-KMr90wNArP1UOSsQNA0n_8nhjCHovorIu2O4kqWcpnU3iLNhASQ";
        String paypal_request_id = "819980cc-cc7e-4800-81dd-0901f1961101";
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Authorization", "Bearer " + authToken)
                                .header("PayPal-Request-Id",  paypal_request_id)
                                .header("Content-Type", "application/json")
                                .method(original.method(), original.body());
                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                })
                .addInterceptor(interceptor)
                .build();
        
     

    

        // Build retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Building and instance of interface api
        ICaptureOrderApi apiService = retrofit.create(ICaptureOrderApi.class);




        // Doing the request to capture the money
        Call<Void> retrofitCall = apiService.captureOrder(orderId);

        retrofitCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                   if(response.isSuccessful() ) {
                       payPalNativeCallBackHelper.firedOnCapturedCallBack(result);
                   } else {
                       Gson gson = new Gson();
                       ErrorInterceptor message=gson.fromJson(response.errorBody().charStream(),ErrorInterceptor.class);
                       Log.d("onResponse", message.getMessage());
                   }



                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.d("onFailure",t.getMessage());
//                    result.error("error", t.getMessage(), t.getMessage());
                }
            });
        }

}
