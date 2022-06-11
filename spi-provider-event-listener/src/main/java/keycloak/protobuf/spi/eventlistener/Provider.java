package keycloak.protobuf.spi.eventlistener;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.AuthDetails;
import org.keycloak.events.admin.OperationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import keycloak.protobuf.spi.eventlistener.proto.KeycloakEventServiceGrpc;
import keycloak.protobuf.spi.eventlistener.proto.Definitions;

public class Provider implements EventListenerProvider {

    final static Logger logger = LoggerFactory.getLogger(Provider.class);
    private static AuthDetails defaultAuthDetails = new AuthDetails();

    private KeycloakEventServiceGrpc.KeycloakEventServiceBlockingStub stub;

    public Provider(KeycloakEventServiceGrpc.KeycloakEventServiceBlockingStub stub) {
        this.stub = stub;
    }

    public void onEvent(Event event) {
        Definitions.Event protoEvent = Definitions.Event.newBuilder()
                .setTime(event.getTime())
                .setType(eventTypeToProto(event.getType()))
                .setRealmId(Serializer.toNullableString(event.getRealmId()))
                .setClientId(Serializer.toNullableString(event.getClientId()))
                .setUserId(Serializer.toNullableString(event.getUserId()))
                .setSessionId(Serializer.toNullableString(event.getSessionId()))
                .setIpAddress(Serializer.toNullableString(event.getIpAddress()))
                .setError(Serializer.toNullableString(event.getError()))
                .putAllDetails(Serializer.handleMaybeNullMap(event.getDetails()))
                .build();

        Definitions.EventRequest request = Definitions.EventRequest.newBuilder()
                .setEvent(protoEvent)
                .build();

        stub.onEvent(request);
    }

    public void onEvent(AdminEvent adminEvent, boolean b) {

        AuthDetails givenDetails = defaultAuthDetails;
        if (adminEvent.getAuthDetails() != null) {
            givenDetails = adminEvent.getAuthDetails();
        }

        Definitions.AuthDetails authDetails = Definitions.AuthDetails.newBuilder()
                .setRealmId(Serializer.toNullableString(givenDetails.getRealmId()))
                .setClientId(Serializer.toNullableString(givenDetails.getClientId()))
                .setUserId(Serializer.toNullableString(givenDetails.getUserId()))
                .setIpAddress(Serializer.toNullableString(givenDetails.getIpAddress()))
                .build();

        Definitions.AdminEvent protoAdminEvent = Definitions.AdminEvent.newBuilder()
                .setTime(adminEvent.getTime())
                .setRealmId(Serializer.toNullableString(adminEvent.getRealmId()))
                .setAuthDetails(authDetails)
                .setResourceType(Serializer.toNullableString(adminEvent.getResourceTypeAsString()))
                .setOperationType(operationTypeToProto(adminEvent.getOperationType()))
                .setResourcePath(Serializer.toNullableString(adminEvent.getResourcePath()))
                .setRepresentation(Serializer.toNullableString(adminEvent.getRepresentation()))
                .setError(Serializer.toNullableString(adminEvent.getError()))
                .build();

        Definitions.AdminEventRequest request = Definitions.AdminEventRequest.newBuilder()
                .setFlag(b)
                .setAdminEvent(protoAdminEvent)
                .build();

        stub.onAdminEvent(request);
    }

    public void close() {

    }

    private Definitions.Event.EventType eventTypeToProto(EventType t) {
        Definitions.Event.EventType rt = Definitions.Event.EventType.UNKNOWN;
        try {
            if (t == null) {
                logger.warn(String.format("eventTypeToProto: t is null '%s', returning UNKNOWN.", t.name()));
                return rt;
            }
            rt = Definitions.Event.EventType.valueOf(t.name());
        } catch (IllegalArgumentException ex) {
            logger.warn(String.format("eventTypeToProto: Unknown value '%s', returning UNKNOWN.", t.name()));
        }
        return rt;
    }

    private Definitions.AdminEvent.OperationType operationTypeToProto(OperationType t) {
        Definitions.AdminEvent.OperationType rt = Definitions.AdminEvent.OperationType.UNKNOWN;
        try {
            if (t == null) {
                logger.warn(String.format("operationTypeToProto: t is null '%s', returning UNKNOWN.", t.name()));
                return rt;
            }
            rt = Definitions.AdminEvent.OperationType.valueOf(t.name());
        } catch (IllegalArgumentException ex) {
            logger.warn(String.format("operationTypeToProto: Unknown vault '%s', returning UNKNOWN.", t.name()));
        }
        return rt;
    }
}
