package developers.bmsce.mank.com.foodorderserver;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {
    Button btnShowLocation;


    GPStrace gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        btnShowLocation = (Button) findViewById(R.id.Show_Location);
        btnShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gps = new GPStrace(Main2Activity.this);
                if (gps.getLocation() != null) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    Toast.makeText(getApplicationContext(), "Your Location is \nLat:" + latitude + "\nLong:" + longitude, Toast.LENGTH_LONG).show();
                } else { //
                    // gps.showSettingAlert();
                }
            }
        });
    }
}

