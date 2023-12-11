
import 'paypal_native_checkout_platform_interface.dart';

class PaypalNativeCheckout {
  Future<String?> getPlatformVersion() {
    return PaypalNativeCheckoutPlatform.instance.getPlatformVersion();
  }
}
