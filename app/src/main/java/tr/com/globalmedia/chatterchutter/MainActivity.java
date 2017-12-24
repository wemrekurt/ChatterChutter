package tr.com.globalmedia.chatterchutter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText editTextUserName;
    private EditText editTextUserPassword;
    private Button buttonLogin;
    private TextView txtRegister;
    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private String userName;
    private String userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextUserName = (EditText)findViewById(R.id.editTextUserName);
        editTextUserPassword = (EditText)findViewById(R.id.editTextUserPassword);
        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        txtRegister = (TextView) findViewById(R.id.txtRegister);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();

        // Eğer kullanıcı giriş yapmış ise HomeActivity başlat.
        if(firebaseUser != null){
            Intent i = new Intent(MainActivity.this,HomeActivity.class);
            startActivity(i);
            finish();
        }

        // Giriş butonuna tıklayınca..
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = editTextUserName.getText().toString();
                userPassword = editTextUserPassword.getText().toString();
                if(userName.isEmpty() || userPassword.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Lütfen gerekli alanları doldurunuz!", Toast.LENGTH_SHORT).show();
                }else if(!userName.contains("@")){
                    Toast.makeText(getApplicationContext(),"Bir e-posta adresi girilmelidir!",Toast.LENGTH_SHORT).show();
                }else if(checkEmail(userName)) {
                    Toast.makeText(getApplicationContext(),"Yalnızca bil.omu.edu.tr!",Toast.LENGTH_SHORT).show();
                }else{
                    // Tüm validasyonları geçince giriş fonksiyonunu çalıştır.
                    login();
                }
            }
        });

        // Kayıt ol butonuna basınca RegisterActivity çalıştırılması
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    // E-posta kontrolü
    private boolean checkEmail(String email){
        String[] parts = email.split("@");
        if(parts[1].equals("bil.omu.edu.tr")){
            return false;
        }else{
            return true;
        }
    }

    private void login() {
        mAuth.signInWithEmailAndPassword(userName,userPassword).addOnCompleteListener(MainActivity.this,
            new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Intent i = new Intent(MainActivity.this,HomeActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else{
                        // Firebase'den gelen hatayı toast'a pasla.
                        Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        );
    }
}
