import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutter_paypal_native_method_channel.dart';

abstract class PaypalNativeCheckoutPlatform extends PlatformInterface {
  /// Constructs a PaypalNativeCheckoutPlatform.
  PaypalNativeCheckoutPlatform() : super(token: _token);

  static final Object _token = Object();

  static PaypalNativeCheckoutPlatform _instance = MethodChannelPaypalNativeCheckout();

  /// The default instance of [PaypalNativeCheckoutPlatform] to use.
  ///
  /// Defaults to [MethodChannelPaypalNativeCheckout].
  static PaypalNativeCheckoutPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [PaypalNativeCheckoutPlatform] when
  /// they register themselves.
  static set instance(PaypalNativeCheckoutPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
