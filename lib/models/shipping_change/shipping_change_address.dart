import '../map_helper.dart';

class ShippingChangeAddress extends MapHelper {
  String adminArea1 = "";
  String adminArea2 = "";
  String postalCode = "";
  String countryCode = "";

  ShippingChangeAddress fromJson(Map<String, dynamic> data) {
    setMap(data);
    adminArea1 = getString("adminArea1");
    adminArea2 = getString("adminArea2");
    postalCode = getString("postalCode");
    countryCode = getString("countryCode");
    return this;
  }
}
