package guru.qa.niffler.config;

public interface Config {

  static Config getInstance() {
    return LocalConfig.INSTANCE;
  }

  String frontUrl();

  String authUrl();

  String spendUrl();

  String ghUrl();

  String gatewayUrl();

  String userdataUrl();

  String currencyUrl();

}
