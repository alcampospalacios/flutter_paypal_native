class Payee {
  final String emailAddress;
  final String merchantId;

  const Payee({
    required this.emailAddress,
    required this.merchantId,
  });

  // initial
  factory Payee.initial() => const Payee(
        emailAddress: '',
        merchantId: '',
      );

  factory Payee.fromJson(Map<String, dynamic> json) => Payee(
        emailAddress: json["email_address"] ?? '',
        merchantId: json["merchant_id"] ?? '',
      );

  Map<String, dynamic> toJson() => {
        "email_address": emailAddress,
        "merchant_id": merchantId,
      };
}
