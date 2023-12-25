enum FPayPalEnvironment {
  /// Live environment is used for production.
  live,

  /// Sandbox environment is used for development and testing.
  sandbox,
}

/// This class contains type of enviroment if is production o testing
/// * [live] This is production environment
/// * [sanbox] This is a secure environment to make test
///
/// In the native code select url by the type of environment
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
