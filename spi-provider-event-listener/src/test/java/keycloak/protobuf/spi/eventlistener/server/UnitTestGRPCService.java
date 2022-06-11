package keycloak.protobuf.spi.eventlistener.server;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import keycloak.protobuf.spi.eventlistener.proto.KeycloakEventServiceGrpc;
import keycloak.protobuf.spi.shared.proto.Shared;
import keycloak.protobuf.spi.eventlistener.proto.Definitions;

import java.util.function.Consumer;

public class UnitTestGRPCService extends KeycloakEventServiceGrpc.KeycloakEventServiceImplBase {

    final static Logger logger = LoggerFactory.getLogger(UnitTestGRPCService.class);

    private Consumer<Definitions.AdminEvent> onAdminEventConsumer;
    private Consumer<Definitions.Event> onEventConsumer;

    public UnitTestGRPCService(Consumer<Definitions.AdminEvent> onAdminEventC,
                               Consumer<Definitions.Event> onEventC) {
        this.onAdminEventConsumer = onAdminEventC;
        this.onEventConsumer = onEventC;
    }

    @Override
    public void onAdminEvent(Definitions.AdminEventRequest request, StreamObserver<Shared.Empty> responseObserver) {
        logger.info(String.format("Received admin event: %s", request.getAdminEvent()));
        onAdminEventConsumer.accept(request.getAdminEvent());
        responseObserver.onNext(Shared.Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void onEvent(Definitions.EventRequest request, StreamObserver<Shared.Empty> responseObserver) {
        logger.info(String.format("Received event: %s", request.getEvent()));
        onEventConsumer.accept(request.getEvent());
        responseObserver.onNext(Shared.Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

}
