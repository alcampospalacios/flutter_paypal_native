import 'dart:convert';
import 'fpn_purchase_unit.dart';

PaypalOrder paypalOrderFromJson(String str) =>
    PaypalOrder.fromJson(json.decode(str));

String paypalOrderToJson(PaypalOrder data) => json.encode(data.toJson());

class PaypalOrder {
  final String id;
  final String intent;
  final String status;
  final List<PurchaseUnit> purchaseUnits;

  const PaypalOrder({
    this.id = '',
    this.intent = '',
    this.status = '',
    this.purchaseUnits = const [],
  });

  factory PaypalOrder.fromJson(Map<String, dynamic> json) => PaypalOrder(
        id: json["id"] ?? '',
        intent: json["intent"] ?? '',
        status: json["status"] ?? '',
        purchaseUnits: List<PurchaseUnit>.from(
            json["purchase_units"].map((x) => PurchaseUnit.fromJson(x))),
      );

  Map<String, dynamic> toJson() => {
        "id": id,
        "intent": intent,
        "status": status,
        "purchase_units":
            List<dynamic>.from(purchaseUnits.map((x) => x.toJson()))
      };
}
