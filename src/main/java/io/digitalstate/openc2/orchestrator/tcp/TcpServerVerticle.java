package io.digitalstate.openc2.orchestrator.tcp;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.*;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.SharedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpServerVerticle extends AbstractVerticle {

    private static Logger logger = LoggerFactory.getLogger(TcpServerVerticle.class);

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        logger.info("Starting Tcp Server Verticle");
        startFuture.complete();
        logger.info("Tcp Server Verticle has started");

        //@TODO add these into Verticle configs
        int port = 4321;
        String host = "localhost";

        NetServerOptions options = new NetServerOptions();

        setupTcpServer(options, host, port);

    }

    private void setupTcpServer(NetServerOptions tcpServerOptions, String host, int port){
        NetServer tcpServer = vertx.createNetServer(tcpServerOptions);

        SharedData sd = vertx.sharedData();

        tcpServer.connectHandler(socket -> {
            logger.info("TCP Client has Connected: " + socket.remoteAddress());

            String clientHandlerId = socket.writeHandlerID();

            //@TODO Refactor to have a proper ID for the Key
            sd.<String, String>getAsyncMap("tcp-clients", res -> {
                if (res.succeeded()) {
                    AsyncMap<String, String> map = res.result();

                    String key = socket.remoteAddress().host() + ":"+ socket.remoteAddress().port();

                    map.put(key , clientHandlerId, resPut -> {
                        if (resPut.succeeded()) {
                            logger.info("Netsocket was added to Shared Data for client: " + key);
                        } else {
                            logger.error("Failed to add Netsocket to Shared Data for client: " + key, resPut.cause());
                        }
                    });
                } else {
                    logger.error("Unable to get the tcp-clients AsyncMap from Shared Data", res.cause());
                }
            });

            logger.info("Attempting to send client a message...");
            sendMessageToTcpClient(clientHandlerId);


        });

        tcpServer.listen(port, host, res->{
            if (res.succeeded()) {
                logger.info("TCP Server is now listening!");
            } else {
                logger.error("TCP Server Failed to bind!", res.cause());
            }
        });

    }

    private void sendMessageToTcpClient(String clientHandlerId){
        JsonObject message = new JsonObject();
        message.put("dog", 111);
        message.put("cat", "frank");

        JsonObject tcpMessage = new JsonObject();
        tcpMessage.put("address", "address1");
        tcpMessage.put("message", message );

        vertx.eventBus().publish(clientHandlerId, tcpMessage.toBuffer());
    }
}
