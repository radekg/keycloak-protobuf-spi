package keycloak.protobuf.spi.eventlistener.utils;

import java.io.IOException;
import java.net.ServerSocket;

public class Utils {

    public static int getAvailablePort() {
        int port = 0;
        try {
            ServerSocket s = new ServerSocket(0);
            port = s.getLocalPort();
            s.close();
        } catch (IOException ex) {
            System.err.println(String.format("Failed to get free port. Reason: %s", ex));
        }
        return port;
    }

}
