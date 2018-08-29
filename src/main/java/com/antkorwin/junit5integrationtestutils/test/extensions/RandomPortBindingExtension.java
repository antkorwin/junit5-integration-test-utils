package com.antkorwin.junit5integrationtestutils.test.extensions;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

import org.springframework.util.SocketUtils;

/**
 * Created on 29.08.2018.
 *
 * @author Korovin Anatoliy
 */
public class RandomPortBindingExtension implements Extension, BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        int port = SocketUtils.findAvailableTcpPort();
        System.out.println("\n binding -> server.port = "+port+"\n");
        System.setProperty("server.port", String.valueOf(port));
    }
}
