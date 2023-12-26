import 'package:flutter_test/flutter_test.dart';
import 'package:flutter_paypal_native/flutter_paypal_native.dart';
import 'package:flutter_paypal_native/flutter_paypal_native_platform_interface.dart';
import 'package:flutter_paypal_native/flutter_paypal_native_method_channel.dart';
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
