package guru.qa.niffler.api;

import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.CurrencyValues;
import guru.qa.niffler.model.rest.SpendJson;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface SpendApi {

  @POST("internal/spends/add")
  Call<SpendJson> addSpend(@Body SpendJson spend);

  @PATCH("internal/spends/edit")
  Call<SpendJson> editSpend(@Body SpendJson spend);

  @GET("internal/spends/{id}")
  Call<SpendJson> getSpendWithId(@Path("id") String id);

  @GET("internal/spends/all")
  Call<List<SpendJson>> allSpends(@Query("username") String username,
                                  @Query("filterCurrency") CurrencyValues filterCurrency,
                                  @Query("from") String from,
                                  @Query("to") String to);

  @DELETE("internal/spends/remove")
  Call<Void> removeSpends(@Query("username") String username, @Query("ids") List<String> ids);

  @POST("internal/categories/add")
  Call<CategoryJson> addCategory(@Body CategoryJson category);

  @PATCH("internal/categories/update")
  Call<CategoryJson> editCategory(@Body CategoryJson category);

  @GET("internal/categories/all")
  Call<List<CategoryJson>> getAllCategories(@Query("username") String username);

}
