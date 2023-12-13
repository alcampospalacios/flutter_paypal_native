package com.alcampospalacios.paypal.paypal_native_checkout;

import androidx.annotation.NonNull;




import android.app.Application;
import android.util.Log;
import android.widget.Toast;





import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutClient;
import com.paypal.android.paypalnativepayments.PayPalNativeCheckoutRequest;
import com.paypal.android.corepayments.CoreConfig;
import com.paypal.android.corepayments.Environment;

import com.alcampospalacios.paypal.paypal_native_checkout.models.PayPalNativeCallBackHelper;
import com.alcampospalacios.paypal.paypal_native_checkout.models.CheckoutConfigStore;
import com.alcampospalacios.paypal.paypal_native_checkout.models.EnvironmentHelper;
//import com.alcampospalacios.paypal.paypal_native_checkout.models.PayPalCallBackHelper;


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
  private MethodChannel channel;

  
// val config = CoreConfig("CLIENT_ID", environment = Environment.SANDBOX)

// val cardClient = CardClient(config)

    private Application application;
    private CheckoutConfigStore checkoutConfigStore;
//    private PayPalCallBackHelper payPalCallBackHelper;
    boolean initialisedPaypalConfig = false;

    PayPalNativeCheckoutClient payPalNativeClient;


  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "paypal_native_checkout");
    channel.setMethodCallHandler(this);
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

        // Creating a instance of listener to receive callback fron the listener client
        PayPalNativeCallBackHelper payPalNativeCallBackHelper = new PayPalNativeCallBackHelper();

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
        if (!initialisedPaypalConfig) {
            initialisePaypalConfig();
        }

        String orderId = call.argument("orderId");

        try {
            final PayPalNativeCheckoutRequest request = new PayPalNativeCheckoutRequest(orderId, null);
            payPalNativeClient.startCheckout(request);
            result.success("completed");
        } catch (Exception e) {
            Toast.makeText(application, "error occurred while the checkout is processing", Toast.LENGTH_SHORT).show();

            result.error("completed", e.getMessage(), e.getMessage());
        }
    }
}
