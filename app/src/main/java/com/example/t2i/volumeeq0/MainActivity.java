package com.example.t2i.volumeeq0;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.AudioRecord   ;
import android.media.AudioTrack ;
import android.media.AudioFormat ;
import android.media.AudioManager ;
import android.media.audiofx.LoudnessEnhancer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.*;
import android.app.*;
import android.widget.*;
import android.content.*;
import android.media.audiofx.*;

import android.widget.Button;
import android.widget.Toast;
import android.widget.SeekBar;

import java.io.IOException;
import java.util.Random;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
//import static com.example.t2i.volumeeq0.BlankFragment.*;

import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import android.app.Activity;
import android.content.Context;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;


import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;

import android.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, NavigationView.OnNavigationItemSelectedListener,
        CompoundButton.OnCheckedChangeListener, View.OnClickListener, AdapterView.OnItemSelectedListener {

   // Intent intent;

    //EQ
    TextView bass_boost_label = null;
    SeekBar bass_boost = null;
    CheckBox enabled, buttonRTS, epReverb, buttonVirtualizer, eeReverb, noiseCancel  = null;
    Button flat = null;
    Equalizer eq = null;
    BassBoost bb = null;
    int min_level = 0;
    int max_level = 100;
    static final int MAX_SLIDERS = 8; // Must match the XML layout
    SeekBar sliders[] = new SeekBar[MAX_SLIDERS];
    TextView slider_labels[] = new TextView[MAX_SLIDERS];
    int num_sliders = 0;
    // Volume   
    SeekBar phoneSeekBar = null;
    public AudioManager audioManager;
    int seekValue, bufferSize, buffersize ;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder;
    Random random;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer;
    LoudnessEnhancer mLoudness;
    boolean isRecording;
    AudioRecord arec;
    public AudioTrack atrack;
    public byte[] buffer = new byte[buffersize];



    //  Effects
    PresetReverb pReverb = null;
    EnvironmentalReverb eReverb = null;
    Virtualizer virtualizer;
    Spinner spinner;
    String[] paths;
    SeekBar seekbarReverbDecayHF;
    SeekBar seekbarReverbDecayTime;
    SeekBar seekbarReverbDensity;
    SeekBar seekbarReverbDiffusion;
    SeekBar seekbarReverbRefLevel;
    SeekBar seekbarReverbRefDelay;
    SeekBar seekbarReverbDelay;
    SeekBar seekbarReverbLevel;
    SeekBar seekbarReverbRoomLevel;
    SeekBar seekbarReverbRoomHF;

    //tabs
    MenuInflater inflater;
   /* Toolbar toolbar;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    Fragment fragment =null;
    Class fragmentClass = null;
    FragmentManager fragmentManager;*/
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public GoogleApiClient client;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) // bilmiyorum nedir
    {
        inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       /* toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle( this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


       // fragmentClass = BlankFragment.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
*/
        //env.reverb

        seekbarReverbDecayHF = (SeekBar)findViewById(R.id.slider_ReverbDecayHF);
        seekbarReverbDecayHF.setOnSeekBarChangeListener(this);
        seekbarReverbDecayHF.setMax(1900);

        seekbarReverbDecayTime = (SeekBar)findViewById(R.id.slider_ReverbDecayTime);
        seekbarReverbDecayTime.setOnSeekBarChangeListener(this);
        seekbarReverbDecayTime.setMax(19900);

        seekbarReverbDensity = (SeekBar)findViewById(R.id.slider_ReverbDensity);
        seekbarReverbDensity.setOnSeekBarChangeListener(this);
        seekbarReverbDensity.setMax(1000);

        seekbarReverbDiffusion = (SeekBar)findViewById(R.id.slider_ReverbDiffusion);
        seekbarReverbDiffusion.setOnSeekBarChangeListener(this);
        seekbarReverbDiffusion.setMax(1000);

        seekbarReverbRefLevel = (SeekBar)findViewById(R.id.slider_ReverbRefLevel);
        seekbarReverbRefLevel.setOnSeekBarChangeListener(this);
        seekbarReverbRefLevel.setMax(10000);

        seekbarReverbRefDelay = (SeekBar)findViewById(R.id.slider_ReverbRefDelay);
        seekbarReverbRefDelay.setOnSeekBarChangeListener(this);
        seekbarReverbRefDelay.setMax(300);

        seekbarReverbDelay = (SeekBar)findViewById(R.id.slider_ReverbDelay);
        seekbarReverbDelay.setOnSeekBarChangeListener(this);
        seekbarReverbDelay.setMax(100);

        seekbarReverbLevel = (SeekBar)findViewById(R.id.slider_ReverbLevel);
        seekbarReverbLevel.setOnSeekBarChangeListener(this);
        seekbarReverbLevel.setMax(11000);

        seekbarReverbRoomLevel = (SeekBar)findViewById(R.id.slider_ReverbRoomLevel);
        seekbarReverbRoomLevel.setOnSeekBarChangeListener(this);
        seekbarReverbRoomLevel.setMax(9000);

        seekbarReverbRoomHF = (SeekBar)findViewById(R.id.slider_ReverbRoomHF);
        seekbarReverbRoomHF.setOnSeekBarChangeListener(this);
        seekbarReverbRoomHF.setMax(9000);

        eReverb = new EnvironmentalReverb(0, 0);
      /*  eReverb.setDecayHFRatio((short) 1000);
        eReverb.setDecayTime(10000);
        eReverb.setDensity((short) 1000);
        eReverb.setDiffusion((short) 1000);
        eReverb.setReflectionsLevel((short) -8500);
        eReverb.setReflectionsDelay(150);
        eReverb.setReverbDelay(100);
        eReverb.setReverbLevel((short) 1000);
        eReverb.setRoomLevel((short) -8500);
        eReverb.setRoomHFLevel((short) -4500);*/
        //eReverb.setEnabled(true);

        // Effects
        virtualizer = new Virtualizer(0, 0);
        pReverb = new  PresetReverb(0, 0);
        paths = new String[]{"Plate", "Large Hall", "Large Room", "Medium Hall", "Medium Room", "Small Room", "None"};
        buttonVirtualizer =(CheckBox)findViewById(R.id.virtualizer);
        buttonVirtualizer.setOnCheckedChangeListener (this);
        epReverb = (CheckBox)findViewById(R.id.prReverb);
        epReverb.setOnCheckedChangeListener (this);
        eeReverb = (CheckBox)findViewById(R.id.eReverb);
        eeReverb.setOnCheckedChangeListener (this);
        noiseCancel = (CheckBox)findViewById(R.id.buttonNoise);
        noiseCancel.setOnCheckedChangeListener (this);


        // volume
        bufferSize = 200000;
        final short[] buffer = new short[bufferSize];
        short[] readBuffer = new short[bufferSize];


        buttonRTS = (CheckBox)findViewById(R.id.buttonRTS);
        buttonRTS.setOnCheckedChangeListener (this);
        phoneSeekBar = (SeekBar)findViewById(R.id.seekBar1);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        buffersize = AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_CONFIGURATION_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);
        arec = new AudioRecord(MediaRecorder.AudioSource.MIC, 16000,
                AudioFormat.CHANNEL_CONFIGURATION_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, buffersize);
        atrack = new AudioTrack(AudioManager.STREAM_VOICE_CALL, 16000,
                AudioFormat.CHANNEL_CONFIGURATION_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, buffersize, AudioTrack.MODE_STREAM);
        atrack.setPlaybackRate(16000);


        random = new Random();

        phoneSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL));
        phoneSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL));

        try {
          /*  buttonRTS.setOnClickListener(new View.OnClickListener() {

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View view) {
                    myThread.start();
                }}); */
//                LoudnessEnhancer mLoudness = new LoudnessEnhancer(atrack.getAudioSessionId());
//

            phoneSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onProgressChanged(SeekBar seekbar, int i, boolean b) {
                    seekValue= i ;
                    //   mLoudness.setTargetGain(seekValue);
                    //   audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, i, 0);
                }

                @Override
                public void onStartTrackingTouch(SeekBar phoneSeekBar) {}
                @Override
                public void onStopTrackingTouch(SeekBar phoneSeekBar) {}
            });
        }
        catch (Exception e)
        { e.printStackTrace(); }

        // EQ

        enabled = (CheckBox)findViewById(R.id.enabled);
        enabled.setOnCheckedChangeListener (this);
        flat = (Button)findViewById(R.id.flat);
        flat.setOnClickListener(this);
        bass_boost = (SeekBar)findViewById(R.id.bass_boost);
        bass_boost.setOnSeekBarChangeListener(this);
        bass_boost_label = (TextView) findViewById (R.id.bass_boost_label);
        sliders[0] = (SeekBar)findViewById(R.id.slider_1);
        slider_labels[0] = (TextView)findViewById(R.id.slider_label_1);
        sliders[1] = (SeekBar)findViewById(R.id.slider_2);
        slider_labels[1] = (TextView)findViewById(R.id.slider_label_2);
        sliders[2] = (SeekBar)findViewById(R.id.slider_3);
        slider_labels[2] = (TextView)findViewById(R.id.slider_label_3);
        sliders[3] = (SeekBar)findViewById(R.id.slider_4);
        slider_labels[3] = (TextView)findViewById(R.id.slider_label_4);
        sliders[4] = (SeekBar)findViewById(R.id.slider_5);
        slider_labels[4] = (TextView)findViewById(R.id.slider_label_5);
        sliders[5] = (SeekBar)findViewById(R.id.slider_6);
        slider_labels[5] = (TextView)findViewById(R.id.slider_label_6);
        sliders[6] = (SeekBar)findViewById(R.id.slider_7);
        slider_labels[6] = (TextView)findViewById(R.id.slider_label_7);
        sliders[7] = (SeekBar)findViewById(R.id.slider_8);
        slider_labels[7] = (TextView)findViewById(R.id.slider_label_8);

        eq = new Equalizer(0, 0);
        if (eq != null)
        {
            eq.setEnabled (true);
            int num_bands = eq.getNumberOfBands();
            num_sliders = num_bands;
            short r[] = eq.getBandLevelRange();
            min_level = r[0];
            max_level = r[1];
            for (int i = 0; i < num_sliders && i < MAX_SLIDERS; i++)
            {
                int[] freq_range = eq.getBandFreqRange((short)i);
                sliders[i].setOnSeekBarChangeListener(this);
                slider_labels[i].setText (formatBandLabel (freq_range));
            }
        }

        for (int i = num_sliders ; i < MAX_SLIDERS; i++)
        {
            sliders[i].setVisibility(View.GONE);
            slider_labels[i].setVisibility(View.GONE);
        }

        bb = new BassBoost (0, 0);
        if (bb != null)
        {
        }
        else
        {
            bass_boost.setVisibility(View.GONE);
            bass_boost_label.setVisibility(View.GONE);
        }
        updateUI();

        // Effects
        spinner = (Spinner)findViewById(R.id.spinner1);
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item,paths);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.menu.menu_main) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            /*intent = new Intent(this, secondActivity.class);
            startActivity(intent);*/
            //finish();
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {


        }
       /* fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);*/
        return true;
    }
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        switch (position) {
            case 0:
                pReverb.setPreset( PresetReverb.PRESET_PLATE);
                break;
            case 1:
                pReverb.setPreset( PresetReverb.PRESET_LARGEHALL);
                break;
            case 2:
                pReverb.setPreset( PresetReverb.PRESET_LARGEROOM);
                break;
            case 3:
                pReverb.setPreset( PresetReverb.PRESET_MEDIUMHALL);
                break;
            case 4:
                pReverb.setPreset( PresetReverb.PRESET_MEDIUMROOM);
                break;
            case 5:
                pReverb.setPreset( PresetReverb.PRESET_SMALLROOM);
                break;
            case 6:
                pReverb.setPreset( PresetReverb.PRESET_NONE);
                break;
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    public native String stringFromJNI();

    @Override
    public void onProgressChanged (SeekBar seekBar, int level, boolean fromTouch)
    {

        if (seekBar == bass_boost)
        {
           if (level > 0){
               bb.setEnabled(true);
               bb.setStrength ((short)level); // Already in the right range 0-1000
           }
        }
        if (seekBar == seekbarReverbDecayHF){
            int levelDecayHF = level +100;
            eReverb.setDecayHFRatio((short) levelDecayHF);

        }

        if (seekBar == seekbarReverbDecayTime){
            int levelDecayTime = level +100;
            eReverb.setDecayTime(levelDecayTime);

        }

        if (seekBar == seekbarReverbDensity){
            int levelReverbDensity = 1000 - level;
            eReverb.setDensity( (short) levelReverbDensity);

        }

        if (seekBar == seekbarReverbDiffusion){
            int levelReverbDiffusion = 1000 - level;
            eReverb.setDiffusion((short) levelReverbDiffusion);

        }

        if (seekBar == seekbarReverbRefLevel){
            int levelReverbRefLevel = 10000 - level;
            eReverb.setReflectionsLevel((short) levelReverbRefLevel);

        }

        if (seekBar == seekbarReverbRefDelay){
            eReverb.setReflectionsDelay(level);

        }

        if (seekBar == seekbarReverbDelay){
            eReverb.setReverbDelay(level);

        }

        if (seekBar == seekbarReverbLevel){
            int levelReverbLevel = level -2000;
            eReverb.setReverbLevel((short) levelReverbLevel);

        }

        if (seekBar == seekbarReverbRoomLevel){
            int levelReverbRoomLevel = -level ;
            eReverb.setRoomLevel((short) levelReverbRoomLevel);

        }

        if (seekBar == seekbarReverbRoomHF){
            int levelReverbRoomHF = -level ;
            eReverb.setRoomHFLevel((short) levelReverbRoomHF);

        }



        if (eq != null)
        {
            int new_level = min_level + (max_level - min_level) * level / 100;

            for (int i = 0; i < num_sliders; i++)
            {
                if (sliders[i] == seekBar)
                {
                    eq.setBandLevel ((short)i, (short)new_level);
                    break;
                }
            }
        }
    }

    public String formatBandLabel (int[] band) {
        return milliHzToString(band[0]) + "-" + milliHzToString(band[1]); }

    public String milliHzToString (int milliHz) {
        if (milliHz < 1000) return "";
        if (milliHz < 1000000)
            return "" + (milliHz / 1000) + "Hz";
        else
            return "" + (milliHz / 1000000) + "kHz"; }

    public void updateSliders () {
        for (int i = 0; i < num_sliders; i++) {
            int level;
            if (eq != null)
                level = eq.getBandLevel ((short)i);
            else
                level = 0;
            int pos = 100 * level / (max_level - min_level) + 50;
            sliders[i].setProgress (pos);
        } }

    public void updateBassBoost () {
        if (bb != null)
            bass_boost.setProgress (bb.getRoundedStrength());
        else
            bass_boost.setProgress (0); }

    /*final public Thread myThread = new Thread(new Runnable() {
        @Override
        public void run() {
            rts();
        }
    });
    public void rts() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
      // isRecording = true;
      //  atrack.setPlaybackRate(16000);
        arec.startRecording();
        atrack.play();
        while (isRecording) {
            arec.read(buffer, 0, buffersize);
            atrack.write(buffer, 0, buffer.length);
            // atrack.setVolume(seekValue);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, seekValue, 0);
        }
    }*/
    

    public String CreateRandomAudioFileName(int string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        int i = 0;
        while (i < string) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));
            i++;
        }
        return stringBuilder.toString();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(MainActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }


    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch(view.getId()) {
            case R.id.buttonRTS:
                if (checked){
                //    myThread.start();
                    Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);
                    atrack.setPlaybackRate(16000);
                    arec.startRecording();
                    atrack.play();
                   while (isRecording) {
                    arec.read(buffer, 0, buffersize);
                    atrack.write(buffer, 0, buffer.length);
                       audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, seekValue, 0);
                    //isRecording=true;
                   }  } else {
                   // isRecording=false;
                    atrack.flush();
                    atrack.stop();
                    atrack.release();
                    arec.stop();
                    arec.release();
                } break;

            case R.id.prReverb:
                if (checked){
                    pReverb.setEnabled(true);
                    atrack.attachAuxEffect(pReverb.getId());
                    atrack.setAuxEffectSendLevel(1.0f);
                }else {
                    pReverb.setEnabled(false);
                }
                break;

            case R.id.enabled:
                if (checked){
                    eq.setEnabled(true);
                    isRecording=true;
                } else {
                    eq.setEnabled(false);
                    isRecording=false;

                } break;

            case R.id.virtualizer:
                if (checked){
                   virtualizer.setEnabled(true);
                    atrack.attachAuxEffect(virtualizer.getId());
                    atrack.setAuxEffectSendLevel(1.0f);
                } else {
                    virtualizer.setEnabled(false);
                } break;

            case R.id.eReverb:
                if (checked){
                    eReverb.setEnabled(true);
                    atrack.attachAuxEffect(eReverb.getId());
                    atrack.setAuxEffectSendLevel(1.0f);
                } else {
                    eReverb.setEnabled(false);
                } break;

            case R.id.buttonNoise:
                if (checked){
                    eReverb.setEnabled(true);
                    atrack.attachAuxEffect(eReverb.getId());
                    atrack.setAuxEffectSendLevel(1.0f);
                } else {
                    eReverb.setEnabled(false);
                } break;
        }
    }

    @Override
    public void onCheckedChanged (CompoundButton view, boolean isChecked) {}

    public void onClick (View view) {
        if (view ==  flat)
        { setFlat(); } }

    public void updateUI () {
        updateSliders();
        updateBassBoost();
        enabled.setChecked (eq.getEnabled()); }

    public void setFlat () {
        if (eq != null) {
            for (int i = 0; i < num_sliders; i++)
            {
                eq.setBandLevel ((short)i, (short)0);
            } }

        if (bb != null)
        {
            bb.setEnabled (false);
            bb.setStrength ((short)0);
        }

        updateUI(); }

    public void showAbout ()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("About Simple EQ");
        alertDialogBuilder.setMessage("selman");
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setPositiveButton ("ok",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                    }
                });
        AlertDialog ad = alertDialogBuilder.create();
        ad.show();

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }



    // Effects



}