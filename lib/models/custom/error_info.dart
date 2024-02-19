import '../map_helper.dart';

/// This class contains values of response in case of the order is approved succefully
///
/// * [code] This parameter represent the code error return from paypal native sdk error `payPalSDKError`
/// * [error] This parameter represent the error return from paypal native sdk error `payPalSDKError`
/// * [errorDescription] This parameter represent the description error return from paypal native sdk error `payPalSDKError`
class FPayPalErrorInfo extends MapHelper {
  String code = "";
  String error = "";
  String errorDescription = "";

  FPayPalErrorInfo fromJson(Map<String, dynamic> data) {
    setMap(data);

    code = getString("code");
    error = getString("error");
    errorDescription = getString("errorDescription");
    return this;
  }
}
