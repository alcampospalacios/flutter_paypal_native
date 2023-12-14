class FPayPalApprovalData {
  String? payerId;
  String? orderId;

  FPayPalApprovalData({
    this.payerId,
    this.orderId,
  });

  FPayPalApprovalData.fromJson(Map<String, dynamic> json) {
    payerId = json['payerId'];
    orderId = json['orderId'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = {};
    data['payerId'] = payerId;
    data['orderId'] = orderId;

    return data;
  }

  // toString
  @override
  String toString() {
    return 'FPayPalApprovalData{payerId: $payerId, orderId: $orderId}';
  }
}
