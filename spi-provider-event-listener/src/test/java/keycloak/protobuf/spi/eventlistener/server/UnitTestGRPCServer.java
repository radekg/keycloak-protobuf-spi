package keycloak.protobuf.spi.eventlistener.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.ClientAuth;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;
import keycloak.protobuf.spi.eventlistener.utils.Utils;

import java.io.File;
import java.io.IOException;

public class UnitTestGRPCServer {

    private Server server;

    public UnitTestGRPCServer(UnitTestGRPCService service) {
        int freePort = Utils.getAvailablePort();
        if (freePort == 0) {
            throw new RuntimeException("Failed to bind unit test GRPC server");
        }
        server = ServerBuilder.forPort(freePort)
                .addService(service)
                .build();
    }

    public UnitTestGRPCServer(UnitTestGRPCService service, String certFilePath, String keyFilePath) throws Exception {
        this(service, certFilePath, keyFilePath, "");
    }

    public UnitTestGRPCServer(UnitTestGRPCService service, String certFilePath, String keyFilePath, String trustedClientsFile) throws Exception {
        int freePort = Utils.getAvailablePort();
        if (freePort == 0) {
            throw new RuntimeException("Failed to bind unit test GRPC server");
        }

        SslContextBuilder sslBuilder = GrpcSslContexts
                .forServer(new File(certFilePath), new File(keyFilePath));
        if (trustedClientsFile != null && !trustedClientsFile.isEmpty()) {
            sslBuilder
                    .trustManager(new File(trustedClientsFile))
                    .clientAuth(ClientAuth.REQUIRE);
        }

        server = NettyServerBuilder
                .forPort(freePort)
                .addService(service)
                .sslContext(sslBuilder.build())
                .build();
    }

    public void start() throws IOException {
        server.start();
    }

    public int getServerPort() {
        return server.getPort();
    }

    public void stop() {
        server.shutdown();
    }

}
