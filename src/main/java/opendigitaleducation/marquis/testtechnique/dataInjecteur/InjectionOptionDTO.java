package opendigitaleducation.marquis.testtechnique.dataInjecteur;

@SuppressWarnings("WeakerAccess") //Public pour Json serialisation
public class InjectionOptionDTO {
  private String CliqAlAlimFile;
  private String CliqAlCompoFile;
  private InjectionSourceType InjectionSource;
  private String CliqAlDirectory;

  public String getCliqAlDirectory() {
    return CliqAlDirectory;
  }

  public void setCliqAlDirectory(String cliqAlDirectory) {
    CliqAlDirectory = cliqAlDirectory;
  }

  public InjectionSourceType getInjectionSource() {
    return InjectionSource;
  }

  public void setInjectionSource(InjectionSourceType injectionSource) {
    InjectionSource = injectionSource;
  }

  public String getCliqAlAlimFile() {
    return CliqAlAlimFile;
  }

  public void setCliqAlAlimFile(String cliqAlAlimFile) {
    CliqAlAlimFile = cliqAlAlimFile;
  }

  public String getCliqAlCompoFile() {
    return CliqAlCompoFile;
  }

  public void setCliqAlCompoFile(String cliqAlCompoFile) {
    CliqAlCompoFile = cliqAlCompoFile;
  }
}
