package developers.bmsce.mank.com.foodorderserver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import developers.bmsce.mank.com.foodorderserver.Common.Common;
import developers.bmsce.mank.com.foodorderserver.Models.User;

import static android.support.constraint.Constraints.TAG;

public class SignIn extends AppCompatActivity {

    EditText et_phone,et_name, et_password;

    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference users;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);



        et_phone = findViewById(R.id.et_phone);
        et_password = findViewById(R.id.et_password);
        Button btnsubmit = findViewById(R.id.btn_submit);


        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        users = mFirebaseDatabase.getReference("user");
        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SigninUser(et_phone.getText().toString(),et_password.getText().toString());




            }
        });

    }


    private void SigninUser(String phone, String passowrd) {

        final ProgressDialog progressDialog = new ProgressDialog(SignIn.this);
        progressDialog.setMessage("Please wait");
        progressDialog.show();

        final String localPhone = phone;
        final String localPassowrd = passowrd;
        users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                if(dataSnapshot.child(localPhone).exists()) {



                    progressDialog.dismiss();
                    User user = dataSnapshot.child(localPhone).getValue(User.class);
                    user.setPhone(localPhone);

                    if(Boolean.parseBoolean(user.getIsStaff())) {
                                   if (user.getPassword().equals(localPassowrd)) {


                                            Intent intent = new Intent(SignIn.this, Home.class);
                                            Common.currentUser = user;
                                            startActivity(intent);
                                            finish();


                                            Toast.makeText(SignIn.this, "sign In Succsfull", Toast.LENGTH_SHORT).show();
                                      } else {

                                                    Toast.makeText(SignIn.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                                   }
                    }else{
                        Toast.makeText(SignIn.this, "Please signe with staff account", Toast.LENGTH_SHORT).show();

                    }


//
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(SignIn.this,"User not exist in database",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });



    }
}
