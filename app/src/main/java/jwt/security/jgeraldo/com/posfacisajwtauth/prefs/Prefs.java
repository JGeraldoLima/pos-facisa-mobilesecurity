package jwt.security.jgeraldo.com.posfacisajwtauth.prefs;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

    private static SharedPreferences ofasPreferences;
    private static SharedPreferences.Editor ofasPreferenvesEditor;

    private static String PREFS_TOKEN = "token";

    private static void initiatePreferencesIfNull(Context context) {
        if (ofasPreferences == null) {
            ofasPreferences = context.getSharedPreferences("posfacisasecuritymobile", 0);
            ofasPreferenvesEditor = ofasPreferences.edit();
        }
    }

    private static void saveChanges() {
        ofasPreferenvesEditor.commit();
        ofasPreferenvesEditor.apply();
    }

    public static String getAccessToken(Context context) {
        initiatePreferencesIfNull(context);
        return ofasPreferences.getString(PREFS_TOKEN, null);
    }

    public static void setAccessToken(Context context, String token) {
        initiatePreferencesIfNull(context);
        ofasPreferenvesEditor.putString(PREFS_TOKEN, token);
        saveChanges();
    }
}
