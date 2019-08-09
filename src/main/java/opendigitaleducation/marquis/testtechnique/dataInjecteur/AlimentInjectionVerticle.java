package opendigitaleducation.marquis.testtechnique.dataInjecteur;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;

public class AlimentInjectionVerticle extends AbstractVerticle {
  @SuppressWarnings("unchecked")  //Je ne gere pas les erreurs pour le test
  @Override
  public void start(Promise<Void> startPromise) {
    EventBusStarter startInjection = new DataInjectionStarter();
    EventBusStarter alimentSaver = new AlimentSaver();
    Future<Object> startEventBusConsumerDataInjection = Future.future(promise -> startInjection.StartEventBusConsumer(vertx, promise));
    Future<Object> startEventBusConsumerAlimentSaver = Future.future(promise -> alimentSaver.StartEventBusConsumer(vertx, promise));
    CompositeFuture.all(startEventBusConsumerAlimentSaver, startEventBusConsumerDataInjection).setHandler(
      startPromises -> {
        if (startPromises.succeeded())
          startPromise.complete();
      });
  }
}

