/*Purpose: this prompt is to display upon failure of google login, providing users with options to
           cancel and return to ordinary login, or try again, and redirect to the google login page
TODO - Update styles and appearance to match rest of app upon definition
*/

package team16.cs307.expensetracker.DialogFragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.app.Dialog;
import android.support.v7.app.AlertDialog;

public class LoginFailureDialogFragment extends DialogFragment {
    //default empty constructor only, no additional info to pass in here
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder loginFail = new AlertDialog.Builder(getActivity());
        loginFail.setMessage("Google Login Failed")
                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss & back to google login page - TODO upon creation of login pages
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss & back to main login page - TODO upon creation of login pages
                        dialog.dismiss();
                    }
                });
        return loginFail.create();
    }
}
