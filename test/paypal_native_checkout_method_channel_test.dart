import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:paypal_native_checkout/paypal_native_checkout_method_channel.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  MethodChannelPaypalNativeCheckout platform = MethodChannelPaypalNativeCheckout();
  const MethodChannel channel = MethodChannel('paypal_native_checkout');

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(
      channel,
      (MethodCall methodCall) async {
        return '42';
      },
    );
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger.setMockMethodCallHandler(channel, null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
