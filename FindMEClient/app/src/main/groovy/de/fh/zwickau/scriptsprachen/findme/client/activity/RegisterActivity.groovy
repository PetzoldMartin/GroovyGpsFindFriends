package de.fh.zwickau.scriptsprachen.findme.client.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import android.widget.Toast
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.OnClick
import com.arasthel.swissknife.annotations.OnUIThread
import de.fh.zwickau.scriptsprachen.findme.client.R
import de.fh.zwickau.scriptsprachen.findme.client.ui.Progress
import de.fh.zwickau.scriptsprachen.findme.client.util.StorageManager
import de.fh.zwickau.scriptsprachen.findme.client.rest.RESTRequests

public class RegisterActivity extends AppCompatActivity {

    RESTRequests restRequests
    EditText nameTextfield
    EditText emailTextfield
    ProgressDialog progDialog
    String usedName

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        // This must be called for injection of views and callbacks to take place
        SwissKnife.inject(this);
        // This must be called for saved state restoring
        SwissKnife.restoreState(this, savedInstanceState);
        // This mus be called for automatic parsing of intent extras
        // SwissKnife.loadExtras(this)

        restRequests = new RESTRequests()
        nameTextfield = findViewById(R.id.textfield_name)
        emailTextfield = findViewById(R.id.textfield_email)

        nameTextfield.setText("Nutzername")

        String email = StorageManager.getInstance().getEmail(this)
        usedName = StorageManager.getInstance().getName(this)
        if (email != null) {
            Progress.showProgress("Einloggen", this)
            restRequests.login(email, this)
        }
    }

    @OnClick(R.id.button_register)
    public void onRegisterClicked() {
        usedName = nameTextfield.getText()
        String email = emailTextfield.getText()
        usedName = usedName.trim()
        email = email.trim()

        if ("debug".equals(usedName)) {
            // Skip connection to server for test purposes
            Intent intent = new Intent(this, MainActivity.class)
            startActivity(intent)
            finish()
            return
        }

        if (checkValidInput(usedName, email)) {
            Progress.showProgress("Registrieren", this)
            restRequests.register(email, usedName, this)
        }
        else
            Toast.makeText(this, "E-Mail-Adresse oder Name ungültig", Toast.LENGTH_LONG).show()
    }

    @OnClick(R.id.button_login)
    public void onLoginClicked() {
        usedName = nameTextfield.getText()
        String email = emailTextfield.getText()
        usedName = usedName.trim()
        email = email.trim()
        StorageManager.getInstance().storeLoginData(email, usedName, this)

        if (checkValidInput(usedName, email)) {
            Progress.showProgress("Einloggen", this)
            restRequests.login(email, this)
        }
    }

    private boolean checkValidInput(String email) {
        if ("".equals(email))
            return false
        int atIndex = -1
        int dotIndex = -1
        for (int i = 0; i < email.length(); ++i) {
            if ('@' == email.charAt(i))
                atIndex = i
            if ('.' == email.charAt(i))
                dotIndex = i
        }
        if (atIndex == -1 || dotIndex < atIndex || dotIndex == email.length() - 1)
            return false
        return true
    }

    private boolean checkValidInput(String name, String email) {
        if ("".equals(name))
            return false
        return checkValidInput(email)
    }

    @OnUIThread
    public void showErrorMessage(String errorMessage) {
        Progress.dismissProgress()
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    @OnUIThread
    public void showRegisterSuccessful(String email) {
        Toast.makeText(this, "Registrierung erfolgreich", Toast.LENGTH_LONG).show()
        Progress.showProgress("Einloggen", this)
        restRequests.login(email, this)
    }

    @OnUIThread
    public void showMapScreen(String email) {
        Toast.makeText(this, "Login erfolgreich", Toast.LENGTH_LONG).show()
        StorageManager.getInstance().storeLoginData(email, usedName, this)
        Intent intent = new Intent(this, MainActivity.class)
        startActivity(intent)
        finish()
    }

}
