package guru.qa.niffler.config;

import javax.annotation.Nonnull;

public interface Config {

  static @Nonnull Config getInstance() {
    return "docker".equals(System.getProperty("test.env"))
            ? DockerConfig.INSTANCE
            : LocalConfig.INSTANCE;
  }


  String frontUrl();

  String authUrl();

  String authJdbcUrl();

  String spendUrl();

  String spendJdbcUrl();

  String ghUrl();

  String gatewayUrl();

  String userdataUrl();

  String userdataJdbcUrl();

  String currencyUrl();


  String currencyJdbcUrl();

}
