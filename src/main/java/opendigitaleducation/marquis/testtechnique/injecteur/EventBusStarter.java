package opendigitaleducation.marquis.testtechnique.injecteur;

import io.vertx.core.Promise;
import io.vertx.core.Vertx;

interface EventBusStarter {
  void StartEventBusConsumer(Vertx vertx, Promise<Object> EventBusStartpromise) ;
}
