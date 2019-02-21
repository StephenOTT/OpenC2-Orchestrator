package io.digitalstate.openc2.orchestrator;

import io.digitalstate.openc2.orchestrator.verticles.Service1Verticle;
import io.digitalstate.openc2.orchestrator.verticles.Service2Verticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApplicationMainVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationMainVerticle.class);

    @Autowired
    private AppConfiguration configuration;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        //@TODO Set the Vertx System Prop to use the SLF Logging factory

        logger.info("Starting Vertx Main Verticle");
        startFuture.complete();

        StartService1();
        StartService2();

        logger.info("Service 1 will be stopped after 25 seconds");
        vertx.setTimer(25000,act-> vertx.eventBus().publish("service1-stop",""));
    }

    private void StartService1() {
        DeploymentOptions options = new DeploymentOptions();
        vertx.deployVerticle(Service1Verticle.class, options, res ->{
            if (res.succeeded()){
                logger.info("Service 1 has deployed");
            } else {
                logger.error("Service 1 failed to deploy", res.cause());
            }
        });
    }

    private void StartService2() {
        DeploymentOptions options = new DeploymentOptions();
        vertx.deployVerticle(Service2Verticle.class, options, res->{
            if (res.succeeded()){
                logger.info("Service 2 has deployed");
            } else {
                logger.error("Service 2 failed to deploy");
            }
        });
    }
}
