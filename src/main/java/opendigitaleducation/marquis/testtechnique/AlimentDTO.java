package opendigitaleducation.marquis.testtechnique;
@SuppressWarnings("unused")  //Used for Json Mapping
public class AlimentDTO {
  private String Name;
  private String _id;
  private double Proteines=-1;
  private double Glucides=-1;
  private double Lipides=-1;
  private int Code=-1;

  public String getName() {
    return Name;
  }

  public AlimentDTO setName(String name) {
    Name = name;
    return this;
  }

  public double getProteines() {
    return Proteines;
  }

  public AlimentDTO setProteines(double proteines) {
    Proteines = proteines;
    return this;
  }

  public double getGlucides() {
    return Glucides;
  }

  public AlimentDTO setGlucides(double glucides) {
    Glucides = glucides;
    return this;
  }

  public double getLipides() {
    return Lipides;
  }

  public AlimentDTO setLipides(double lipides) {
    Lipides = lipides;
    return this;
  }

  public AlimentDTO setCode(int code) {
    Code= code;
    return this;
  }

  public int getCode() {
    return Code;
  }

  public String get_id() {
    return _id;
  }


  public void set_id(String _id) {
    this._id = _id;
  }
}
