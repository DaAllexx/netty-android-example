package de.tunebitfm.nettyexample.client;

import java.net.URI;
import java.net.URISyntaxException;

import lombok.Getter;

public class WebsocketEndpointAddress {

    @Getter private String address;

    @Getter private URI uri;

    @Getter private String host;

    @Getter private int port;

    private boolean encrypted = false;

    public WebsocketEndpointAddress(String address) throws URISyntaxException {

        this.address = address;
        this.uri = new URI(this.address);

        String scheme = uri.getScheme() == null ? "ws" : uri.getScheme();
        this.host = uri.getHost() == null ? "127.0.0.1" : uri.getHost();

        // Set port
        if(this.uri.getPort() == -1) {
            if("ws".equalsIgnoreCase(scheme)) {
                this.port = 80;
            } else if("wss".equalsIgnoreCase(scheme)) {
                this.port = 443;
            } else {
                this.port = -1;
            }
        } else {
            this.port = uri.getPort();
        }

        // Check for protocol
        if(!"ws".equalsIgnoreCase(scheme) && !"wss".equalsIgnoreCase(scheme)) {
            throw new IllegalStateException("Non valid protocol scheme: " + scheme);
        }

        // Check for existence of encryption
        this.encrypted = "wss".equalsIgnoreCase(scheme);
    }

    public boolean isEncrypted() {
        return this.encrypted;
    }

}
