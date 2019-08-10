package opendigitaleducation.marquis.testtechnique.injecteur;

@SuppressWarnings("WeakerAccess") //Public pour Json serialisation
public class InjectionOptionDTO {
  private InjectionSourceType InjectionSource;

  public InjectionSourceType getInjectionSource() {
    return InjectionSource;
  }

  public void setInjectionSource(InjectionSourceType injectionSource) {
    InjectionSource = injectionSource;
  }

}
