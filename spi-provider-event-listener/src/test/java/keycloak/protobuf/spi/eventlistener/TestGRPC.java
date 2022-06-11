package keycloak.protobuf.spi.eventlistener;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import keycloak.protobuf.spi.eventlistener.server.UnitTestGRPCService;
import keycloak.protobuf.spi.eventlistener.utils.tls.TlsUtils;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.AuthDetails;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;
import keycloak.protobuf.spi.shared.proto.Shared;
import keycloak.protobuf.spi.eventlistener.proto.Definitions;
import keycloak.protobuf.spi.eventlistener.server.UnitTestGRPCServer;
import keycloak.protobuf.spi.eventlistener.utils.AsyncUtil;
import keycloak.protobuf.spi.eventlistener.utils.tls.CertBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.security.KeyPair;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

public class TestGRPC {

    @Test
    void testSimpleServer() throws Exception {

        AdminEvent testAdminEvent = getTestAdminEvent();
        Event testEvent = getTestEvent();

        ConcurrentLinkedQueue<Definitions.AdminEvent> receivedAdminEvents = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Definitions.Event> receivedEvents = new ConcurrentLinkedQueue<>();

        Consumer<Definitions.AdminEvent> consumer1 = new Consumer<Definitions.AdminEvent>() {
            @Override
            public void accept(Definitions.AdminEvent adminEvent) {
                receivedAdminEvents.add(adminEvent);
            }
        };

        Consumer<Definitions.Event> consumer2 = new Consumer<Definitions.Event>() {
            @Override
            public void accept(Definitions.Event event) {
                receivedEvents.add(event);
            }
        };

        UnitTestGRPCService service = new UnitTestGRPCService(consumer1, consumer2);
        UnitTestGRPCServer server = new UnitTestGRPCServer(service);
        server.start();

        int serverPort = server.getServerPort();

        Config underlying = ConfigFactory.empty()
                .withValue("endpoint.host", ConfigValueFactory.fromAnyRef("127.0.0.1"))
                .withValue("endpoint.port", ConfigValueFactory.fromAnyRef(serverPort));

        Factory f = new Factory(new Configuration(underlying).resolve());
        EventListenerProvider provider = f.create(null);

        provider.onEvent(testAdminEvent, true);

        AsyncUtil.eventually(() -> {
            Definitions.AdminEvent receivedAdminEvent = receivedAdminEvents.poll();
            assertNotNull(receivedAdminEvent);

            assertEquals(testAdminEvent.getOperationType().name(), receivedAdminEvent.getOperationType().name());

            assertEquals(testAdminEvent.getAuthDetails().getClientId(), receivedAdminEvent.getAuthDetails().getClientId().getValue());
            assertEquals(testAdminEvent.getAuthDetails().getIpAddress(), receivedAdminEvent.getAuthDetails().getIpAddress().getValue());
            assertEquals(testAdminEvent.getAuthDetails().getRealmId(), receivedAdminEvent.getAuthDetails().getRealmId().getValue());
            assertEquals(testAdminEvent.getAuthDetails().getUserId(), receivedAdminEvent.getAuthDetails().getUserId().getValue());

            assertEquals(testAdminEvent.getRealmId(), receivedAdminEvent.getRealmId().getValue());
            assertEquals(testAdminEvent.getRepresentation(), receivedAdminEvent.getRepresentation().getValue());
            assertEquals(testAdminEvent.getResourcePath(), receivedAdminEvent.getResourcePath().getValue());
            assertEquals(testAdminEvent.getResourceTypeAsString(), receivedAdminEvent.getResourceType().getValue());
            assertEquals(Shared.Empty.getDefaultInstance(), receivedAdminEvent.getError().getNoValue());
        });

        provider.onEvent(testEvent);

        AsyncUtil.eventually(() -> {
            Definitions.Event receivedEvent = receivedEvents.poll();

            assertNotNull(receivedEvent);

            assertEquals(testEvent.getTime(), receivedEvent.getTime());
            assertEquals(testEvent.getType().name(), receivedEvent.getType().name());
            assertEquals(testEvent.getRealmId(), receivedEvent.getRealmId().getValue());
            assertEquals(testEvent.getClientId(), receivedEvent.getClientId().getValue());
            assertEquals(testEvent.getUserId(), receivedEvent.getUserId().getValue());
            assertEquals(testEvent.getSessionId(), receivedEvent.getSessionId().getValue());
            assertEquals(testEvent.getIpAddress(), receivedEvent.getIpAddress().getValue());
            assertEquals(Shared.Empty.getDefaultInstance(), receivedEvent.getError().getNoValue());
            assertTrue(testEvent.getDetails().keySet().containsAll(receivedEvent.getDetailsMap().keySet()));
        });

        server.stop();
    }

