package com.ibm.dscoc;

import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.sun.jersey.spi.container.servlet.ServletContainer;


public class StartGeoServer {

    static final int DEFAULT_PORT_STOP = 8099;
    static final String STOP_COMMAND = "stop";
    private static final int DEFAULT_PORT_START = 8099;
    private static final Logger LOGGER = LoggerFactory.getLogger(StartGeoServer.class);
    private final int startPort;
    private final int stopPort;

    public StartGeoServer() {
        this(DEFAULT_PORT_START, DEFAULT_PORT_STOP);
    }

    public StartGeoServer(int port) {
        this.startPort = port;
        this.stopPort = port;
    }

    public StartGeoServer(int startPort, int stopPort) {
        this.startPort = startPort;
        this.stopPort = stopPort;
    }

    static public void stop() {
        stop(DEFAULT_PORT_STOP);
    }

    /**
     * Stops a running web application powered with Jetty.
     *
     * @param stopPort TCP port used to communicate with Jetty.
     */
    static public void stop(Integer stopPort) {
        try {
            Socket s = new Socket(InetAddress.getLocalHost().getHostName(), stopPort);
            LOGGER.info("Jetty stopping...");
            s.setSoLinger(false, 0);
            OutputStream out = s.getOutputStream();
            out.write(("stop\r\n").getBytes());
            out.flush();
            s.close();
        } catch (ConnectException e) {
            LOGGER.info("Jetty not running!");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void main(String[] args) throws Exception {
        StartGeoServer server = null;
        if (args.length == 2) {
            server = new StartGeoServer(Integer.valueOf(args[0]), Integer.valueOf(args[1]));
        } else if (args.length == 1) {
        	server = new StartGeoServer(Integer.valueOf(args[0]));
        } else {
            server = new StartGeoServer();
        }

        server.start();
    }

    public void start() throws Exception {
    	ServletHolder sh = new ServletHolder(ServletContainer.class);
        sh.setInitParameter("com.sun.jersey.config.property.resourceConfigClass", "com.sun.jersey.api.core.PackagesResourceConfig");
        sh.setInitParameter("com.sun.jersey.config.property.packages", "com.ibm.dscoc.restful.resource");
        sh.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");

    	Server server = new Server(startPort);

        ServletContextHandler context = new ServletContextHandler(server, "/", ServletContextHandler.SESSIONS);
        context.addServlet(sh, "/*");

        server.start();

        LOGGER.info("Jetty server started");
        LOGGER.debug("Jetty web server port: {}", startPort);
        LOGGER.debug("Port to stop Jetty with the 'stop' operation: {}", stopPort);

        Monitor monitor = new Monitor(stopPort, new Server[]{server});
        monitor.start();

        server.join();

        LOGGER.info("Jetty server exited");
    }

}
