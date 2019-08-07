package opendigitaleducation.marquis.testtechnique.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.eventbus.EventBus;

import java.util.function.Supplier;

public class AlimentInjectionServiceXml extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
        startPromise.complete();

  }
}
