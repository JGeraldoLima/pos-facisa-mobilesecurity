package jwt.security.jgeraldo.com.posfacisajwtauth.retrofit;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface JWTServerRetrofitInterface {

    @GET("/api/protected/random-quote")
    Call<JWTServerResult> getRandomQuote(@Header("Authorization") String token);

}
