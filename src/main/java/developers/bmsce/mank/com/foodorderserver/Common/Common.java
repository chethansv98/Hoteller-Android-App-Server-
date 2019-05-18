package developers.bmsce.mank.com.foodorderserver.Common;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import java.util.Calendar;
import java.util.Locale;

import developers.bmsce.mank.com.foodorderserver.Models.Request;
import developers.bmsce.mank.com.foodorderserver.Models.User;
import developers.bmsce.mank.com.foodorderserver.Remote.APIService;
import developers.bmsce.mank.com.foodorderserver.Remote.IGeoCordinates;
import developers.bmsce.mank.com.foodorderserver.Remote.RetrofitClient;
import retrofit2.Retrofit;

public class Common {

    public  static Request currentRequest;
    public  static User currentUser;
    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";

    public static String baseUrl = "https://maps.googleapis.com";
    public static final String BASE_URL = "https://fcm.googleapis.com/";

    public static APIService getFCMService()
    {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);

    }

    public static final String converCodeToStatus(String status) {

        if(status.equals("0"))
        {
            return  "Placed";
        }else if(status.equals("1")){
            return "On my Way";
        }else {

            return "Shipped";
        }


    }

    public static IGeoCordinates getGeoCodeServices() {

        return RetrofitClient.getClient(baseUrl).create(IGeoCordinates.class);
    }

    public static Bitmap scalBitmap(Bitmap bitmap, int newwidth, int newheight) {


        Bitmap scaledBitmap = Bitmap.createBitmap(newwidth, newheight, Bitmap.Config.ARGB_8888);

        float scaleX = newwidth/(float)bitmap.getWidth();
        float scaleY = newheight/(float)bitmap.getHeight();
        float pivotX=0,pivotY=0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);


        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, 0, 0, new Paint((Paint.FILTER_BITMAP_FLAG)));

        return scaledBitmap;


    }

    public static String getdate(long time) {


        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date = new StringBuilder(
                android.text.format.DateFormat.format("dd-MM-yyyy HH:mm",calendar).toString());
        return date.toString();
    }
}
