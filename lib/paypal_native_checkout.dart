// ignore_for_file: unused_field

import 'dart:convert';
import 'dart:developer';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'models/approval/approval_data.dart';
import 'models/custom/environment.dart';
import 'models/custom/error_info.dart';
import 'models/custom/order_callback.dart';
import 'models/custom/purchase_unit.dart';
import 'models/shipping_change/shipping_info.dart';
import 'paypal_native_checkout_platform_interface.dart';

class PaypalNativeCheckout {
  static PaypalNativeCheckout? _instance;
  bool _initiated = false;
  final _methodChannel = const MethodChannel('paypal_native_checkout');

  List<FPayPalPurchaseUnit> purchaseUnits = [];
  //default callback does nothing
  FPayPalOrderCallback _callback = FPayPalOrderCallback(
    onCancel: () {},
    onSuccess: (_) {},
    onError: (_) {},
    onShippingChange: (_) {},
    onCapturedMoney: () {},
  );

  static bool isDebugMode = false;

  ///use PaypalNativeCheckout.instance instead
  PaypalNativeCheckout();

  Future<String?> getPlatformVersion() {
    return PaypalNativeCheckoutPlatform.instance.getPlatformVersion();
  }

  void setPayPalOrderCallback({
    required FPayPalOrderCallback callback,
  }) {
    _callback = callback;
  }

  Future<PaypalNativeCheckout> init({
    //the return url. This is probably your appid://paypalpay
    required String returnUrl,
    //your client id from paypal developer
    required String clientID,
    //which environmanet would you like to use
    required FPayPalEnvironment payPalEnvironment,
  }) async {
    _methodChannel.setMethodCallHandler(_handleMethod);
    _initiated = true;

    Map<String, String> data = {
      "returnUrl": returnUrl,
      "clientId": clientID,
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

  ///get instance of FlutterPaypal
  static PaypalNativeCheckout get instance {
    //check if null and set
    _instance ??= PaypalNativeCheckout();
    return _instance!;
  }

  ///starts an order of payment
  ///@throws Exception if init()
  ///was not called before this function
  Future<String> makeOrder({
    required String orderId,
  }) async {
    if (!_initiated) {
      throw Exception(
        "you must initiate package first. call FlutterPaypal.instance.init()",
      );
    }

    Map<String, String> data = {
      "orderId": orderId,
    };

    final result = await _methodChannel.invokeMethod<String>('FlutterPaypal#makeOrder', data);
    return result ?? "";
  }

  // Private function that gets called by ObjC/Java
  Future<void> _handleMethod(MethodCall call) async {
    //(call.arguments.cast<String, dynamic>())
    if (call.method == 'FlutterPaypal#onSuccess') {
      _onPayPalOrderSuccess(call.arguments.cast<String, dynamic>());
    } else if (call.method == 'FlutterPaypal#onCancel') {
      _onCancelPayPalOrder();
    } else if (call.method == 'FlutterPaypal#onError') {
      _onPayPalOrderError(call.arguments.cast<String, dynamic>());
    } else if (call.method == 'FlutterPaypal#onShippingChange') {
      _onPayPalOrderShippingChange(call.arguments.cast<String, dynamic>());
    } else if (call.method == 'FlutterPaypal#onCapture') {
      _onPayPalOrderCaptured();
    }
  }

  Future<void> _onPayPalOrderSuccess(Map<String, dynamic> data) async {
    String aData = data['approvalData'] ?? "";
    FPayPalApprovalData success = FPayPalApprovalData();

    try {
      success = FPayPalApprovalData.fromJson(
        jsonDecode(aData),
      );

      Map<String, String> data = {
        "orderId": success.orderId ?? "",
      };

      log('approvalData ${success.toString()}');
      await _methodChannel.invokeMethod<String>('FlutterPaypal#captureMoney', data);
    } catch (e) {
      if (isDebugMode) debugPrint(e.toString());
    }
    _callback.onSuccess(success);
  }

  void _onCancelPayPalOrder() {
    if (_callback.onCancel != null) {
      _callback.onCancel!();
    }
  }

  void _onPayPalOrderError(Map<String, dynamic> data) {
    FPayPalErrorInfo error = FPayPalErrorInfo();
    try {
      error = error.fromJson(data);
    } catch (e) {
      if (isDebugMode) debugPrint(e.toString());
    }

    if (_callback.onError != null) {
      _callback.onError!(error);
    }
  }

  void _onPayPalOrderShippingChange(Map<String, dynamic> data) {
    FPayPalShippingChangeInfo shipping = FPayPalShippingChangeInfo();
    try {
      shipping = FPayPalShippingChangeInfo().fromJson(data);
    } catch (e) {
      if (isDebugMode) debugPrint(e.toString());
    }

    if (_callback.onShippingChange != null) {
      _callback.onShippingChange!(shipping);
    }
  }

  void _onPayPalOrderCaptured() {
    log('Order captured successfully');
    if (_callback.onCapturedMoney != null) {
      _callback.onCapturedMoney!();
    }
  }

  void removeAllPurchaseItems() {
    purchaseUnits = [];
  }
}
