package io.github.lhernandez1848.plantpedia;

import android.content.Context;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;

public class GlobalMethods {

    private Context _context;

    public GlobalMethods(Context context){
        this._context = context;
    }

    public FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    void signOut() {
        // Firebase sign out
        firebaseAuth.signOut();

        Intent intent = new Intent(_context, StartActivity.class);
        intent.putExtra("action", "SIGN_OUT");

        _context.startActivity(intent);
    }
}
