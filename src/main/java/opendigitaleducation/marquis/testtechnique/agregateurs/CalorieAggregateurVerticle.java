package opendigitaleducation.marquis.testtechnique.agregateurs;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import opendigitaleducation.marquis.testtechnique.AlimentDTO;

public class CalorieAggregateurVerticle extends AbstractVerticle {
  private EventBus eventBus;
  @Override
  public void start(Promise<Void> startPromise) {
    eventBus = vertx.eventBus();
    eventBus.consumer("aliment.calorie.get.ByName", message -> {
      CalorieRequestDTO calorieRequestDTO = ((JsonObject) message.body()).mapTo(CalorieRequestDTO.class);
      CalorieAgregateur calorieAgregateur= CaloriesAggregateurFactory.GetAggegateur(calorieRequestDTO.getCalorieAgregateurType());
      eventBus.request("aliment.mongo.get.ByName", calorieRequestDTO.getSearchedAlimentName(), mongoResult -> {
        if (mongoResult.succeeded()) {
          AlimentDTO alimentDTO= ((JsonObject)mongoResult.result().body()).mapTo(AlimentDTO.class);
          double calories= calorieAgregateur != null ? calorieAgregateur.AgregeCalorie(alimentDTO) : 0;
          message.reply(calories);
        }
      });
    });
    startPromise.complete();
  }
}
