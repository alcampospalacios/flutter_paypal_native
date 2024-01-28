import 'fpn_item_total.dart';

class Breakdown {
  final ItemTotal itemTotal;
  final ItemTotal shipping;
  final ItemTotal taxTotal;

  Breakdown({
    this.itemTotal = const ItemTotal(
      currencyCode: '',
      value: '0.0',
    ),
    this.shipping = const ItemTotal(
      currencyCode: '',
      value: '0.0',
    ),
    this.taxTotal = const ItemTotal(
      currencyCode: '',
      value: '0.0',
    ),
  });

  // initial
  factory Breakdown.initial() => Breakdown(
        itemTotal: ItemTotal.initial(),
      );

  factory Breakdown.fromJson(Map<String, dynamic> json) => Breakdown(
        itemTotal: ItemTotal.fromJson(json["item_total"]),
        shipping: json["shipping"] != null ? ItemTotal.fromJson(json["shipping"]) : ItemTotal.initial(),
        taxTotal: json["tax_total"] != null ? ItemTotal.fromJson(json["tax_total"]) : ItemTotal.initial(),
      );

  Map<String, dynamic> toJson() => {
        "item_total": itemTotal.toJson(),
        "shipping": shipping.toJson(),
        "tax_total": taxTotal.toJson(),
      };
}
