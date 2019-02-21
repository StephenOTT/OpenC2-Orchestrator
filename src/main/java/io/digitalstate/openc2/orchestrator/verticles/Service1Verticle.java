package io.digitalstate.openc2.orchestrator.verticles;

import io.digitalstate.openc2.orchestrator.AppConfiguration;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Service1Verticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(Service1Verticle.class);

    @Autowired
    private AppConfiguration configuration;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        logger.info("Starting Service 1 Verticle");

        EventBus bus = vertx.eventBus();

        bus.consumer("service1-stop").handler(m->{
           vertx.undeploy(deploymentID(), res ->{
               if (res.succeeded()){
                   logger.info("Service 1 has stopped");
               } else {
                   logger.error("Unable to stop Service 1", res.cause());
               }
           });
        });

        startFuture.complete();
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        logger.info("Stopping Service 1 Verticle");
        stopFuture.complete();
    }
}
