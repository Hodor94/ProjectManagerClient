package layout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.grum.raphael.projectmanagerclient.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EditUserProfileFragment extends Fragment {

    private TextView username;
    private TextView team;
    private TextView tributes;
    private TextView dayOfEntry;
    private EditText firstName;
    private EditText surname;
    private EditText address;
    private EditText phoneNr;
    private DatePicker birthday;
    private Button ready;

    public EditUserProfileFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_user_profile, container, false);
        Bundle bundle = getArguments();
        // Fetch components of fragment
        username = (TextView) rootView.findViewById(R.id.edit_username);
        team = (TextView) rootView.findViewById(R.id.edit_team);
        tributes = (TextView) rootView.findViewById(R.id.edit_tributes);
        dayOfEntry = (TextView) rootView.findViewById(R.id.edit_dayOfEntry);
        firstName = (EditText) rootView.findViewById(R.id.edit_firstName);
        surname = (EditText) rootView.findViewById(R.id.edit_surname);
        address = (EditText) rootView.findViewById(R.id.edit_address);
        phoneNr = (EditText) rootView.findViewById(R.id.edit_phoneNr);
        birthday = (DatePicker) rootView.findViewById(R.id.edit_birthday);
        ready = (Button) rootView.findViewById(R.id.edit_user_profile);

        // Fetch values of Bundle
        String firstName = bundle.getString("firstName");
        String username = bundle.getString("username");
        String team = bundle.getString("team");
        String tributes = bundle.getString("tributes");
        String dayOfEntry = bundle.getString("dayOfEntry");
        String surname = bundle.getString("surname");
        String address = bundle.getString("address");
        String phoneNr = bundle.getString("phoneNr");
        String birthday = bundle.getString("birthday");

        // Set values to components
        this.username.setText(username);
        this.team.setText(team);
        this.tributes.setText(tributes);
        this.dayOfEntry.setText(dayOfEntry);
        this.firstName.setText(firstName);
        this.surname.setText(surname);
        this.address.setText(address);
        this.phoneNr.setText(phoneNr);
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        Calendar usersBirthday = Calendar.getInstance();
        try {
            usersBirthday.setTime(formatter.parse(birthday));
            // TODO instantiate DatePicker with style of Activity!!!! And usersBirthday!!!
        } catch (ParseException e) {
            // TODO
            e.printStackTrace();
        }



        return rootView;
    }
}