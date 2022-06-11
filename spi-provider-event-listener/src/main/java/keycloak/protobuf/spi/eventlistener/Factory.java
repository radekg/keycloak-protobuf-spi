package keycloak.protobuf.spi.eventlistener;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NegotiationType;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import keycloak.protobuf.spi.eventlistener.proto.KeycloakEventServiceGrpc;

import javax.net.ssl.SSLException;
import java.io.File;

public class Factory implements EventListenerProviderFactory {

    final static Logger logger = LoggerFactory.getLogger(Factory.class);
    Configuration config;

    ManagedChannel channel = null;
    KeycloakEventServiceGrpc.KeycloakEventServiceBlockingStub stub = null;

    public Factory() {
        config = new Configuration().resolve();
    }

    public Factory(Configuration sourceConfig) {
        config = sourceConfig;
    }

    public EventListenerProvider create(KeycloakSession keycloakSession) {
        if (stub == null) {
            if (configureGRPCChannel()) {
                logger.info(String.format("%s: creating new blocking stub", getId()));
                stub = KeycloakEventServiceGrpc.newBlockingStub(channel);
            }
        }
        return new Provider(stub);
    }

    public void init(Config.Scope scope) {
        logger.info(String.format("%s: init called, creating the channel", getId()));

    }

    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
        logger.info(String.format("%s: post-init called", getId()));
    }

    public void close() {
        logger.info(String.format("%s: close called", getId()));
        if (channel != null) {
            channel.shutdownNow();
        }
    }

    public String getId() {
        return "keycloak-protobuf-spi-event-listener";
    }

    private boolean configureGRPCChannel() {
        if (config.tlsEnabled()) {
            logger.info(String.format("%s: configuring channel with TLS", getId()));
            try {

                logger.info(String.format("%s: configuring TLS trust manager", getId()));
                SslContextBuilder sslBuilder = GrpcSslContexts
                        .forClient()
                        .trustManager(new File(config.tlsTrustedCertsFilePath()));

                if (!config.tlsCertFilePath().isEmpty() && !config.tlsKeyFilePath().isEmpty()) {
                    logger.info(String.format("%s: TLS cert and key file given, setting up key manager", getId()));
                    sslBuilder.keyManager(new File(config.tlsCertFilePath()), new File(config.tlsKeyFilePath()));
                }

                NettyChannelBuilder builder = NettyChannelBuilder
                        .forAddress(config.endpointHost(), config.endpointPort())
                        .negotiationType(NegotiationType.TLS)
                        .sslContext(sslBuilder.build());

                if (!config.tlsAuthority().isEmpty()) {
                    logger.info(String.format("%s: overriding authority to '%s'", getId(), config.tlsAuthority()));
                    builder.overrideAuthority(config.tlsAuthority());
                }

                logger.info(String.format("%s: building the channel", getId()));
                channel = builder.build();
                logger.info(String.format("%s: channel built", getId()));

            } catch (SSLException ex) {
                logger.error(String.format("Failed configuring TLS for the GRPC channel. Reason: %s", ex));
            }
        } else {
            logger.info(String.format("%s: configuring channel without TLS", getId()));
            channel = ManagedChannelBuilder
                    .forAddress(config.endpointHost(), config.endpointPort())
                    .enableRetry()
                    .usePlaintext()
                    .build();
        }
        if (channel == null) {
            logger.error("GRPC channel not configured, returning null as provider. Provider configuration failed.");
            return false;
        }
        return true;
    }

}
