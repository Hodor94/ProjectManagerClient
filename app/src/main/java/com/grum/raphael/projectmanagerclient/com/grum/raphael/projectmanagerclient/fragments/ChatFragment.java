package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.grum.raphael.projectmanagerclient.ChatMessage;
import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.w3c.dom.Text;

import java.util.List;

public class ChatFragment extends Fragment {

    private String receiver;
    private String team;
    private FloatingActionButton btnSend;
    private EditText newMessage;
    private String message;
    private FirebaseListAdapter<ChatMessage> firebaseListAdapter;
    private ListView messages;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chats, container, false);
        Bundle bundle = getArguments();
        receiver = bundle.getString("receiver");
        team = bundle.getString("team");
        newMessage = (EditText) rootView.findViewById(R.id.input);
        newMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                message = newMessage.getText().toString();
            }
        });
        messages = (ListView) rootView.findViewById(R.id.list_of_messages);
        btnSend = (FloatingActionButton) rootView.findViewById(R.id.fab);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase.getInstance()
                        .getReference()
                        .push()
                        .setValue(new ChatMessage(message, MainActivity.userData.getUsername(),
                                team, receiver));
                newMessage.setText("");
                displayMessages();
            }
        });
        displayMessages();
        return rootView;
    }

    public void displayMessages() {
        firebaseListAdapter = new FirebaseListAdapter<ChatMessage>(getActivity(), ChatMessage.class,
                R.layout.message, FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                if (model.getReceiver().equals(receiver)
                        && model.getTeam().equals(team)) {
                    TextView messageText = (TextView) v.findViewById(R.id.message_text);
                    TextView messageUser = (TextView) v.findViewById(R.id.message_user);
                    TextView messageTime = (TextView) v.findViewById(R.id.message_time);

                    String author = model.getMessageUser();
                    String message = model.getMessageText();
                    String time = (String) DateFormat.format("dd.MM.yyyy (HH:mm:ss)",
                            model.getMessageTime());

                    // Set values
                    messageText.setText(message);
                    messageUser.setText(author);
                    if (messageUser.getText().toString().equals(MainActivity.userData.getUsername())) {
                        messageUser.setTextColor(getResources().getColor(R.color.colorPrimary));
                    }
                    messageTime.setText(time);
                }
            }
        };
        messages.setAdapter(firebaseListAdapter);
    }

}
