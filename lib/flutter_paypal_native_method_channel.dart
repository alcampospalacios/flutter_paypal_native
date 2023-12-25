import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'flutter_paypal_native_platform_interface.dart';

/// An implementation of [PaypalNativeCheckoutPlatform] that uses method channels.
class MethodChannelPaypalNativeCheckout extends PaypalNativeCheckoutPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutter_paypal_native');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
