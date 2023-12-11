package com.alcampospalacios.paypal.paypal_native_checkout;

import androidx.annotation.NonNull;




import android.app.Application;
import android.util.Log;
import android.widget.Toast;


import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.OrderRequest;
import com.paypal.checkout.order.PurchaseUnit;
import com.alcampospalacios.paypal.paypal_native_checkout.models.CheckoutConfigStore;
import com.alcampospalacios.paypal.paypal_native_checkout.models.CurrencyCodeHelper;
import com.alcampospalacios.paypal.paypal_native_checkout.models.EnvironmentHelper;
import com.alcampospalacios.paypal.paypal_native_checkout.models.PayPalCallBackHelper;
import com.alcampospalacios.paypal.paypal_native_checkout.models.PurchaseUnitC;
import com.alcampospalacios.paypal.paypal_native_checkout.models.PurchaseUnitHelper;
import com.alcampospalacios.paypal.paypal_native_checkout.models.UserActionHelper;

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
    private PayPalCallBackHelper payPalCallBackHelper;
    boolean initialisedPaypalConfig = false;


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
        String currencyStr = call.argument("currency");
        String userActionStr = call.argument("userAction");

        CurrencyCode currency = (new CurrencyCodeHelper()).getEnumFromString(currencyStr);
        UserAction userAction = (new UserActionHelper()).getEnumFromString(userActionStr);
        Environment payPalEnvironment = (new EnvironmentHelper()).getEnumFromString(payPalEnvironmentStr);

        // store in checkoutconfigstore because application is sometimes null
        checkoutConfigStore = new CheckoutConfigStore(
                clientId,
                payPalEnvironment,
                returnUrl,
                currency,
                userAction);
        result.success("completed");
    }

    void initialisePaypalConfig() {
        if (application == null)
            return;
        if (checkoutConfigStore == null)
            return;

        PayPalCheckout.setConfig(new CheckoutConfig(
                application,
                checkoutConfigStore.clientId,
                checkoutConfigStore.payPalEnvironment,
                checkoutConfigStore.currency,
                checkoutConfigStore.userAction,
                checkoutConfigStore.returnUrl));
        payPalCallBackHelper = new PayPalCallBackHelper(this);
        PayPalCheckout.registerCallbacks(
                approval -> {
                    // Order successfully captured
                    payPalCallBackHelper.onPayPalApprove(approval);
                },
                (shippingData, shippingAction) -> {
                    // called when shippinginfo changes
                    payPalCallBackHelper.onPayPalShippingChange(shippingData, shippingAction);
                },
                () -> {
                    // Optional callback for when a buyer cancels the paysheet
                    payPalCallBackHelper.onPayPalCancel();
                },
                errorInfo -> {
                    // Optional error callback
                    payPalCallBackHelper.onPayPalError(errorInfo);
                });
        initialisedPaypalConfig = true;
    }

    private void makeOrder(@NonNull MethodCall call, @NonNull Result result) {
        if (!initialisedPaypalConfig) {
            initialisePaypalConfig();
        }

        String purchaseUnitsStr = call.argument("purchaseUnits");
        String userActionStr = call.argument("userAction");
        UserAction userAction = (new UserActionHelper()).getEnumFromString(userActionStr);

        List<PurchaseUnitC> purchaseUnitsC = (new PurchaseUnitHelper())
                .convertJsonToArrayList(purchaseUnitsStr);
        CurrencyCodeHelper helper = (new CurrencyCodeHelper());

        try {
            PayPalCheckout.startCheckout(
                    createOrderActions -> {
                        ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
                        for (PurchaseUnitC purchaseUnit : purchaseUnitsC) {
                            CurrencyCode currency = helper.getEnumFromString(
                                    purchaseUnit.getCurrency());
                            purchaseUnits.add(
                                    new PurchaseUnit.Builder()
                                            .amount(
                                                    new Amount.Builder()
                                                            .currencyCode(currency)
                                                            .value(purchaseUnit.getPrice())
                                                            .build())
                                            .referenceId(purchaseUnit.getReferenceID())
                                            .build());
                        }
                        OrderRequest order = new OrderRequest(
                                OrderIntent.CAPTURE,
                                new AppContext.Builder()
                                        .userAction(userAction)
                                        .build(),
                                purchaseUnits);
                        createOrderActions.create(order, orderId -> {
                        });
                    });
            result.success("completed");
        } catch (Exception e) {
            Toast.makeText(application, "error occured while getting order", Toast.LENGTH_SHORT).show();

            result.error("completed", e.getMessage(), e.getMessage());
        }
    }
}
