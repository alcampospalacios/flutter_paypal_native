import 'fpn_amount.dart';
import 'fpn_item.dart';
import 'fpn_payee.dart';

class PurchaseUnit {
  final String referenceId;
  final Amount amount;
  final Payee payee;
  final List<Item> items;

  PurchaseUnit({
    this.referenceId = '',
    this.payee = const Payee(
      emailAddress: '',
      merchantId: '',
    ),
    required this.amount,
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
