package opendigitaleducation.marquis.testtechnique;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import opendigitaleducation.marquis.testtechnique.dal.MongoDbVerticle;
import opendigitaleducation.marquis.testtechnique.injecteur.AlimentInjecteursStarterVertice;
import opendigitaleducation.marquis.testtechnique.injecteur.InjectionOptionDTO;
import opendigitaleducation.marquis.testtechnique.injecteur.InjectionSourceType;
import opendigitaleducation.marquis.testtechnique.injecteur.cliquAL.xml.AlimentReaderVerticle;
import opendigitaleducation.marquis.testtechnique.injecteur.cliquAL.xml.CompositionReaderVerticle;

public class MainInjecteurLaucherVerticle extends AbstractVerticle {
  private EventBus eventBus;

  @Override
  public void start(Promise<Void> startPromise) {
    eventBus = vertx.eventBus();
    vertx.deployVerticle(CompositionReaderVerticle.class, new DeploymentOptions().setInstances(4),
      dep1 -> vertx.deployVerticle(new MongoDbVerticle(),
        dep2 -> vertx.deployVerticle(new AlimentInjecteursStarterVertice(),
          dep3 -> vertx.deployVerticle(new AlimentReaderVerticle(), dep4 ->
          {
            InjectionOptionDTO injectionOptionDTO = new InjectionOptionDTO();
            injectionOptionDTO.setInjectionSource(InjectionSourceType.CiquALXml);
            eventBus.send("aliment.data.inject.start", JsonObject.mapFrom(injectionOptionDTO));
            startPromise.complete();
          }))));
  }
}

