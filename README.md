# Keycloak protobuf SPI

Keycloak protobuf SPI implementations. 

## Keycloak protobuf Event Listener SPI

Keycloak event listener designed to send Keycloak events via GRPC connection to a GRCP server.

### Configuration

The SPI uses Typesafe Config for configuration. Path to the configuration file can be provided using `KEYCLOAK_PROTOBUF_SPI_CONFIG_FILE` environment variable. If this variable does not exist, configuration follows regular Typesafe Config resolution rules.

Each configuration setting can be defined using environment variables:

- `KEYCLOAK_PROTOBUF_SPI_EVENT_LISTENER_ENDPOINT_HOST`: GRPC server hostname of IP address, default `127.0.0.1`
- `KEYCLOAK_PROTOBUF_SPI_EVENT_LISTENER_ENDPOINT_PORT`: GRPC server port, default `5000`
- `KEYCLOAK_PROTOBUF_SPI_EVENT_LISTENER_TLS_ENABLED`: should the client use TLS when connecting to the server, default `false`, **use TLS in production**

When TLS is enabled, the following setting must be provided:

- `KEYCLOAK_PROTOBUF_SPI_EVENT_LISTENER_TLS_TRUSTED_CERTS_FILE_PATH`: full path to the trusted certs file, this PEM encoded file contains all server certificates used by the servers you are connecting to, default `<empty string>`

Optionally, you can provide client certificate and client key, if your server requires mTLS:

- `KEYCLOAK_PROTOBUF_SPI_EVENT_LISTENER_TLS_CERT_FILE_PATH`: full path to the cert file, default `<empty string>`
- `KEYCLOAK_PROTOBUF_SPI_EVENT_LISTENER_TLS_KEY_FILE_PATH`: full path to the key file, default `<empty string>`

Optionally, you can override the TLS authority:

- `KEYCLOAK_PROTOBUF_SPI_EVENT_LISTENER_TLS_AUTHORITY`: TLS authority, default `<empty string>`

## Install Maven

```sh
brew install maven
```

## Running tests

```sh
mvn clean test
```

## Create a release

```sh
rm release.properties && mvn release:prepare
```

## Golang SPI

Golang SPI packages are generated at the time of `mvn package`.  
To use the version at a certain commit, in your project with `go.mod` file, execute:

```sh
go get github.com/radekg/keycloak-protobuf-spi/gospi@<commit-sha>
```
