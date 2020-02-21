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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    EditText t1,t2;
    TextView text_view_id;
    Button b1;
    CheckBox c1;

    FirebaseDatabase database;
    DatabaseReference ref, ref1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        t1=(EditText) findViewById(R.id.editText);
        t2=(EditText) findViewById(R.id.editText2);
        b1=(Button) findViewById(R.id.btnlogin);
        c1=(CheckBox) findViewById(R.id.checkbox);
        text_view_id = (TextView)findViewById(R.id.text_view_id);
        database=FirebaseDatabase.getInstance();

        b1.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                checkUser();
            }
        });

        c1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean value){
                if(value){
                    t2.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }

                else{
                    t2.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        text_view_id.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent=new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
    private void checkUser()
    {
        final String name=t1.getText().toString().trim();
        final String pass=t2.getText().toString().trim();

        if(name.length()==0)
        {
            t1.setError("User name is required");
        }

        else
        {
            if(pass.length()==0)
            {
                t2.setError("Password is required");
            }

            else
            {
                if(isInternetConnection())
                {
                    ref=database.getReference();
                    ref.addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            if(dataSnapshot.hasChild(name))
                            {
                                ref1=database.getReference(name);
                                ref1.child("pass").addListenerForSingleValueEvent(new ValueEventListener()
                                {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot)
                                    {
                                        String pass1=dataSnapshot.getValue(String.class);

                                        if(pass1.equals(pass))
                                        {
                                            final ProgressDialog Dialog=new ProgressDialog(MainActivity.this);
                                            Dialog.setMessage("Loading");
                                            Dialog.show();
                                            Intent intent=new Intent(MainActivity.this, MapsActivity.class);
                                            startActivity(intent);
                                            Dialog.hide();
                                        }

                                        else
                                        {
                                            t2.setError("Incorrect Password");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError)
                                    {

                                    }
                                });
                            }

                            else
                            {
                                t1.setError("Incorrect User Name");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {

                        }
                    });
                }

                else
                {
                    Toast toast= Toast.makeText(MainActivity.this, "Please check your internet connction", Toast.LENGTH_LONG);
                    toast.show();
                }
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
