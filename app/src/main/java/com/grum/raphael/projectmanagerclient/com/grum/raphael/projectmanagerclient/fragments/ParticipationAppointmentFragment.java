package com.grum.raphael.projectmanagerclient.com.grum.raphael.projectmanagerclient.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.grum.raphael.projectmanagerclient.MainActivity;
import com.grum.raphael.projectmanagerclient.R;
import com.grum.raphael.projectmanagerclient.tasks.CheckInternet;
import com.grum.raphael.projectmanagerclient.tasks.GetAppointmentTask;
import com.grum.raphael.projectmanagerclient.tasks.SendParticipationTask;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ParticipationAppointmentFragment extends Fragment {

    private final String MAYBE = "MAYBE";
    private final String YES = "YES";
    private final String NO = "NO";
    private JSONObject appointment;
    private String appointmentId;
    private String name;
    private String myAnswer;
    private String date;
    private String userAnswersText;
    private HashMap<String, String> userAnswers;
    private TextView nameHeader;
    private ListView answersList;
    private RadioButton yes;
    private RadioButton no;
    private RadioButton maybe;
    private RadioGroup checkButtons;
    private Button sendParticipationAnswer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_participation_appointment, container,
                false);
        Bundle bundle = getArguments();
        nameHeader = (TextView) rootView.findViewById(R.id.appointment_name_header);
        answersList = (ListView) rootView.findViewById(R.id.appointment_participation_answers);
        yes = (RadioButton) rootView.findViewById(R.id.take_part_appointment_participation);
        maybe = (RadioButton) rootView.findViewById(R.id.maybe_appointment_participation);
        no = (RadioButton)  rootView.findViewById(R.id.decline_appointment_participation);
        checkButtons = (RadioGroup) rootView.findViewById(R.id.answer_options);
        sendParticipationAnswer = (Button) rootView.findViewById(R.id.btn_send_participation_answer);
        fetchDataFromBundle(bundle);
        nameHeader.setText("Beteligung für das Meeting " + name + " am " + date + ":");
        HashMapAdapterForList adapter = new HashMapAdapterForList(userAnswers);
        answersList.setAdapter(adapter);
        return rootView;
    }

    private void fetchDataFromBundle(Bundle bundle) {
        String appointmentData = bundle.getString("appointment");
        if (appointmentData != null) {
            try {
                appointment = new JSONObject(appointmentData);
                appointmentId = appointment.getString("id");
                name = appointment.getString("name");
                date = appointment.getString("deadline");
                date = date.split(" ")[0];
                userAnswersText = appointment.getString("userAnswer");
                fillUserAnswers(userAnswersText);
            } catch (JSONException e) {
                // TODO
                appointment = null;
            }
        } else {
            appointment = new JSONObject();
        }
    }

    private void fillUserAnswers(String userAnswersText) {
        try {
            JSONObject userAnswerJson = new JSONObject(userAnswersText);
            userAnswers = new HashMap<>();
            Iterator<?> keys = userAnswerJson.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                if (key.equals(MainActivity.userData.getUsername())) {
                    myAnswer = userAnswerJson.getString(key);
                    if (myAnswer.equals(YES)) {
                        yes.setChecked(true);
                    } else if (myAnswer.equals(MAYBE)) {
                        maybe.setChecked(true);
                    } else {
                        no.setChecked(true);
                    }
                } else {
                    String value = userAnswerJson.getString(key);
                    userAnswers.put(key, value);
                }
            }
            checkButtons.setOnCheckedChangeListener(setCheckAnserListener());
            sendParticipationAnswer.setOnClickListener(sendAnswer());
        } catch (JSONException e) {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.error)
                    .setMessage("Die Beteiligung der User konnte nicht geladen werden!")
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FragmentTransaction transaction
                                    = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.containerFrame, new AppointmentsFragment());
                            transaction.commit();
                        }
                    }).create();
            alertDialog.show();
        }
    }

    private RadioGroup.OnCheckedChangeListener setCheckAnserListener() {
        return new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.take_part_appointment_participation) {
                    myAnswer = YES;
                } else if (checkedId == R.id.maybe_appointment_participation) {
                    myAnswer = MAYBE;
                } else {
                    myAnswer = NO;
                }
            }
        };
    }

    private View.OnClickListener sendAnswer() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckInternet.isNetworkAvailable(getContext())) {
                    String[] params = new String[]{MainActivity.URL + "appointment/answer/participation",
                            MainActivity.userData.getToken(), MainActivity.userData.getUsername(),
                            appointmentId, myAnswer};
                    SendParticipationTask sendParticipationTask = new SendParticipationTask();
                    try {
                        JSONObject result = sendParticipationTask.execute(params).get();
                        String success = result.getString("success");
                        if (success.equals("true")) {
                            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                    .setTitle(R.string.success)
                                    .setMessage("Sie haben Ihre Beteiligung erfolgreich gespeichert!")
                                    .setNegativeButton("OK", reloadPage())
                                    .create();
                            alertDialog.show();
                        } else {
                            String reason = result.getString("reason");
                            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                                    .setTitle(R.string.error)
                                    .setMessage("Ihre Beteiligung konnte nicht gespeichert werden!\n"
                                            + reason)
                                    .setNegativeButton("OK", reloadPage())
                                    .create();
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
            }
        };
    }

    private DialogInterface.OnClickListener reloadPage() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Bundle bundle = getAppointment();
                Fragment newFragment = new ParticipationAppointmentFragment();
                if (bundle != null) {
                    newFragment.setArguments(bundle);
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.pager_appointment_profile, newFragment);
                    transaction.commit();
                } else {
                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.error)
                            .setMessage("Das Meeting konnte nicht geladen werden!")
                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Fragment newFragment = new AppointmentsFragment();
                                    FragmentTransaction transaction = getFragmentManager()
                                            .beginTransaction();
                                    transaction.replace(R.id.containerFrame, newFragment);
                                    transaction.commit();
                                }
                            }).create();
                    alertDialog.show();
                }
            }
        };
    }

    private Bundle getAppointment() {
        Bundle bundle = null;
        String[] params = new String[] {MainActivity.URL + "appointment",
                MainActivity.userData.getToken(), appointmentId};
        final GetAppointmentTask getAppointmentTask = new GetAppointmentTask();
        try {
            JSONObject result = getAppointmentTask.execute(params).get();
            String success = result.getString("success");
            if (success.equals("true")) {
                bundle = new Bundle();
                bundle.putString("appointment", result.getString("appointment"));
            } else {
                String reason = result.getString("reason");
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.error)
                        .setMessage("Das Meeting konnte nicht geladen werden!\n" + reason)
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Fragment newFragment = new AppointmentsFragment();
                                FragmentTransaction transaction = getFragmentManager()
                                        .beginTransaction();
                                transaction.replace(R.id.containerFrame, newFragment);
                                transaction.commit();
                            }
                        }).create();
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
        return bundle;
    }
}
