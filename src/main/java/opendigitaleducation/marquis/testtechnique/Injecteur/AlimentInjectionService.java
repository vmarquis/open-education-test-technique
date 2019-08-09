package opendigitaleducation.marquis.testtechnique.Injecteur;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import opendigitaleducation.marquis.testtechnique.Injecteur.InjectionOptionDTO;

public class AlimentInjectionService extends AbstractVerticle {
  @Override
  public void start(Promise<Void> startPromise) {
    EventBus eb = vertx.eventBus();
    eb.consumer("aliment.data.inject.start", message -> {
      InjectionOptionDTO injectionOptionDTO = ((JsonObject) message.body()).mapTo(InjectionOptionDTO.class);
      // Je laisse pour montrer que l'on peut utiliser plusieurs injecteurs
      //noinspection SwitchStatementWithTooFewBranches
      switch (injectionOptionDTO.InjectionSource) {
        case CiquALXml:
          eb.send("aliment.data.inject.CiquALXml.start", message.body());
      }
    });
    startPromise.complete();
  }
}
