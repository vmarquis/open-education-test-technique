package opendigitaleducation.marquis.testtechnique;

import io.vertx.core.json.JsonObject;

public class ProjectConfig {
  public String MongoDbConnectionString;
  public String MongoDbCollection;
  public String MongoDbAlimentDb;
  public String CliqAlDirectory;
  public String CliqAlAlimFile;
  public String CliqAlCompoFile;

  public ProjectConfig MapFrom(JsonObject jsonObject) {
    MongoDbConnectionString=jsonObject.getString("MongoDbConnectionString");
    MongoDbCollection=jsonObject.getString("MongoDbCollection");
    MongoDbAlimentDb=jsonObject.getString("MongoDbAlimentDb");
    CliqAlDirectory=jsonObject.getString("CliqAlDirectory");
    CliqAlAlimFile=jsonObject.getString("CliqAlAlimFile");
    CliqAlCompoFile =jsonObject.getString("CliqAlCompoFile");
    return this;
  }
}
