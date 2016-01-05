package de.tunebitfm.nettyexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import de.tunebitfm.nettyexample.client.WebsocketClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WebsocketClient client = new WebsocketClient();
        client.connect();

        final EditText textField = (EditText) this.findViewById(R.id.text_field);

        Button sendButton = (Button) this.findViewById(R.id.button_send);
        sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String message = textField.getText().toString();
                Log.i("websocket-test", "Sending message: " + message);
                client.sendMessage(message);
            }

        });

    }

}
