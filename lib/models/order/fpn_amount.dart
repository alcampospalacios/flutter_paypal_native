import 'fpn_breakdown.dart';

class Amount {
  final String currencyCode;
  final String value;
  final Breakdown breakdown;

  Amount({
    required this.currencyCode,
    required this.value,
    required this.breakdown,
  });

  // initial
  factory Amount.initial() => Amount(
        currencyCode: '',
        value: '',
        breakdown: Breakdown.initial(),
      );

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
