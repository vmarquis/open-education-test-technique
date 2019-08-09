package opendigitaleducation.marquis.testtechnique;

import io.vertx.core.json.JsonObject;

public class ProjectConfig {
  public String MongoDbConnectionString;
  public String MongoDbCollection;
  public String MongoDbAlimentDb;

  public ProjectConfig MapFrom(JsonObject jsonObject) {
    MongoDbConnectionString=jsonObject.getString("MongoDbConnectionString");
    MongoDbCollection=jsonObject.getString("MongoDbCollection");
    MongoDbAlimentDb=jsonObject.getString("MongoDbAlimentDb");
    return this;
  }
}
