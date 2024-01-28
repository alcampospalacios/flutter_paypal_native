class ItemTotal {
  final String currencyCode;
  final String value;

  const ItemTotal({
    required this.currencyCode,
    required this.value,
  });

  // initial
  factory ItemTotal.initial() => const ItemTotal(
        currencyCode: '',
        value: '0.0',
      );

  factory ItemTotal.fromJson(Map<String, dynamic> json) => ItemTotal(
        currencyCode: json["currency_code"] ?? '',
        value: json["value"] ?? '',
      );

  Map<String, dynamic> toJson() => {
        "currency_code": currencyCode,
        "value": value,
      };
}
