package io.github.lhernandez1848.plantpedia;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;

import org.joda.time.DateTimeComparator;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class GlobalMethods {

    private Context _context;

    public GlobalMethods(Context context){
        this._context = context;
    }

    public FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    public void signOut() {
        // Firebase sign out
        firebaseAuth.signOut();

        Intent intent = new Intent(_context, StartActivity.class);
        intent.putExtra("action", "SIGN_OUT");

        _context.startActivity(intent);
    }


    public Boolean isWateringDay(int day, int month, int year, int waterFreq){
        Date cDate = new Date();
        Date lwDate = new GregorianCalendar(year, month - 1, day).getTime();

        Calendar waterCalendar = Calendar.getInstance();
        waterCalendar.setTime(lwDate);
        waterCalendar.add(Calendar.DAY_OF_MONTH, waterFreq);

        Date rDate = waterCalendar.getTime();

        return DateTimeComparator.getDateOnlyInstance().compare(cDate, rDate) == 0;
    }
}
