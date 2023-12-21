import '../approval/approval_data.dart';
import '../shipping_change/shipping_info.dart';
import 'error_info.dart';

class FPayPalOrderCallback {
  void Function(FPayPalApprovalData success) onSuccess;
  void Function(FPayPalErrorInfo error)? onError;
  void Function()? onCancel;
  void Function()? onCapturedMoney;
  void Function(FPayPalShippingChangeInfo shipping)? onShippingChange;

  String onSuccessMessage;
  String onErrorMessage;
  String onCancelMessage;
  String onCapturedMessage;
  String onShippingChangeMessage;

  FPayPalOrderCallback({
    required this.onSuccess,
    this.onError,
    this.onCancel,
    this.onShippingChange,
    this.onCapturedMoney,
    this.onSuccessMessage = "Order approved",
    this.onErrorMessage = "Error creating order",
    this.onCancelMessage = "Order cancelled",
    this.onShippingChangeMessage = "Shipping change",
    this.onCapturedMessage = "Order captured, money sended to merchant",
  });
}
