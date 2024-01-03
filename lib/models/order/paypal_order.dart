// To parse this JSON data, do
//
//     final paypalOrder = paypalOrderFromJson(jsonString);

import 'dart:convert';

PaypalOrder paypalOrderFromJson(String str) =>
    PaypalOrder.fromJson(json.decode(str));

String paypalOrderToJson(PaypalOrder data) => json.encode(data.toJson());

class PaypalOrder {
  final String id;
  final String intent;
  final String status;
  final List<PurchaseUnit> purchaseUnits;
  final DateTime createTime;
  final List<Link> links;

  PaypalOrder({
    required this.id,
    required this.intent,
    required this.status,
    required this.purchaseUnits,
    required this.createTime,
    required this.links,
  });

  factory PaypalOrder.fromJson(Map<String, dynamic> json) => PaypalOrder(
        id: json["id"],
        intent: json["intent"],
        status: json["status"],
        purchaseUnits: List<PurchaseUnit>.from(
            json["purchase_units"].map((x) => PurchaseUnit.fromJson(x))),
        createTime: DateTime.parse(json["create_time"]),
        links: List<Link>.from(json["links"].map((x) => Link.fromJson(x))),
      );

  Map<String, dynamic> toJson() => {
        "id": id,
        "intent": intent,
        "status": status,
        "purchase_units":
            List<dynamic>.from(purchaseUnits.map((x) => x.toJson())),
        "create_time": createTime.toIso8601String(),
        "links": List<dynamic>.from(links.map((x) => x.toJson())),
      };
}

class Link {
  final String href;
  final String rel;
  final String method;

  Link({
    required this.href,
    required this.rel,
    required this.method,
  });

  factory Link.fromJson(Map<String, dynamic> json) => Link(
        href: json["href"],
        rel: json["rel"],
        method: json["method"],
      );

  Map<String, dynamic> toJson() => {
        "href": href,
        "rel": rel,
        "method": method,
      };
}

class PurchaseUnit {
  final String referenceId;
  final Amount amount;
  final Payee payee;
  final List<Item> items;

  PurchaseUnit({
    required this.referenceId,
    required this.amount,
    required this.payee,
    required this.items,
  });

  factory PurchaseUnit.fromJson(Map<String, dynamic> json) => PurchaseUnit(
        referenceId: json["reference_id"],
        amount: Amount.fromJson(json["amount"]),
        payee: Payee.fromJson(json["payee"]),
        items: List<Item>.from(json["items"].map((x) => Item.fromJson(x))),
      );

  Map<String, dynamic> toJson() => {
        "reference_id": referenceId,
        "amount": amount.toJson(),
        "payee": payee.toJson(),
        "items": List<dynamic>.from(items.map((x) => x.toJson())),
      };
}

class Amount {
  final String currencyCode;
  final String value;
  final Breakdown breakdown;

  Amount({
    required this.currencyCode,
    required this.value,
    required this.breakdown,
  });

  factory Amount.fromJson(Map<String, dynamic> json) => Amount(
        currencyCode: json["currency_code"],
        value: json["value"],
        breakdown: Breakdown.fromJson(json["breakdown"]),
      );

  Map<String, dynamic> toJson() => {
        "currency_code": currencyCode,
        "value": value,
        "breakdown": breakdown.toJson(),
      };
}

class Breakdown {
  final ItemTotal itemTotal;

  Breakdown({
    required this.itemTotal,
  });

  factory Breakdown.fromJson(Map<String, dynamic> json) => Breakdown(
        itemTotal: ItemTotal.fromJson(json["item_total"]),
      );

  Map<String, dynamic> toJson() => {
        "item_total": itemTotal.toJson(),
      };
}

class ItemTotal {
  final String currencyCode;
  final String value;

  ItemTotal({
    required this.currencyCode,
    required this.value,
  });

  factory ItemTotal.fromJson(Map<String, dynamic> json) => ItemTotal(
        currencyCode: json["currency_code"],
        value: json["value"],
      );

  Map<String, dynamic> toJson() => {
        "currency_code": currencyCode,
        "value": value,
      };
}

class Item {
  final String name;
  final ItemTotal unitAmount;
  final String quantity;
  final String description;

  Item({
    required this.name,
    required this.unitAmount,
    required this.quantity,
    required this.description,
  });

  factory Item.fromJson(Map<String, dynamic> json) => Item(
        name: json["name"],
        unitAmount: ItemTotal.fromJson(json["unit_amount"]),
        quantity: json["quantity"],
        description: json["description"],
      );

  Map<String, dynamic> toJson() => {
        "name": name,
        "unit_amount": unitAmount.toJson(),
        "quantity": quantity,
        "description": description,
      };
}

class Payee {
  final String emailAddress;
  final String merchantId;

  Payee({
    required this.emailAddress,
    required this.merchantId,
  });

  factory Payee.fromJson(Map<String, dynamic> json) => Payee(
        emailAddress: json["email_address"],
        merchantId: json["merchant_id"],
      );

  Map<String, dynamic> toJson() => {
        "email_address": emailAddress,
        "merchant_id": merchantId,
      };
}
