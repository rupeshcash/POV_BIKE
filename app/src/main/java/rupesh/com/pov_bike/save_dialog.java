package rupesh.com.pov_bike;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by rupesh kashyap on 4/3/2017.
 */

public class save_dialog extends Dialog implements View.OnClickListener {

    public Activity c;
    public Button save;
    EditText xx;
    public save_dialog(Activity t) {
        super(t);
        this.c =t;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.savedialogfinal);
        save = (Button) findViewById(R.id.declineButton);
        save.setOnClickListener(this);
        xx =(EditText) findViewById(R.id.edittextDialog);
    }
        @Override
    public void onClick(View view) {
            dismiss();


    }
}
