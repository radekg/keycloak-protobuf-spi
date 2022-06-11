package keycloak.protobuf.spi.eventlistener;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class Configuration {

    final static Logger logger = LoggerFactory.getLogger(Configuration.class);

    private Config moduleConfig;

    public Configuration() {
        this.moduleConfig = ConfigFactory.empty();
    }
    public Configuration(Config sourceConfig) {
        this.moduleConfig = sourceConfig;
    }

    public Configuration resolve() {
        Config root;
        String maybeLocation = System.getenv("KEYCLOAK_PROTOBUF_SPI_CONFIG_FILE");
        if (maybeLocation != null && !maybeLocation.isEmpty()) {
            try {
                root = ConfigFactory.parseFile(new File(maybeLocation)).resolve();
            } catch (Exception ex) {
                logger.error(String.format("Error while resolving configuration using named file. Reason: %s", ex));
                root = ConfigFactory.load().resolve();
            }
        } else {
            logger.info("Using default resolution");
            root = ConfigFactory.load(this.getClass().getClassLoader(), "reference.conf").resolve();
        }
        moduleConfig = moduleConfig
                .withFallback(root.getConfig("keycloak.protobuf.spi.event-listener"))
                .resolve();
        return this;
    }

    public String endpointHost() {
        return moduleConfig.getString("endpoint.host");
    }

    public int endpointPort() {
        return moduleConfig.getInt("endpoint.port");
    }

    public boolean tlsEnabled() {
        return moduleConfig.getBoolean("tls.enabled");
    }

    public String tlsTrustedCertsFilePath() {
        return moduleConfig.getString("tls.trusted-certs-file-path");
    }

    public String tlsCertFilePath() {
        return moduleConfig.getString("tls.cert-file-path");
    }

    public String tlsKeyFilePath() {
        return moduleConfig.getString("tls.key-file-path");
    }

    public String tlsAuthority() {
        return moduleConfig.getString("tls.authority");
    }

}
