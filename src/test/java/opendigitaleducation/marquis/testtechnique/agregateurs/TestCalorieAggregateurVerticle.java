package opendigitaleducation.marquis.testtechnique.agregateurs;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import opendigitaleducation.marquis.testtechnique.AlimentDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(VertxExtension.class)
class TestCalorieAggregateurVerticle {
  private EventBus eventBus;
  @Test
  void CheckGetCalorie(Vertx vertx, VertxTestContext testContext) {
    eventBus=vertx.eventBus();
    vertx.deployVerticle(new CalorieAggregateurVerticle(), id->
    {
      EventBus eventBus = vertx.eventBus();
      CalorieRequestDTO calorieRequestDTO =new CalorieRequestDTO();
      calorieRequestDTO.setCalorieAgregateurType(CalorieAgregateurType.Simple);
      MockMongo();
      calorieRequestDTO.setSearchedAlimentName("Champignon à la grecque");
      eventBus.request("aliment.calorie.get.ByName", JsonObject.mapFrom(calorieRequestDTO), busReply -> testContext.verify(() -> {
        {
          assertThat(busReply.succeeded()).isTrue();
          double  calories=(double) busReply.result().body();
         // assertThat(calories).isEqualTo(56.07); //Fail test
          assertThat(calories).isEqualTo(48.59);
          testContext.completeNow();
        }
      }));
    });
  }

  private void MockMongo() {
    MessageConsumer<String> consumer = eventBus.consumer("aliment.mongo.get.ByName");
    consumer.handler(message -> {
      AlimentDTO alimentDTO = new AlimentDTO();
      alimentDTO.setLipides(3.55);
      alimentDTO.setProteines(2.08);
      alimentDTO.setGlucides(2.08);
      alimentDTO.setGlucides(2.08);
      alimentDTO.setCode(25605);
      alimentDTO.setName("Champignon à la grecque");
      message.reply(JsonObject.mapFrom(alimentDTO));
    });
  }
}
