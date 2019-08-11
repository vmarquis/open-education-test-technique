package opendigitaleducation.marquis.testtechnique.agregateurs;

@SuppressWarnings({"unused", "WeakerAccess", "UnusedReturnValue"}) //JSon serialisation, Fluent
public class CalorieRequestDTO {
  private String searchedAlimentName;
  private CalorieAgregateurType calorieAgregateurType;

  public CalorieAgregateurType getCalorieAgregateurType() {
    return calorieAgregateurType;
  }

  public CalorieRequestDTO setCalorieAgregateurType(CalorieAgregateurType calorieAgregateurType) {
    this.calorieAgregateurType = calorieAgregateurType;
    return this;
  }

  public String getSearchedAlimentName() {
    return searchedAlimentName;
  }

  public CalorieRequestDTO setSearchedAlimentName(String searchedAlimentName) {
    this.searchedAlimentName = searchedAlimentName;
    return this;
  }
}
