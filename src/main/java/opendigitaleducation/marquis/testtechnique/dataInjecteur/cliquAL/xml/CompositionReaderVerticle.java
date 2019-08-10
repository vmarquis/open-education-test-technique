package opendigitaleducation.marquis.testtechnique.dataInjecteur.cliquAL.xml;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.file.AsyncFile;
import io.vertx.core.file.OpenOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.parsetools.RecordParser;
import opendigitaleducation.marquis.testtechnique.AlimentDTO;
import opendigitaleducation.marquis.testtechnique.ProjectConfig;

import java.util.HashMap;

public class CompositionReaderVerticle extends AbstractVerticle {
  private HashMap<Integer, CliquALCompositionPOJO> compoMap = new HashMap<>();
  private ProjectConfig projectConfig;
  private EventBus eventBus;


  @Override
  public void start(Promise<Void> startPromise) {
    eventBus = vertx.eventBus();
    StartEventBusConsummer();
    ConfigRetriever retriever = ConfigRetriever.create(vertx);
    retriever.getConfig(ar -> {
      projectConfig = new ProjectConfig().MapFrom(ar.result());
      ReadCompositionFile(startPromise);
    });
  }

  private void ReadCompositionFile(Promise<Void> startPromise) {
    vertx.fileSystem().open(projectConfig.CliqAlDirectory + "/" + projectConfig.CliqAlCompoFile,
      new OpenOptions(), result -> {
        AsyncFile file = result.result();
        RecordParser parser = RecordParser.newDelimited("</COMPO>", parsedBufer -> fillAlimentDTOCompositionFromXml(parsedBufer));
        file.endHandler(v -> {
          file.close();
          startPromise.complete();
        });
        file.handler(parser);
      });
  }

  private void StartEventBusConsummer() {
    eventBus.consumer("aliment.data.inject.CiquALXml.setComposition", message -> {
      AlimentDTO alimentDTO = ((JsonObject) message.body()).mapTo(AlimentDTO.class);
      CliquALCompositionPOJO compo = compoMap.get(alimentDTO.getCode());
      alimentDTO.setGlucides(compo.getGlucides());
      alimentDTO.setProteines(compo.getProteines());
      alimentDTO.setLipides(compo.getLipides());
      eventBus.send("aliment.data.inject.save", JsonObject.mapFrom(alimentDTO));
    });
  }

  private void fillAlimentDTOCompositionFromXml(Buffer parsedBufer) {
    try {
      CliquALCompositionPOJO cliquALComposition;
      String XML = parsedBufer.toString();
      int constCode = Integer.parseInt(getXMLValue(XML, "<const_code>", "</const_code>"));
      int alimCode = Integer.parseInt(getXMLValue(XML, "<alim_code>", "</alim_code>"));
      cliquALComposition = compoMap.get(alimCode);
      if (cliquALComposition == null) {
        cliquALComposition = new CliquALCompositionPOJO();
        compoMap.put(alimCode, cliquALComposition);
      }
      switch (constCode) {
        case 25000:
          cliquALComposition.setProteines(getTeneur(XML));
          break;
        case 31000:
          cliquALComposition.setGlucides(getTeneur(XML));
          break;
        case 40000:
          cliquALComposition.setLipides(getTeneur(XML));
          break;
      }
    } catch (NumberFormatException e) {
      e.printStackTrace();
    }
  }

  private double getTeneur(String XML) {
    return Double.parseDouble(getXMLValue(XML, "<teneur>", "</teneur>").
      replace(",", ".").
      replace("&lt;", "").
      replace("traces", "0").
      replace("-", "0").
      trim());
  }

  private static String getXMLValue(String buf, String startBalise, String endBalise) {
    int start = buf.indexOf(startBalise) + startBalise.length();
    int end = buf.indexOf(endBalise);
    return buf.substring(start, end).trim();
  }
}
