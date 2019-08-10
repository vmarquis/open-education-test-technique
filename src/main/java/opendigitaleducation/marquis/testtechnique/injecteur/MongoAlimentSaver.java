package opendigitaleducation.marquis.testtechnique.injecteur;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.BulkOperation;
import io.vertx.ext.mongo.MongoClient;
import opendigitaleducation.marquis.testtechnique.ProjectConfig;
import java.util.ArrayList;

public class MongoAlimentSaver implements EventBusStarter {
  private MongoClient mongoClient;
  private ArrayList<BulkOperation> alimentsBulkUpsetToDo = new ArrayList<>();
  private ProjectConfig projectConfig;
  private EventBus eventBus;

  @Override
  public void StartEventBusConsumer(Vertx vertx, Promise<Object> eventBusStartpromise) {
    ConfigRetriever retriever = ConfigRetriever.create(vertx);
    eventBus = vertx.eventBus();
    retriever.getConfig(ar -> {
      projectConfig = new ProjectConfig().MapFrom(ar.result());
      mongoClient = MongoClient.createShared(vertx, new JsonObject()
        .put("connection_string", projectConfig.MongoDbConnectionString)
        .put("db_name", projectConfig.MongoDbAlimentDb));
      consumeBulckSaveAlimentMessage();
      vertx.setPeriodic(1000, id -> BulckSaveAlimentToMongo());
      eventBusStartpromise.complete();
    });
  }

  private void BulckSaveAlimentToMongo() {
    if(alimentsBulkUpsetToDo.size()>0)
    {
      mongoClient.bulkWrite(projectConfig.MongoDbCollection, alimentsBulkUpsetToDo, res->{
        if(res.failed())
          System.out.println("Mongo Save fail" + res.cause());
          else
          eventBus.send("aliment.mongo.inject.saved",res.result().getUpserts().size());
      });
      alimentsBulkUpsetToDo =new ArrayList<>();
    }
  }

  private void consumeBulckSaveAlimentMessage() {
    eventBus.consumer("aliment.mongo.inject.bulk.save", saveMessage -> {
      JsonObject alimentJson = ((JsonObject) saveMessage.body());
      alimentJson.remove("_id");
      alimentsBulkUpsetToDo.add(BulkOperation.createReplace(new JsonObject()
        .put("code", alimentJson.getInteger("code")),alimentJson,true));
    });
  }
}
