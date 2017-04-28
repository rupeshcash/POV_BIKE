package rupesh.com.pov_bike;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by rupesh kashyap on 4/1/2017.
 */

public class list_of_sketch extends Activity implements View.OnClickListener {


    private Boolean isFabOpen = false;

    private ProgressDialog progress;
    private static final int SELECT_PICTURE = 100;
    ArrayList<sketch_obj> f = new ArrayList<sketch_obj>();// list of file paths
    File[] listFile;
    sketch_adapter msketch_adapter;
    public static String EXTRA_ADDRESS = "device_address";
    String address = null;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private FloatingActionButton fab, fab1, fab2,fab3;
    ListView listView;
    //receive the address of the bluetooth device
    public static String bitmaps = "imagetogo";



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View emptyview= findViewById(R.id.empty_view212);
        //new lol().execute();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);


        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);

        getFromSdcard();

        msketch_adapter = new sketch_adapter(this, f, R.color.category_numbers);
        listView = (ListView) findViewById(R.id.list_view_sketch);
        listView.setAdapter(msketch_adapter);
        msketch_adapter.notifyDataSetChanged();
        listView.setEmptyView(emptyview);
        registerForContextMenu(listView);

        listView.setOnItemClickListener((new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //
                msketch_adapter.notifyDataSetChanged();
                Intent cv = new Intent(list_of_sketch.this, ledControl.class);
                Bitmap bitmap = BitmapFactory.decodeFile(msketch_adapter.getItem(i).getImageId());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                cv.putExtra(bitmaps, byteArray);
                cv.putExtra(EXTRA_ADDRESS, address);
                startActivity(cv);

            }
        }));

    }

    public void animateFAB() {

        if (isFabOpen) {

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            isFabOpen = false;

        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            isFabOpen = true;

        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.list_view_sketch) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(f.get(info.position).getName());
            String[] menuItems = getResources().getStringArray(R.array.menu_items);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }


        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.menu_items);
        String menuItemName = menuItems[menuItemIndex];
        String listItemName = f.get(info.position).getName();

        //TextView text = (TextView)findViewById(R.id.footer);
        //text.setText(String.format("Selected %s for item %s", menuItemName, listItemName));

        switch (item.getItemId()) {
            case 0: {
                Toast.makeText(getApplicationContext(), "delete", Toast.LENGTH_SHORT).show();
                //adapter.remove(adapter.getItem(info.position));
                File file = new File(f.get(info.position).getImageId());
                msketch_adapter.remove(msketch_adapter.getItem(info.position));
                boolean deleted = file.delete();

            }
            case 1: {

                Intent xx = new Intent(list_of_sketch.this, MainActivity.class);

            }


        }

        return true;
    }

    public void getFromSdcard() {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File file = new File(getCacheDir(), "led_Paint");
        file.mkdir();
        if (file.isDirectory()) {
            listFile = file.listFiles();


            if (listFile != null)
                for (int i = 0; i < listFile.length; i++) {

                    String filename = listFile[i].getAbsolutePath().substring(listFile[i].getAbsolutePath().lastIndexOf("/") + 1);


                    {
                        sketch_obj xx = new sketch_obj(filename, listFile[i].getAbsolutePath());
                        f.add(xx);
                    }


                }

        }

        /*{
            String imageUri = "drawable://" + R.drawable.heartb;
                sketch_obj vv = new sketch_obj("heart1", imageUri);
            f.add(vv);
            imageUri = "drawable://" + R.drawable.heartxx;
            vv = new sketch_obj("heart2", imageUri);
            f.add(vv);

        }*/
    }

    private String saveToInternalStorage(Bitmap bitmapImage, String filename) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());

        // create a File object for the parent directory
        File path = new File(getCacheDir(), "led_Paint");
        path.mkdir();
        // Create imageDir


        File mypath = new File(path, filename);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath.getAbsolutePath());

            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 50, fos);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
                Toast savedToast = Toast.makeText(getApplicationContext(),
                        "no to Gallery!", Toast.LENGTH_SHORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath.getAbsolutePath();
    }

    //starts the gallery thing
    void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);

    }

    //gallery selector activity result
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {

                Uri selectedImageUri = data.getData();

                if (null != selectedImageUri) {

                    // Saving to Database...

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                        String namegallery = getFileName(selectedImageUri);
                        sketch_obj xx = new sketch_obj(namegallery, selectedImageUri.getPath());
                        saveToInternalStorage(bitmap, namegallery);
                        f.add(xx);
                        msketch_adapter.notifyDataSetChanged();
                        showMessage("added to list");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                // Reading from Database after 3 seconds just to show the message

            }
        }

    }


    //returns the file name once uri is given
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    // Show simple message using SnackBar
    void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        //savedToast.show();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.fab:
                animateFAB();
                break;
            case R.id.fab1:
                openImageChooser();
                break;
            case R.id.fab2:
                Intent v = new Intent(list_of_sketch.this, TextEntryActivity.class);
                v.putExtra(EXTRA_ADDRESS, address);
                startActivity(v);
                finish();
               break;
            case R.id.fab3:
                Intent cv = new Intent(list_of_sketch.this, MainActivity.class);
                cv.putExtra(EXTRA_ADDRESS, address);
                startActivity(cv);
                finish();
                break;

        }
    }

    @Override
    public void onBackPressed() {
        Intent x = new Intent(list_of_sketch.this, DeviceList.class);
        startActivity(x);
    }


    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }


}