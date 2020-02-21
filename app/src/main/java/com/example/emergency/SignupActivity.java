package com.example.emergency;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignupActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference ref;

    EditText t1;
    EditText t2;
    EditText t3;
    Button b1;
    CheckBox c1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        t1=(EditText) findViewById(R.id.editText);
        t2=(EditText) findViewById(R.id.editText2);
        t3=(EditText) findViewById(R.id.editText3);
        b1=(Button) findViewById(R.id.btnlogin);
        c1=(CheckBox) findViewById(R.id.checkbox);
        database= FirebaseDatabase.getInstance();
        ref=database.getReference();

        b1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                addUser();
            }
        });
        c1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean value){
                if(value){
                    t2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    t3.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }

                else{
                    t2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    t3.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    private void addUser(){
        final String name=t1.getText().toString().trim();
        final String pass1=t2.getText().toString().trim();
        final String pass2=t3.getText().toString().trim();

        //final Register register=new Register(name, pass1);

        if(name.length()==0){
            t1.setError("User name is required");
        }

        else{
            if(isInternetConnection()){
                final ProgressDialog Dialog=new ProgressDialog(SignupActivity.this);
                Dialog.setMessage("Loading");
                Dialog.show();
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(name)){
                            t1.setError("User name already exists");
                        }

                        else{
                            if(pass1.length()==0){
                                t2.setError("This field should not be empty");
                            }

                            else if(pass2.length()==0){
                                t3.setError("This field should not be empty");
                            }

                            else if(pass1.equals(pass2)){
                                Register register=new Register(name, pass1);
                                ref.child(name).setValue(register);
                                Intent intent=new Intent(SignupActivity.this, MapsActivity.class);
                                startActivity(intent);
                            }

                            else{
                                t3.setError("Password is not matching");
                            }
                        }
                        Dialog.hide();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            else{
                Toast toast= Toast.makeText(SignupActivity.this, "Please check your internet connction", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    public boolean isInternetConnection()
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()== NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState()==NetworkInfo.State.CONNECTED)
        {
            return true;
        }

        else
        {
            return false;
        }
    }
}