    @Test
    void testTLSServer(@TempDir Path tempDir) throws Exception {

        String sigAlg = "SHA256WithRSAEncryption";
        CertBuilder certBuilder = new CertBuilder(30, sigAlg, "localhost");

        KeyPair serverKeyPair = TlsUtils.generateKeyPair("RSA", 2048);
        X509Certificate serverCertificate = certBuilder.generate("CN=localhost", serverKeyPair);

        KeyPair clientKeyPair = TlsUtils.generateKeyPair("RSA", 2048);
        X509Certificate clientCertificate = certBuilder.generate("CN=localhost-client", clientKeyPair);

        writeObjectToPem(tempDir.resolve("server-key.pem").toAbsolutePath().toString(), serverKeyPair.getPrivate());
        writeObjectToPem(tempDir.resolve("server-cert.pem").toAbsolutePath().toString(), serverCertificate);
        writeObjectToPem(tempDir.resolve("client-key.pem").toAbsolutePath().toString(), clientKeyPair.getPrivate());
        writeObjectToPem(tempDir.resolve("client-cert.pem").toAbsolutePath().toString(), clientCertificate);

        AdminEvent testAdminEvent = getTestAdminEvent();
        Event testEvent = getTestEvent();

        ConcurrentLinkedQueue<Definitions.AdminEvent> receivedAdminEvents = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Definitions.Event> receivedEvents = new ConcurrentLinkedQueue<>();

        Consumer<Definitions.AdminEvent> consumer1 = new Consumer<Definitions.AdminEvent>() {
            @Override
            public void accept(Definitions.AdminEvent adminEvent) {
                receivedAdminEvents.add(adminEvent);
            }
        };

        Consumer<Definitions.Event> consumer2 = new Consumer<Definitions.Event>() {
            @Override
            public void accept(Definitions.Event event) {
                receivedEvents.add(event);
            }
        };

        UnitTestGRPCService service = new UnitTestGRPCService(consumer1, consumer2);
        UnitTestGRPCServer server = new UnitTestGRPCServer(service,
                tempDir.resolve("server-cert.pem").toAbsolutePath().toString(),
                tempDir.resolve("server-key.pem").toAbsolutePath().toString(),
                tempDir.resolve("client-cert.pem").toAbsolutePath().toString());
        server.start();

        int serverPort = server.getServerPort();

        Config underlying = ConfigFactory.empty()
                .withValue("endpoint.host", ConfigValueFactory.fromAnyRef("localhost"))
                .withValue("endpoint.port", ConfigValueFactory.fromAnyRef(serverPort))
                .withValue("tls.enabled", ConfigValueFactory.fromAnyRef(true))
                .withValue("tls.trusted-certs-file-path", ConfigValueFactory.fromAnyRef(tempDir.resolve("server-cert.pem").toAbsolutePath().toString()))
                .withValue("tls.cert-file-path", ConfigValueFactory.fromAnyRef(tempDir.resolve("client-cert.pem").toAbsolutePath().toString()))
                .withValue("tls.key-file-path", ConfigValueFactory.fromAnyRef(tempDir.resolve("client-key.pem").toAbsolutePath().toString()))
                .withValue("tls.authority", ConfigValueFactory.fromAnyRef("localhost"));

        Factory f = new Factory(new Configuration(underlying).resolve());
        EventListenerProvider provider = f.create(null);

        provider.onEvent(testAdminEvent, true);

        AsyncUtil.eventually(() -> {
            Definitions.AdminEvent receivedAdminEvent = receivedAdminEvents.poll();
            assertNotNull(receivedAdminEvent);

            assertEquals(testAdminEvent.getOperationType().name(), receivedAdminEvent.getOperationType().name());

            assertEquals(testAdminEvent.getAuthDetails().getClientId(), receivedAdminEvent.getAuthDetails().getClientId().getValue());
            assertEquals(testAdminEvent.getAuthDetails().getIpAddress(), receivedAdminEvent.getAuthDetails().getIpAddress().getValue());
            assertEquals(testAdminEvent.getAuthDetails().getRealmId(), receivedAdminEvent.getAuthDetails().getRealmId().getValue());
            assertEquals(testAdminEvent.getAuthDetails().getUserId(), receivedAdminEvent.getAuthDetails().getUserId().getValue());

            assertEquals(testAdminEvent.getRealmId(), receivedAdminEvent.getRealmId().getValue());
            assertEquals(testAdminEvent.getRepresentation(), receivedAdminEvent.getRepresentation().getValue());
            assertEquals(testAdminEvent.getResourcePath(), receivedAdminEvent.getResourcePath().getValue());
            assertEquals(testAdminEvent.getResourceTypeAsString(), receivedAdminEvent.getResourceType().getValue());
            assertEquals(Shared.Empty.getDefaultInstance(), receivedAdminEvent.getError().getNoValue());
        });

        provider.onEvent(testEvent);

        AsyncUtil.eventually(() -> {
            Definitions.Event receivedEvent = receivedEvents.poll();

            assertNotNull(receivedEvent);

            assertEquals(testEvent.getTime(), receivedEvent.getTime());
            assertEquals(testEvent.getType().name(), receivedEvent.getType().name());
            assertEquals(testEvent.getRealmId(), receivedEvent.getRealmId().getValue());
            assertEquals(testEvent.getClientId(), receivedEvent.getClientId().getValue());
            assertEquals(testEvent.getUserId(), receivedEvent.getUserId().getValue());
            assertEquals(testEvent.getSessionId(), receivedEvent.getSessionId().getValue());
            assertEquals(testEvent.getIpAddress(), receivedEvent.getIpAddress().getValue());
            assertEquals(Shared.Empty.getDefaultInstance(), receivedEvent.getError().getNoValue());
            assertTrue(testEvent.getDetails().keySet().containsAll(receivedEvent.getDetailsMap().keySet()));
        });

        server.stop();

    }

