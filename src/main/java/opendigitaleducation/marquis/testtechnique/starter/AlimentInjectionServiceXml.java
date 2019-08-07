package opendigitaleducation.marquis.testtechnique.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Verticle;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

import java.util.function.Supplier;

public class AlimentInjectionServiceXml extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    EventBus eb = vertx.eventBus();

    eb.consumer("aliment.data.xmlInjecteur.get", message -> {
      message.reply("toto");

    });
    startPromise.complete();

  }
}
