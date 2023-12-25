import 'package:flutter_test/flutter_test.dart';
import 'package:paypal_native_checkout/flutter_paypal_native.dart';
import 'package:paypal_native_checkout/paypal_native_checkout_platform_interface.dart';
import 'package:paypal_native_checkout/paypal_native_checkout_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockPaypalNativeCheckoutPlatform with MockPlatformInterfaceMixin implements PaypalNativeCheckoutPlatform {
  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final PaypalNativeCheckoutPlatform initialPlatform = PaypalNativeCheckoutPlatform.instance;

  test('$MethodChannelPaypalNativeCheckout is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelPaypalNativeCheckout>());
  });

  test('getPlatformVersion', () async {
    FlutterPaypalNative paypalNativeCheckoutPlugin = FlutterPaypalNative();
    MockPaypalNativeCheckoutPlatform fakePlatform = MockPaypalNativeCheckoutPlatform();
    PaypalNativeCheckoutPlatform.instance = fakePlatform;

    expect(await paypalNativeCheckoutPlugin.getPlatformVersion(), '42');
  });
}
