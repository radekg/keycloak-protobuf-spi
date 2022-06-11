package keycloak.protobuf.spi.eventlistener.utils.tls;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

public class TlsUtils {

    public static KeyPair generateKeyPair(String algo, int keysize) throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance(algo);
        generator.initialize(keysize);
        return generator.generateKeyPair();
    }

}
