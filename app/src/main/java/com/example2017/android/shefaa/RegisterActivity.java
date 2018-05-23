package com.example2017.android.shefaa;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private  EditText regEmailText,regPassText,regConfirmPassText;
    private Button register_Button,login_reg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        regEmailText=(EditText)findViewById(R.id.editText_reg_email);
        regPassText=(EditText)findViewById(R.id.editText_reg_pass);
        regConfirmPassText=(EditText)findViewById(R.id.editText_reg_pass_confirm);
        register_Button=(Button)findViewById(R.id.but_register);
        login_reg=(Button)findViewById(R.id.but_login_reg);

        register_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String RegisterEmail=regEmailText.getText().toString();
                String RegisterPass=regPassText.getText().toString();
                String RegisterConfirmPass=regConfirmPassText.getText().toString();

                if ( ! TextUtils.isEmpty(RegisterEmail) && ! TextUtils.isEmpty(RegisterPass) && ! TextUtils.isEmpty(RegisterConfirmPass))
                {
                    if (RegisterPass.equals(RegisterConfirmPass))
                    {

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(RegisterEmail,RegisterPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"register sucsess",Toast.LENGTH_LONG).show();

                            }else {
                                Toast.makeText(getApplicationContext(),task.getException().getMessage().toString(),Toast.LENGTH_LONG).show();

                            }
                        }
                    });

                }else {
                        Toast.makeText(getApplicationContext(), "confirm password and password doesn't match ", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


        login_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });



    }
}
