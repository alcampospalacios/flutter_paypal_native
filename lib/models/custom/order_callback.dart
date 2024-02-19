import '../approval/approval_data.dart';
import 'error_info.dart';

class FPayPalCallback {
  void Function(FPayPalApprovalData success) onSuccess;
  void Function(FPayPalErrorInfo error)? onError;
  void Function()? onCancel;
  void Function()? onCapturedMoney;

  String onSuccessMessage;
  String onErrorMessage;
  String onCancelMessage;
  String onCapturedMessage;

  FPayPalCallback({
    required this.onSuccess,
    this.onError,
    this.onCancel,
    this.onCapturedMoney,
    this.onSuccessMessage = "Order approved",
    this.onErrorMessage = "Error creating order",
    this.onCancelMessage = "Order cancelled",
    this.onCapturedMessage = "Order captured, money sended to merchant",
  });

  factory FPayPalCallback.initial() => FPayPalCallback(
        onCancel: () {},
        onSuccess: (_) {},
        onError: (_) {},
        onCapturedMoney: () {},
      );
}
