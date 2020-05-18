package io.github.lhernandez1848.plantpedia;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatDialogFragment;

public class WaterDateDialog extends AppCompatDialogFragment
        implements View.OnClickListener{

    private Button btnYesWatered, btnNoWatered;
    private WaterDateDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_water_date, null);

        builder.setView(view);

        // initialize widgets
        btnYesWatered = view.findViewById(R.id.btnYesWatered);
        btnNoWatered = view.findViewById(R.id.btnNoWatered);

        btnYesWatered.setOnClickListener(this);
        btnNoWatered.setOnClickListener(this);

        return builder.create();
    }

    @Override
    public void onAttach(Context _context) {
        super.onAttach(_context);

        try {
            listener = (WaterDateDialogListener) _context;
        } catch (ClassCastException e) {
            throw new ClassCastException(_context.toString() + "WaterDateDialogListener not implemented");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnYesWatered:
                listener.applyWaterDate();
                dismiss();
                break;
            case R.id.btnNoWatered:
                dismiss();
                break;
            default:
                break;
        }
    }

    public interface WaterDateDialogListener{
        void applyWaterDate();
    }
}

