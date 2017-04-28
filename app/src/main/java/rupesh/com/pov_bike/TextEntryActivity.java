package rupesh.com.pov_bike;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;


public class TextEntryActivity extends ActionBarActivity {

    Button btnOn, btnOff, btnDis,povbtn;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    ImageView vv;
    TextView xx;
    String arraysent;
    Button atdisp;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(list_of_sketch.EXTRA_ADDRESS); //receive the address of the bluetooth device

        setContentView(R.layout.activity_text);

        this.setRequestedOrientation(
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        btnOn = (Button) findViewById(R.id.button2c);
        btnOff = (Button) findViewById(R.id.button3c);
        btnDis = (Button) findViewById(R.id.button4c);
        atdisp = (Button) findViewById(R.id.button5c);
        povbtn = (Button) findViewById(R.id.button6c) ;
        new ConnectBT().execute();

        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                turnOnLed();//method to turn on
            }
        });

        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOffLed();   //method to turn off
            }
        });

        atdisp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               new ATled().execute();   //method to turn off
            }
        });

        btnDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disconnect(); //close connection
            }
        });

        povbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new povbuttn().execute(); //close connection
            }
        });



    }

    private void Disconnect() {
        if (btSocket != null) //If the btSocket is busy
        {
            try {
                btSocket.close(); //close connection
                msg("Disconnected");
            } catch (IOException e) {
                msg("Error");
            }
        }
        // finish(); //return to the first layout

    }

    private void turnOffLed() {
        if (btSocket != null && isBtConnected) {

            new turnoffm().execute();
            // btSocket.getOutputStream().write("TF".toString().getBytes());


        }
    }

    private void turnOnLed() {
        if (btSocket != null && isBtConnected) {
            new turnonm().execute();
            // xx.append("g");
        } else {
            msg("not connected_hit back button and try again");

        }

    }


    // fast way to call Toast
    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_led_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(TextEntryActivity.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                //finish();
            } else {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }


    public Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }


    public int[][] converter(Bitmap bmp) {


        Bitmap bmpscaled = Bitmap.createScaledBitmap(bmp, 27, 27, false);
        // Bitmap bmpscaled = toGrayscale(bmpscaleed);

        int[][][] real = new int[bmpscaled.getWidth()][bmpscaled.getWidth()][4];

        for (int i = 0; i < bmpscaled.getWidth(); i++) {
            for (int j = 0; j < bmpscaled.getHeight(); j++) {


                int colour = bmpscaled.getPixel(i, j);

                real[i][j][0] = Color.alpha(colour);
                real[i][j][1] = Color.red(colour);
                real[i][j][2] = Color.green(colour);
                real[i][j][3] = Color.blue(colour);


            }

        }


        for (int c = 0; c < bmpscaled.getWidth(); c++) {
            for (int d = 0; d < bmpscaled.getHeight(); d++) {
                real[c][d][1] = (real[c][d][1] + real[c][d][2] + real[c][d][3]) / 3;
            }

        }
        Double NumberOfSpoke = 102.0;
        int b1 = 0, b2 = 0, b3 = 0, b4 = 0, c1 = 0, c2 = 0, c3 = 0, c4 = 0, l = 0;
        Double SpokeInOneSection = NumberOfSpoke / 4.0;

        int[][] Section1 = new int[SpokeInOneSection.intValue() + 1][13];
        int[][] Section2 = new int[SpokeInOneSection.intValue() + 1][13];
        int[][] Section3 = new int[SpokeInOneSection.intValue() + 1][13];
        int[][] Section4 = new int[SpokeInOneSection.intValue() + 1][13];
        Double Angle = 360.0 / NumberOfSpoke;
        int TotalSize;
        int numberOfLed = 13;
        int temp1 = 0, temp2 = 0, temp3 = 0, temp4 = 0;
        for (Double Theta = 45.0; Theta > -135.0; Theta = Theta - Angle) {

            Double Slope = Math.tan(Math.toRadians(Theta));

            if (Math.abs(Slope) <= 1) {
                Double c;
                int yy = 0;

                for (int xx = -12; xx <= 0; xx++) {
                    c = Slope * xx;
                    yy = c.intValue();

                    if (real[xx + 12][12 - yy][1] > 127) {

                        Section3[temp1][Math.abs(xx)] = 0;

                    } else {
                        Section3[temp1][Math.abs(xx)] = 1;
                        b3++;
                    }
                }
                temp1++;
                for (int xx = 0; xx <= 12; xx++) {
                    c = Slope * xx;
                    yy = c.intValue();

                    if (real[xx + 12][12 - yy][1] > 127) {
                        Section1[temp2][Math.abs(xx)] = 0;
                    } else {
                        Section1[temp2][Math.abs(xx)] = 1;

                    }
                }
                temp2++;

            }
            if (Math.abs(Slope) >= 1) {
                Double c;
                int xx = 0;
                for (int yy = -12; yy <= 12; yy++) {
                    c = yy / Slope;

                    xx = c.intValue();
                    if (yy <= 0) {

                        if (real[xx + 12][12 - yy][1] > 127) {
                            Section2[temp3][Math.abs(yy)] = 0;
                        } else {
                            Section2[temp3][Math.abs(yy)] = 1;
                            b2++;
                        }

                    }
                    if (yy >= 0) {

                        if (real[xx + 12][12 - yy][1] > 127) {
                            Section4[temp4][Math.abs(yy)] = 0;
                            l++;
                        } else {
                            Section4[temp4][Math.abs(yy)] = 1;

                        }

                    }

                }
                temp3++;
                temp4++;
            }


        }


        TotalSize = temp1 + temp2 + temp3 + temp4;

        int[][] Final = new int[TotalSize][13];
        for (int i = 0; i < temp2; i++) {

            for (int j = 0; j < 13; j++) {
                Final[temp1 + i][j] = Section1[i][j];
                if (Section1[i][j] == 1)
                    c1++;
                //xx.append(String.valueOf(Final[temp1+i][j] +" "));
            }

        }

        //System.out.println(c1);


        for (int i = 0; i < temp3; i++) {
            //10 to 13
            for (int j = 0; j < 13; j++) {
                Final[temp1 + temp2 + i][j] = Section2[i][j];
                if (Section2[i][j] == 1)
                    c2++;
            }
        }

        //System.out.println(c2);
        for (int i = 0; i < temp1; i++) {

            //10 to 13
            for (int j = 0; j < 13; j++) {
                Final[i][j] = Section3[i][j];
                if (Section3[i][j] == 1)
                    c3++;
            }
        }
        //System.out.println(c3);
        int k = 0;

        for (int i = 0; i < temp4; i++) {

            //10 to13
            for (int j = 0; j < 13; j++) {
                k++;
                Final[temp1 + temp2 + temp3 + i][j] = Section4[i][j];
                //   System.out.println(Section4[i][j] + "kk" + c4 + " " + k);
                if (Section4[i][j] == 1)
                    c4++;
            }
        }
        //   System.out.println(c4);

        // System.out.println(temp4 + " vvv" + k);

        //System.out.println(pp);*/


        return Final;

    }


    private class turnonm extends AsyncTask<Object, Object, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected Void doInBackground(Object... voids) {

            int[][] xxc = new int[102][13];
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.heartb);
            for (int i = 0; i < 102; i++) {
                for (int j = 0; j < 13; j++) {
                    if (i == 7) {
                        xxc[i][j] = 1;
                    } else if (i >= 0 && i < 13 && j == 12) {
                        xxc[i][j] = 1;
                    } else
                        xxc[i][j] = 0;

                    try {
                        converter(largeIcon);
                        converter(largeIcon);
                        converter(largeIcon);
                        btSocket.getOutputStream().write(String.valueOf(xxc[i][j]).getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                converter(largeIcon);
            }


            return null;
        }


        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(TextEntryActivity.this, "Uploading the image...", "Please wait!!!");  //show a progress dialog
        }


        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed.Try again.");
                //finish();
            } else {
                msg("uploaded.");
            }

            progress.dismiss();
        }


    }


    private class turnoffm extends AsyncTask<Object, Object, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected Void doInBackground(Object... voids) {

            int[][] xxc = new int[102][13];
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.heartb);
            for (int i = 0; i < 102; i++) {
                for (int j = 0; j < 13; j++) {
                    if (i >= 15 && i < 27) {
                        xxc[i][i - 15] = 1;
                    } else if (i >= 28 && i < 40) {
                        xxc[i][40 - i] = 1;
                    } else
                        xxc[i][j] = 0;

                    if (i >= 23 && i < 32)
                        xxc[i][8] = 1;


                    try {
                        converter(largeIcon);
                        converter(largeIcon);
                        converter(largeIcon);
                        btSocket.getOutputStream().write(String.valueOf(xxc[i][j]).getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                converter(largeIcon);
            }


            return null;
        }


        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(TextEntryActivity.this, "Uploading the image...", "Please wait!!!");  //show a progress dialog
        }


        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed.Try again.");
                //finish();
            } else {
                msg("uploaded.");
            }

            progress.dismiss();
        }


    }


    private class ATled extends AsyncTask<Object, Object, Void> {
        private boolean ConnectSuccess = true;

        @Override
        protected Void doInBackground(Object... voids) {

            int[][] xxc = new int[102][13];
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.heartb);
            for (int i = 0; i < 102; i++) {
                for (int j = 0; j < 13; j++) {

                    if (i == 7) {
                        xxc[i][j] = 1;
                    } else if (i >= 0 && i < 13 && j == 12) {
                        xxc[i][j] = 1;
                        if (i >= 15 && i < 27) {
                            xxc[i][i - 15] = 1;
                        } else if (i >= 28 && i < 40) {
                            xxc[i][40 - i] = 1;
                        } else
                            xxc[i][j] = 0;

                        if (i >= 23 && i < 32)
                            xxc[i][8] = 1;


                        try {
                            converter(largeIcon);
                            converter(largeIcon);
                            converter(largeIcon);
                            btSocket.getOutputStream().write(String.valueOf(xxc[i][j]).getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
                    converter(largeIcon);

            }

                return null;
            }


            @Override
            protected void onPreExecute ()
            {
                progress = ProgressDialog.show(TextEntryActivity.this, "Uploading the image...", "Please wait!!!");  //show a progress dialog
            }


            @Override
            protected void onPostExecute (Void
            result) //after the doInBackground, it checks if everything went fine
            {
                super.onPostExecute(result);

                if (!ConnectSuccess) {
                    msg("Connection Failed.Try again.");
                    //finish();
                } else {
                    msg("uploaded.");
                }

                progress.dismiss();
            }


        }

    private class povbuttn extends AsyncTask<Object, Object, Void> {
        private boolean ConnectSuccess = true;


        @Override
        protected Void doInBackground(Object... voids) {

            int[][] Final = new int[102][13];
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.heartb);
            for (int i = 0; i < 102; i++) {
                for (int j = 0; j < 13; j++) {
                    if(i>=0 && i<=6 && (j==12-i))
                    {

                        Final[i][j] = 1;
                    }
                    else
                    if( i>6 && i<=12 &&(j==2*i-12))
                    {
                        Final[i][j] =1;
                    }
                    else
                    if(i>=22 && i<29 && (j==i-14 ||j==28-i))
                    {
                        Final[i][j]=1;
                    }
                    else
                    if(i>=29 && i<36 && (j==i-29|| j==41-i)){
                        Final[i][j]=1;
                    }
                    else
                    if(i>=45 && i<=49 && (j==i-37)){
                        Final[i][j]=1;
                    }
                    else
                    if(i>=45 && i<=49 &&(j==53-i)){
                        Final[i][j]=1;
                    }
                    else
                    if(i==51 || i==52){
                        Final[i][j]=1;
                    }
                    else Final[i][j] = 0;

                    try {
                        converter(largeIcon);
                        converter(largeIcon);
                        converter(largeIcon);
                        btSocket.getOutputStream().write(String.valueOf(Final[i][j]).getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                converter(largeIcon);
            }


            return null;
        }


        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(TextEntryActivity.this, "Uploading the image...", "Please wait!!!");  //show a progress dialog
        }


        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed.Try again.");
                //finish();
            } else {
                msg("uploaded.");
            }

            progress.dismiss();
        }


    }



}