    private Event getTestEvent() {
        Event testEvent = new Event();
        testEvent.setTime(System.currentTimeMillis());
        testEvent.setType(EventType.CLIENT_INFO);
        testEvent.setRealmId("test-realm");
        testEvent.setClientId("test-client");
        testEvent.setUserId("test-user");
        testEvent.setSessionId("test-session-id");
        testEvent.setIpAddress("172.120.0.1");
        testEvent.setError(null);
        testEvent.setDetails(new HashMap<>());
        return testEvent;
    }

    private AdminEvent getTestAdminEvent() {
        AuthDetails testAuthDetails = new AuthDetails();
        testAuthDetails.setClientId("test-client");
        testAuthDetails.setIpAddress("172.28.0.10");
        testAuthDetails.setRealmId("test-realm");
        testAuthDetails.setUserId("test-admin-user");

        AdminEvent testAdminEvent = new AdminEvent();
        testAdminEvent.setOperationType(OperationType.ACTION);
        testAdminEvent.setAuthDetails(testAuthDetails);
        testAdminEvent.setRealmId("test-realm");
        testAdminEvent.setRepresentation("test-representation");
        testAdminEvent.setResourcePath("/a/b/c");
        testAdminEvent.setResourceType(ResourceType.AUTHORIZATION_RESOURCE);
        testAdminEvent.setError(null);

        return testAdminEvent;
    }

    private void writeObjectToPem(String path, Object input) throws IOException {
        FileWriter writer = new FileWriter(path);
        JcaPEMWriter pemWriter = new JcaPEMWriter(writer);
        pemWriter.writeObject(input);
        pemWriter.flush();
        pemWriter.close();
    }

}
