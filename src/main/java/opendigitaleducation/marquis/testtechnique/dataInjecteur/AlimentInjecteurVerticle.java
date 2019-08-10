package opendigitaleducation.marquis.testtechnique.dataInjecteur;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;

public class AlimentInjecteurVerticle extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) {
    EventBusStarter startInjection = new AlimentInjecteursStarter();
    EventBusStarter alimentSaver = new MongoAlimentSaver();
    Future<Object> startEventBusConsumerDataInjection = Future.future(promise -> startInjection.StartEventBusConsumer(vertx, promise));
    Future<Object> startEventBusConsumerAlimentSaver = Future.future(promise -> alimentSaver.StartEventBusConsumer(vertx, promise));
    CompositeFuture.all(startEventBusConsumerAlimentSaver, startEventBusConsumerDataInjection).setHandler(
      startPromises -> {
        if (startPromises.succeeded())
          startPromise.complete();
      });
  }
}

