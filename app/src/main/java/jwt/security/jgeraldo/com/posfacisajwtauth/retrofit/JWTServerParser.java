package jwt.security.jgeraldo.com.posfacisajwtauth.retrofit;

import android.content.Context;

import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import jwt.security.jgeraldo.com.posfacisajwtauth.prefs.Prefs;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JWTServerParser {

    static String baseUrl = "http://192.168.0.3:3001";

    public static String getRandomQuote(String token)
        throws IOException {

        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        JWTServerRetrofitInterface api = retrofit.create(JWTServerRetrofitInterface.class);
        Call<JWTServerResult> call = api.getRandomQuote("Bearer " + token);
        JWTServerResult result = call.execute().body();

        return result.message;
    }

    public static String getAccessToken(Context context, FirebaseUser user)
        throws IOException, JSONException {

        String username = user.getEmail();
        String password = user.getUid();

        JsonObject paramObject = new JsonObject();
        paramObject.addProperty("username", username);
        paramObject.addProperty("password", password);

        MediaType contetType = MediaType.parse("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(contetType, paramObject.toString());
        Request request = new Request.Builder()
            .url(baseUrl + "/users")
            .post(body)
            .build();

        Response response = client.newCall(request).execute();
        JSONObject responseObj = new JSONObject(response.body().string());
        String token = responseObj.getString("access_token");
        Prefs.setAccessToken(context, token);
        return token;
    }
}
