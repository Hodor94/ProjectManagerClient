package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;
import com.grum.raphael.projectmanagerclient.tasks.CheckInternet;
import com.grum.raphael.projectmanagerclient.tasks.GetMyChatsTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Raphael on 27.11.2017.
 */

public class ChooseChatFragment extends Fragment {

    private FloatingActionButton createChat;
    private ArrayList<JSONObject> myChats;
    private ArrayList<ConnectChatWithIdentifier> chatInformation;
    private ListView chatsList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.choose_chat_fragment, container, false);
        chatInformation = new ArrayList<>();
        chatsList = (ListView) rootView.findViewById(R.id.chats_list);
        createChat = (FloatingActionButton) rootView.findViewById(R.id.create_chat_btn);
        createChat.setImageBitmap(textAsBitmap("+", 50, Color.WHITE));
        myChats = getMyChats();
        fillChatInformation();
        ChatListArrayAdapter adapter = new ChatListArrayAdapter(chatInformation);
        chatsList.setAdapter(adapter);
        chatsList.setOnItemClickListener(enterChat());
        createChat.setOnClickListener(goToCreateChatFragment());
        return rootView;
    }

    private void fillChatInformation() {
        for (int i = 0; i < myChats.size(); i++) {
            try {
                JSONObject chat = myChats.get(i);
                String isSoloChat = chat.getString("type");
                String name = null;
                if (isSoloChat.equals("true")) {
                    JSONArray users = chat.getJSONArray("users");
                    for (int j = 0; j < users.length(); j++) {
                        String username = users.getString(j);
                        if (!username.equals(MainActivity.userData.getUsername())) {
                            name = username;
                            break;
                        }
                    }
                } else {
                    name = chat.getString("name");
                }
                if (name != null) {
                    ConnectChatWithIdentifier newChatInfo = new ConnectChatWithIdentifier(name, i);
                    chatInformation.add(newChatInfo);
                } else {
                    break;
                }
            } catch (JSONException e) {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle(R.string.error)
                        .setMessage("Interner Fehler! Der Datensatz der Chats ist fehlerhaft!\n" +
                                "Die Chats konnten nicht geladen werden!")
                        .setNegativeButton("OK", null).create();
                alertDialog.show();
                break;
            }
        }
    }

    private View.OnClickListener goToCreateChatFragment() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("chats", myChats);
                Fragment newFragment = new CreateChatFragment();
                newFragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.containerFrame, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        };
    }

    private Bitmap textAsBitmap(String text, float textSize, int color) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(color);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative so - to get it positive
        int width = (int) (paint.measureText(text) + 0.0f);
        int heigth = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, heigth, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    private ArrayList<JSONObject> getMyChats() {
        ArrayList<JSONObject> myChats = new ArrayList<>();
        if (CheckInternet.isNetworkAvailable(getContext())) {
            String[] params = new String[] {MainActivity.URL + "user/chats",
            MainActivity.userData.getToken(), MainActivity.userData.getUsername(),
            MainActivity.userData.getTeamName()};
            GetMyChatsTask getMyChatsTask = new GetMyChatsTask();
            try {
                JSONObject result = getMyChatsTask.execute(params).get();
                String success = result.getString("success");
                if (success.equals("true")) {
                    JSONArray chats = result.getJSONArray("chats");
                    for (int i = 0; i < chats.length(); i++) {
                        JSONObject chat = chats.getJSONObject(i);
                        myChats.add(chat);
                    }
                    System.out.println("YOLO");
                } else {
                    String reason = result.getString("reason");
                    AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                            .setTitle(R.string.error)
                            .setMessage("Ihre Chats konnten nicht geladen werden!\n" + reason)
                            .setNegativeButton("OK", null).create();
                    alertDialog.show();
                }
            } catch (InterruptedException e) {
                // TODO
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO
                e.printStackTrace();
            } catch (JSONException e) {
                // TODO
                e.printStackTrace();
            }
        } else {
            AlertDialog alertDialog = CheckInternet.internetNotAvailable(getActivity());
            alertDialog.show();
        }
        return myChats;
    }

    private AdapterView.OnItemClickListener enterChat() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView nameView = (TextView) view.findViewById(R.id.chat_name);
                String name = nameView.getText().toString();
                TextView indexView = (TextView) view.findViewById(R.id.chat_position);
                int indexOfChat = Integer.parseInt(indexView.getText().toString());
                JSONObject chat = myChats.get(position);
                ChatFragment chatFragment = new ChatFragment();
                Bundle bundle = new Bundle();
                bundle.putString("chat", chat.toString());
                chatFragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.containerFrame, chatFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        };
    }
}
