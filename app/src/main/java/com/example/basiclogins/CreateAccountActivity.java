package com.example.basiclogins;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextEmail;
    private Button buttonCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        wireWidgets();
        prefillUserName();
        
        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerAccountOnBackendless();
            }
        });
    }

    private void registerAccountOnBackendless() {
        //verify all fields are filled and passwords are the same
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();
        String email = editTextEmail.getText().toString();
        String username = editTextUsername.getText().toString();
        String name = editTextName.getText().toString();

        if(allFieldsValid(password, confirmPassword, username, email, name)) {
            BackendlessUser user = new BackendlessUser();
            user.setProperty("email", email);
            user.setProperty("name", name);
            user.setProperty("username", username);
            user.setProperty("password", password);

            Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
                @Override
                public void handleResponse(BackendlessUser registeredUser) {
                    Toast.makeText(CreateAccountActivity.this, registeredUser.getUserId() + " has registered", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Toast.makeText(CreateAccountActivity.this, fault.getMessage(), Toast.LENGTH_LONG).show();

                }
            });

        }

        //make the registration call

        //return to LoginActivity in the handleResponse
    }

    private boolean allFieldsValid(String password, String confirmPassword, String username, String email, String name) {
        //TODO validate all fields
        return password.endsWith(confirmPassword) && username.length() != 0;
    }

    private void prefillUserName() {
        String username = getIntent().getStringExtra(LoginActivity.EXTRA_USERNAME);
        if(username != null) {
            editTextUsername.setText(username);
        }
    }

    private void wireWidgets() {
        editTextUsername = findViewById(R.id.edittext_create_username);
        editTextName = findViewById(R.id.edittext_create_name);
        editTextPassword = findViewById(R.id.edittext_create_password);
        editTextConfirmPassword = findViewById(R.id.edittext_create_confirmpassword);
        editTextEmail = findViewById(R.id.edittext_create_email);
        buttonCreateAccount = findViewById(R.id.button_create_account);
    }
}