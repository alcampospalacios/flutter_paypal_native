package com.alcampospalacios.paypal.flutter_paypal_native;


import androidx.annotation.NonNull;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
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

import com.alcampospalacios.paypal.flutter_paypal_native.models.CaptureOrderConfigStore;
import com.alcampospalacios.paypal_dialog.BottomSheetLibrary;
import com.alcampospalacios.paypal_dialog.models.PaypalOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutClient;
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutRequest;
import com.paypal.android.corepayments.CoreConfig;
import com.paypal.android.corepayments.Environment;

import com.alcampospalacios.paypal.flutter_paypal_native.models.PayPalNativeCallBackHelper;
import com.alcampospalacios.paypal.flutter_paypal_native.models.CheckoutConfigStore;
import com.alcampospalacios.paypal.flutter_paypal_native.models.EnvironmentHelper;



import org.jetbrains.annotations.NotNull;
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

/** PaypalNativePlugin */
public class PaypalNativePlugin extends FlutterIO
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
    private CaptureOrderConfigStore  captureOrderConfigStore;
//    private PayPalCallBackHelper payPalCallBackHelper;
    boolean initialisedPaypalConfig = false;

    PayPalNativeCheckoutClient payPalNativeClient;

    // Creating a instance of listener to receive callback from the listener client and dialog confirmation payment
    PayPalNativeCallBackHelper payPalNativeCallBackHelper;
    PayPalCallBackDialogHelper payPalCallBackDialogHelper;

    private Activity _activity;


  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_paypal_native");
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
        _activity = binding.getActivity();
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
        Boolean autoCaptureFromClient = call.argument("autoCaptureFromClient");
        String payPalEnvironmentStr = call.argument("payPalEnvironment");

        Environment payPalEnvironment = (new EnvironmentHelper()).getEnumFromString(payPalEnvironmentStr);

        // store data in global config
        checkoutConfigStore = new CheckoutConfigStore(
                clientId,
                payPalEnvironment,
                returnUrl,
                autoCaptureFromClient
               );
        result.success("completed");
    }

    void initialisePaypalConfig() {
        if (application == null)
            return;
        if (checkoutConfigStore == null)
            return;

        // Getting the new payPalNativeClient instance
        CoreConfig coreConfig = new CoreConfig(checkoutConfigStore.clientId, checkoutConfigStore.payPalEnvironment);
        payPalNativeClient = new PayPalNativeCheckoutClient(
                application,
                coreConfig,
                checkoutConfigStore.returnUrl
        );

        // Setting the client with listener to get the callback
        payPalNativeCallBackHelper = new PayPalNativeCallBackHelper(this, checkoutConfigStore);
        payPalCallBackDialogHelper = new PayPalCallBackDialogHelper(this);
        payPalNativeClient.setListener(payPalNativeCallBackHelper);

        // To check if is initialized
        initialisedPaypalConfig = true;
    }

    private void makeOrder(@NonNull MethodCall call, @NonNull Result result) {
        String orderId = call.argument("orderId");
        String accessToken = call.argument("accessToken");
        String paypalRequestId = call.argument("paypalRequestId");
        String jsonData = call.argument("jsonData");

        captureOrderConfigStore = new CaptureOrderConfigStore(accessToken, paypalRequestId, jsonData);

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

        PaypalOrder paypalOrder;

        // Transforming from json data to PaypalOrderClass
        ObjectMapper objectMapper = new ObjectMapper();
        try {
             paypalOrder = objectMapper.readValue(captureOrderConfigStore.jsonData, PaypalOrder.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            paypalOrder =  PaypalOrder.getEmptyOrder();
        }



        BottomSheetLibrary.showBottomSheet(
                payPalCallBackDialogHelper,
                _activity,
                orderId,
                paypalOrder,
                captureOrderConfigStore.accessToken,
                captureOrderConfigStore.paypalRequestId,
                url);

        payPalCallBackDialogHelper.setResult(result);
        }

}
