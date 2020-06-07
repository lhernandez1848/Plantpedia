package io.github.lhernandez1848.plantpedia;

import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity {

    private Plantpedia app;
    RelativeLayout relLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Home");

        relLayout = (RelativeLayout) findViewById(R.id.mainLayout);

        // initialize app to start notification service
        app = (Plantpedia) getApplication();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, new MainActivityFragment())
                .commit();

    }

    @Override
    public void onBackPressed() {
        // Clear any existing layouts before popping the stack.
        if (relLayout != null) {
            relLayout.removeAllViews();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.getBackStackEntryCount() > 1 ) {
            fragmentManager.popBackStack();
            return;
        }

        // Exit the app if there are no more fragments.
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
