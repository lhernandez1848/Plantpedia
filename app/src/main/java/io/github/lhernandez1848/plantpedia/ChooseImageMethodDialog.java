package io.github.lhernandez1848.plantpedia;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatDialogFragment;

public class ChooseImageMethodDialog extends AppCompatDialogFragment
        implements View.OnClickListener{

    private Button btnViewGallery, btnTakePhoto, btnCancel;
    private ChooseImageMethodDialogListener listener;
    private String imageMethodChoice;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_image_method, null);

        builder.setView(view)
                .setTitle("Choose Image Method");

        // initialize widgets
        btnViewGallery = view.findViewById(R.id.btnViewGallery);
        btnTakePhoto = view.findViewById(R.id.btnTakePhoto);
        btnCancel = view.findViewById(R.id.btnCancel);

        btnViewGallery.setOnClickListener(this);
        btnTakePhoto.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

        imageMethodChoice = "";

        return builder.create();
    }

    @Override
    public void onAttach(Context _context) {
        super.onAttach(_context);

        try {
            listener = (ChooseImageMethodDialogListener) _context;
        } catch (ClassCastException e) {
            throw new ClassCastException(_context.toString() + "ChooseImageMethodDialogListener not implemented");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnViewGallery:
                imageMethodChoice = "gallery";
                listener.applyImageMethodChoice(imageMethodChoice);
                dismiss();
                break;
            case R.id.btnTakePhoto:
                imageMethodChoice = "camera";
                listener.applyImageMethodChoice(imageMethodChoice);
                dismiss();
                break;
            case R.id.btnCancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    public interface ChooseImageMethodDialogListener{
        void applyImageMethodChoice (String imageMethodChoice);
    }
}

