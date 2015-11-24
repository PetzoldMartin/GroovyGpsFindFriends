package com.example.aisma.findmeclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle
import android.widget.Toast
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.OnClick;

public class MainActivity extends AppCompatActivity {
    def ILocator
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // This must be called for injection of views and callbacks to take place
        SwissKnife.inject(this);
        // This must be called for saved state restoring
        SwissKnife.restoreState(this, savedInstanceState);
        // This mus be called for automatic parsing of intent extras
        SwissKnife.loadExtras(this)
        ILocator=new ClientLocator(this);

    }

    @OnClick(R.id.test)
    public void onClick() {
        Toast.makeText(this, ILocator.toString(), Toast.LENGTH_SHORT).show()

    }
}

