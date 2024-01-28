import 'fpn_item_total.dart';

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

  // initial
  factory Item.initial() => Item(
        name: '',
        unitAmount: ItemTotal.initial(),
        quantity: '',
        description: '',
      );

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
