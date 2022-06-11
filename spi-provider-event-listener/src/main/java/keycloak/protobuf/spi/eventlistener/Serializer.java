package keycloak.protobuf.spi.eventlistener;

import keycloak.protobuf.spi.shared.proto.Shared;

import java.util.HashMap;
import java.util.Map;

public class Serializer {

    public static Shared.NullableString toNullableString(String input) {
        if (input == null) {
            return Shared.NullableString
                    .newBuilder()
                    .setNoValue(Shared.Empty.getDefaultInstance())
                    .build();
        }
        return Shared.NullableString
                .newBuilder()
                .setValue(input)
                .build();
    }

    public static Map<String, String> handleMaybeNullMap(Map<String, String> input) {
        if (input == null) {
            return new HashMap<>();
        }
        return input;
    }

}
