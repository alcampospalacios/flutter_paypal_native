// ignore_for_file: unused_field

import 'dart:convert';
import 'dart:developer';

import 'package:flutter/services.dart';

import 'models/approval/approval_data.dart';
import 'models/custom/environment.dart';
import 'models/custom/error_info.dart';
import 'models/custom/order_callback.dart';
import 'flutter_paypal_native_platform_interface.dart';
import 'models/order/fpn_paypal_order.dart';

class FlutterPaypalNative {
  static FlutterPaypalNative? _instance;
  bool _initiated = false;
  final _methodChannel = const MethodChannel('flutter_paypal_native');

  //Default empty callback
  FPayPalCallback _callback = FPayPalCallback.initial();

  // If this parameter is active you need pass accessToken and the payment is captured automatically from client
  bool autoCaptureFromClient = false;

  ///Use PaypalNativeCheckout.instance instead
  FlutterPaypalNative();

  Future<String?> getPlatformVersion() {
    return PaypalNativeCheckoutPlatform.instance.getPlatformVersion();
  }

  /// This function setup the callback functions after `startPayOrder` is fired. This will be a way to
  /// get retroalimantation from native code.
  ///
  /// * [callback] contains a FPayPalCallback in the way:
  /// ``` Dart
  ///  FPayPalCallback(
  ///      onCancel: () {},
  ///      onSuccess: () {},
  ///      onError: (error) {},
  ///      onCapturedMoney: () {},
  ///    );
  /// ```
  /// You need to know that `onCapturedMoney` function only will be fired if `autoCaptureFromClient` is `true`
  void setPayPalCallback({
    required FPayPalCallback callback,
  }) {
    _callback = callback;
  }

  /// This function initialize the native package the android.
  ///
  /// * [returnUrl] This parameter is a deep link to return to app, normally has this structure `appid://paypalpay` where "appid" can be "package name".
  /// * [clientID] This parameter  contains the "clientId from paypal dashboard panel, we recommended use `Enviroment` and not hardcode data.
  /// * [autoCaptureFromClient] This parameter decide if the capture of money after being approved is executed by the client, if is true `accessToken` and `paypalRequestId` on [`startPayOrder`] must be different from null or empty.
  Future<FlutterPaypalNative> init({
    required String returnUrl,
    required String clientID,
    bool autoCaptureFromClient = false,
    required FPayPalEnvironment payPalEnvironment,
  }) async {
    _methodChannel.setMethodCallHandler(_handleMethod);
    _initiated = true;

    this.autoCaptureFromClient = autoCaptureFromClient;

    Map<String, dynamic> data = {
      "returnUrl": returnUrl,
      "clientId": clientID,
      "autoCaptureFromClient": autoCaptureFromClient,
      "payPalEnvironment": FPayPalEnvironmentHelper.convertFromEnumToString(
        payPalEnvironment,
      ),
    };
    await _methodChannel.invokeMethod<String>(
      'FlutterPaypal#initiate',
      data,
    );
    return instance;
  }

  /// get instance of FlutterPaypal
  static FlutterPaypalNative get instance {
    //check if null and set
    _instance ??= FlutterPaypalNative();
    return _instance!;
  }

  /// Starts an order of payment `@throws Exception if init()` was not called before this function.
  /// * [orderId] This param is getted from the response of create order from paypal api, is the reference to order
  /// * [accessToken] This parameter contains the access token genereated by the api of paypal, we recommended that this data being passed encrypted from server. If `autoCaptureFromClient` is `true` this param is required.
  /// * [paypalRequestId] This parameter is generated by the server using some time of unique identifier like `uuid`, in the create order request from your backend you must sent to create the order. If `autoCaptureFromClient` is `true` this param is required.
  /// * [paypalOrder] This parameter is an entity created by the response of [create order from paypal api]. If `autoCaptureFromClient` is `true` this param is required. You can use:
  /// ``` Dart
  ///   PaypalOrder.fromJson(jsonDataCreateOrderResponse);
  ///
  /// ```
  ///
  Future<String> startPayOrder({
    required String orderId,
    String accessToken = '',
    String paypalRequestId = '',
    PaypalOrder? paypalOrder,
  }) async {
    if (!_initiated) {
      throw Exception(
        "You must initiate package first. call FlutterPaypal.instance.init()",
      );
    }

    assert(autoCaptureFromClient && accessToken != '' && paypalRequestId != '' && paypalOrder != null,
        "The accessToken, paypalRequestId and paypalOrder can't be empty or null if autoCaptureFromClient is true");

    Map<String, String> data = {
      "orderId": orderId,
      "accessToken": accessToken,
      "paypalRequestId": paypalRequestId,
      if (paypalOrder != null) "jsonData": json.encode(paypalOrder.toJson()),
    };

    final result = await _methodChannel.invokeMethod<String>('FlutterPaypal#makeOrder', data);
    return result ?? "";
  }

  // This function handle the type of method fired from native code
  Future<void> _handleMethod(MethodCall call) async {
    //(call.arguments.cast<String, dynamic>())
    if (call.method == 'FlutterPaypal#onSuccess') {
      _onPayPalOrderSuccess(call.arguments.cast<String, dynamic>());
    } else if (call.method == 'FlutterPaypal#onCancel') {
      _onCancelPayPalOrder();
    } else if (call.method == 'FlutterPaypal#onError') {
      _onPayPalError(call.arguments.cast<String, dynamic>());
    } else if (call.method == 'FlutterPaypal#onCapture') {
      _onPayPalPaymentCaptured();
    }
  }

  Future<void> _onPayPalOrderSuccess(
    Map<String, dynamic> data,
  ) async {
    String aData = data['approvalData'] ?? "";
    FPayPalApprovalData success = FPayPalApprovalData();

    try {
      success = FPayPalApprovalData.fromJson(
        jsonDecode(aData),
      );

      Map<String, String> data = {
        "orderId": success.orderId ?? "",
      };

      // If auto capture from client is active invoke the capture of the money from native code
      if (autoCaptureFromClient) {
        await _methodChannel.invokeMethod<String>('FlutterPaypal#captureMoney', data);
      }
    } catch (e) {
      log(e.toString());
    }
    _callback.onSuccess(success);
  }

  void _onCancelPayPalOrder() {
    if (_callback.onCancel != null) {
      _callback.onCancel!();
    }
  }

  void _onPayPalError(Map<String, dynamic> data) {
    FPayPalErrorInfo error = FPayPalErrorInfo();
    try {
      error = error.fromJson(data);
    } catch (e) {
      log(e.toString());
    }

    if (_callback.onError != null) {
      _callback.onError!(error);
    }
  }

  void _onPayPalPaymentCaptured() {
    log('The payment of the order was captured successfully');
    if (_callback.onCapturedMoney != null) {
      _callback.onCapturedMoney!();
    }
  }
}
