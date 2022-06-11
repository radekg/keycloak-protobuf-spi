package keycloak.protobuf.spi.eventlistener;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestConfiguration {

    @Test
    void configurationWithFallbackTest() {

        Config underlying = ConfigFactory.empty()
                .withValue("endpoint.host", ConfigValueFactory.fromAnyRef("unit-test-host"))
                .withValue("endpoint.port", ConfigValueFactory.fromAnyRef(10000))
                .withValue("tls.enabled", ConfigValueFactory.fromAnyRef(true))
                .withValue("tls.trusted-certs-file-path", ConfigValueFactory.fromAnyRef("/etc/expected-trusted-cert.pem"))
                .withValue("tls.cert-file-path", ConfigValueFactory.fromAnyRef("/etc/expected-cert.pem"))
                .withValue("tls.key-file-path", ConfigValueFactory.fromAnyRef("/etc/expected-key.pem"))
                .withValue("tls.authority", ConfigValueFactory.fromAnyRef("unit-test-host"));

        Configuration cfg = new Configuration(underlying).resolve();

        assertTrue(cfg.tlsEnabled());

        assertEquals("unit-test-host", cfg.endpointHost());
        assertEquals(10000, cfg.endpointPort());

        assertEquals("/etc/expected-trusted-cert.pem", cfg.tlsTrustedCertsFilePath());
        assertEquals("/etc/expected-cert.pem", cfg.tlsCertFilePath());
        assertEquals("/etc/expected-key.pem", cfg.tlsKeyFilePath());
        assertEquals("unit-test-host", cfg.tlsAuthority());
    }

}
