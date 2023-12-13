enum FPayPalEnvironment {
  /// Live environment is used for production.
  live,

  /// Sandbox environment is used for development and testing.
  sandbox,

 }

class FPayPalEnvironmentHelper {
  static const Map<FPayPalEnvironment, String> codes = {
    FPayPalEnvironment.live: "live",
    FPayPalEnvironment.sandbox: "sandbox",
  };

  //convert enum  to string
  static String convertFromEnumToString(FPayPalEnvironment payPalEnvironment) {
    if (codes[payPalEnvironment] != null) {
      return codes[payPalEnvironment]!;
    }
    return codes[FPayPalEnvironment.sandbox]!;
  }
}
