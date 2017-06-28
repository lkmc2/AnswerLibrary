package linchange.example.com.answerlibrary;

import android.util.Log;

/**
 * Created by Lin Change on 2017-01-14.
 */

public class LogUtil {
    public static boolean FLAG = true;

    public static void v(String TAG, String message) {
        if (FLAG) {
            Log.v(TAG, message);
        }
    }

    public static void e(String TAG, String message) {
        if (FLAG) {
            Log.e(TAG, message);
        }
    }

    public static void d(String TAG, String message) {
        if (FLAG) {
            Log.d(TAG, message);
        }
    }

    public static void i(String TAG, String message) {
        if (FLAG) {
            Log.i(TAG, message);
        }
    }
}
