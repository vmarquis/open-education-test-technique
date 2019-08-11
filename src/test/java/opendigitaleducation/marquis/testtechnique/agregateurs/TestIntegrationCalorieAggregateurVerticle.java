package opendigitaleducation.marquis.testtechnique.agregateurs;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import opendigitaleducation.marquis.testtechnique.AlimentDTO;
import opendigitaleducation.marquis.testtechnique.ProjectConfig;
import opendigitaleducation.marquis.testtechnique.dal.MongoDbVerticle;
import opendigitaleducation.marquis.testtechnique.injecteur.cliquAL.xml.CompositionReaderVerticle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(VertxExtension.class)
class TestIntegrationCalorieAggregateurVerticle {
  private EventBus eventBus;
  private MongoClient mongoClient;
  private ProjectConfig projectConfig;
  @Test
  void CheckGetCalorie(Vertx vertx, VertxTestContext testContext) {
    eventBus=vertx.eventBus();
    vertx.deployVerticle(new CalorieAggregateurVerticle(),
      dep1 -> vertx.deployVerticle(new MongoDbVerticle(),
        dep2-> TestGetCalorie(testContext)));
    }

  private void TestGetCalorie(VertxTestContext testContext) {
    AlimentDTO alimentDTO=new AlimentDTO();
    alimentDTO.setCode(10).setName("Chocolat").setLipides(1).setProteines(1).setGlucides(1);
    eventBus.send("aliment.mongo.inject.bulk.save", JsonObject.mapFrom(alimentDTO));
    eventBus.consumer("aliment.mongo.inject.saved",savedMessage->{
      CalorieRequestDTO calorieRequestDTO=new CalorieRequestDTO();
      calorieRequestDTO.setCalorieAgregateurType(CalorieAgregateurType.Simple).setSearchedAlimentName("Chocolat");
      eventBus.request("aliment.calorie.get.ByName",JsonObject.mapFrom(calorieRequestDTO) ,calorieMessage->{
        double calorie=(double)calorieMessage.result().body();
        //assertThat(calorie).isEqualTo(50); //Fail tests
        assertThat(calorie).isEqualTo(17);
        testContext.completeNow();
      });
    });
  }
}
