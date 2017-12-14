package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputFilter;
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

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;
import com.grum.raphael.projectmanagerclient.tasks.CheckInternet;
import com.grum.raphael.projectmanagerclient.tasks.DeleteChatTask;
import com.grum.raphael.projectmanagerclient.tasks.GetMessagesOfChatTask;
import com.grum.raphael.projectmanagerclient.tasks.SendMessageTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class ChatFragment extends Fragment {

    private JSONObject chat;
    private String id;
    private String chatName;
    private FloatingActionButton btnSend;
    private EditText newMessage;
    private String message;
    private ListView messages;
    private Button deleteChat;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chats, container, false);
        Bundle bundle = getArguments();
        JSONArray users = null;
        try {
            chat = new JSONObject(bundle.getString("chat"));
            id = chat.getString("id");
            chatName = chat.getString("name");
            users = chat.getJSONArray("users");
        } catch (JSONException e) {
            showInternalError();
        }
        newMessage = (EditText) rootView.findViewById(R.id.input);
        newMessage.setFilters(new InputFilter[]{MainActivity.EMOJI_FILTER});
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

        // Writing data to the database
        btnSend.setOnClickListener(sendMessage());
        deleteChat = (Button) rootView.findViewById(R.id.deleteChat);
        deleteChat.setOnClickListener(removeChat());
        displayMessages(id);
        return rootView;
    }

    private View.OnClickListener sendMessage() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (message != null && !message.equals("")) {
                    if (chat != null) {
                        if (CheckInternet.isNetworkAvailable(getContext())) {
                            Calendar currentTime = Calendar.getInstance();
                            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                            String timestamp = formatter.format(currentTime.getTime());
                            String[] params = new String[]{MainActivity.URL + "receive/message",
                                    MainActivity.userData.getToken(), id, message,
                                    MainActivity.userData.getUsername(), timestamp};
                            SendMessageTask sendMessageTask = new SendMessageTask();
                            try {
                                JSONObject result = sendMessageTask.execute(params).get();
                                String success = result.getString("success");
                                if (success.equals("true")) {
                                    displayMessages(id);
                                } else {
                                    String reason = result.getString("reason");
                                    AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                                            .setTitle(R.string.error)
                                            .setMessage("Die Nachricht konnte nicht verschickt " +
                                                    "werden!\n" + reason)
                                            .setNegativeButton("OK", null)
                                            .create();
                                    alertDialog.show();
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                // TODO
                                e.printStackTrace();
                            } catch (JSONException e) {
                                // TODO
                                e.printStackTrace();
                            }
                        } else {
                            AlertDialog alertDialog
                                    = CheckInternet.internetNotAvailable(getActivity());
                            alertDialog.show();
                        }
                    } else {
                        showInternalError();
                    }
                    newMessage.setText("");
                    message = "";
                } else {
                    Toast.makeText(getContext(), "Bitte geben Sie eine Nachricht ein, die " +
                            "Sie verschicken wollen!", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void showInternalError() {
        AlertDialog internalError = new AlertDialog.Builder(getContext())
                .setTitle(R.string.error)
                .setMessage("Interner Fehler! Es wurden falsche Daten übergeben!\n" +
                        "Das Chatten ist zur eit nicht möglich! Versuchen Sie es später noch einmal.")
                .setNegativeButton("OK", null)
                .create();
        internalError.show();
    }

    public void displayMessages(String id) {
        if (CheckInternet.isNetworkAvailable(getContext())) {
            String[] params = new String[]{MainActivity.URL + "chat/messages",
                    MainActivity.userData.getToken(), id, MainActivity.userData.getUsername()};
            GetMessagesOfChatTask getMessagesTask = new GetMessagesOfChatTask();
            try {
                JSONObject result = getMessagesTask.execute(params).get();
                String success = result.getString("success");
                if (success.equals("true")) {
                    JSONArray fetchedMessages = result.getJSONArray("messages");
                    final ArrayList<JSONObject> messages = new ArrayList<>();
                    for (int i = 0; i < fetchedMessages.length(); i++) {
                        messages.add(fetchedMessages.getJSONObject(i));
                    }
                    final ChatMessageArrayAdapter adapter = new ChatMessageArrayAdapter(messages,
                            getContext());
                    this.messages.setAdapter(adapter);
                    scrollToLastItem(this.messages, adapter);
                } else {
                    String reason = result.getString("reason");
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                            .setTitle(R.string.error)
                            .setMessage("Die Nachrichten konnten nicht geladen werden!\n"
                                    + reason)
                            .setNegativeButton("OK", null).create();
                    alertDialog.show();
                }
            } catch (InterruptedException | ExecutionException | JSONException e) {
                Toast.makeText(getContext(), "Interner Fehler!\n" +
                        "Die Nachrichten konnten nicht geladen werden!", Toast.LENGTH_LONG).show();
            }
        } else {
            AlertDialog alertDialog = CheckInternet.internetNotAvailable(getActivity());
            alertDialog.show();
        }
    }

    private void scrollToLastItem(final ListView messages, final ChatMessageArrayAdapter adapter) {
        this.messages.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                messages.setSelection(adapter.getCount() - 1);
            }
        });
    }

    private View.OnClickListener removeChat() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext())
                        .setTitle(R.string.attention)
                        .setMessage("Möchten Sie den Chat wirklich löschen?")
                        .setNegativeButton("Abbrechen", null)
                        .setPositiveButton("Löschen", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (CheckInternet.isNetworkAvailable(getContext())) {
                                    String[] params = new String[]{MainActivity.URL + "delete/chat",
                                            MainActivity.userData.getToken(), id};
                                    DeleteChatTask deleteChatTask = new DeleteChatTask();
                                    try {
                                        JSONObject result = deleteChatTask.execute(params).get();
                                        String success = result.getString("success");
                                        if (success.equals("true")) {
                                            Toast.makeText(getContext(),
                                                    "Der Chat wurde erfolgreich gelsöcht!",
                                                    Toast.LENGTH_LONG).show();
                                            FragmentTransaction transaction
                                                    = getFragmentManager()
                                                    .beginTransaction();
                                            transaction.replace(R.id.containerFrame,
                                                    new ChooseChatFragment());
                                            transaction.commit();
                                        } else {
                                            String reason = result.getString("reason");
                                            Toast.makeText(getContext(),
                                                    "Der Chat konnte nicht gelöscht werden!\n"
                                                            + reason, Toast.LENGTH_LONG).show();
                                        }
                                    } catch (InterruptedException | ExecutionException
                                            | JSONException e) {
                                        Toast.makeText(getContext(),
                                                "Interner Fehler! Die Aktion konnte nicht " +
                                                        "durchgeführt werden!", Toast.LENGTH_LONG)
                                                .show();
                                    }
                                } else

                                {
                                    AlertDialog alertDialog = CheckInternet.internetNotAvailable(getActivity());
                                    alertDialog.show();
                                }
                            }
                        });
                alertBuilder.show();
            }
        };
    }

}
