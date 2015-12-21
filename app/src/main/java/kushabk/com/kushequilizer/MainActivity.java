package kushabk.com.kushequilizer;

import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener,
        CompoundButton.OnCheckedChangeListener,
        View.OnClickListener {


    private SeekBar bass_boost = null;
    private CheckBox enabled = null;
    private Button flat = null;

    private Equalizer eq = null;
    private BassBoost bb = null;

    private int min_level = 0;
    private int max_level = 100;

    static final int MAX_SLIDERS = 8;
    private SeekBar sliders[] = new SeekBar[MAX_SLIDERS];
    private TextView slider_labels[] = new TextView[MAX_SLIDERS];
    int num_sliders = 0;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        enabled = (CheckBox) findViewById(R.id.enabled);
        enabled.setOnCheckedChangeListener(this);

        flat = (Button) findViewById(R.id.flat);
        flat.setOnClickListener(this);

        bass_boost = (SeekBar) findViewById(R.id.bass_boost);
        bass_boost.setOnSeekBarChangeListener(this);
        TextView bass_boost_label = (TextView) findViewById(R.id.bass_boost_label);

        sliders[0] = (SeekBar) findViewById(R.id.slider_1);
        slider_labels[0] = (TextView) findViewById(R.id.slider_label_1);
        sliders[1] = (SeekBar) findViewById(R.id.slider_2);
        slider_labels[1] = (TextView) findViewById(R.id.slider_label_2);
        sliders[2] = (SeekBar) findViewById(R.id.slider_3);
        slider_labels[2] = (TextView) findViewById(R.id.slider_label_3);
        sliders[3] = (SeekBar) findViewById(R.id.slider_4);
        slider_labels[3] = (TextView) findViewById(R.id.slider_label_4);
        sliders[4] = (SeekBar) findViewById(R.id.slider_5);
        slider_labels[4] = (TextView) findViewById(R.id.slider_label_5);
        sliders[5] = (SeekBar) findViewById(R.id.slider_6);
        slider_labels[5] = (TextView) findViewById(R.id.slider_label_6);
        sliders[6] = (SeekBar) findViewById(R.id.slider_7);
        slider_labels[6] = (TextView) findViewById(R.id.slider_label_7);
        sliders[7] = (SeekBar) findViewById(R.id.slider_8);
        slider_labels[7] = (TextView) findViewById(R.id.slider_label_8);

        eq = new Equalizer(0, 0);
        eq.setEnabled(true);
        num_sliders = (int) eq.getNumberOfBands();
        short r[] = eq.getBandLevelRange();
        min_level = r[0];
        max_level = r[1];
        for (int i = 0; i < num_sliders && i < MAX_SLIDERS; i++) {
            int[] freq_range = eq.getBandFreqRange((short) i);
            sliders[i].setOnSeekBarChangeListener(this);
            slider_labels[i].setText(formatBandLabel(freq_range));
        }
        for (int i = num_sliders; i < MAX_SLIDERS; i++) {
            sliders[i].setVisibility(View.GONE);
            slider_labels[i].setVisibility(View.GONE);
        }

        bb = new BassBoost(0, 0);

        updateUserInterface();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int level, boolean fromTouch) {
        if (seekBar == bass_boost) {
            bb.setEnabled(level > 0);
            bb.setStrength((short) level); // Already in the right range 0-1000
        } else if (eq != null) {
            int new_level = min_level + (max_level - min_level) * level / 100;

            for (int i = 0; i < num_sliders; i++) {
                if (sliders[i] == seekBar) {
                    eq.setBandLevel((short) i, (short) new_level);
                    break;
                }
            }
        }
    }


    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    public String formatBandLabel(int[] band) {
        return milliHzToString(band[0]) + "-" + milliHzToString(band[1]);
    }


    public String milliHzToString(int milliHz) {
        if (milliHz < 1000) return "";
        if (milliHz < 1000000)
            return "" + (milliHz / 1000) + "Hz";
        else
            return "" + (milliHz / 1000000) + "kHz";
    }


    public void updateSliders() {
        for (int i = 0; i < num_sliders; i++) {
            int level;
            if (eq != null)
                level = eq.getBandLevel((short) i);
            else
                level = 0;
            int pos = 100 * level / (max_level - min_level) + 50;
            sliders[i].setProgress(pos);
        }
    }

    public void updateBassBoost() {
        if (bb != null)
            bass_boost.setProgress(bb.getRoundedStrength());
        else
            bass_boost.setProgress(0);
    }


    @Override
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        if (view == enabled) {
            eq.setEnabled(isChecked);
        }
    }


    @Override
    public void onClick(View view) {
        if (view == flat) {
            setFlat();
        }
    }


    public void updateUserInterface() {
        updateSliders();
        updateBassBoost();
        enabled.setChecked(eq.getEnabled());
    }


    public void setFlat() {
        if (eq != null) {
            for (int i = 0; i < num_sliders; i++) {
                eq.setBandLevel((short) i, (short) 0);
            }
        }

        if (bb != null) {
            bb.setEnabled(false);
            bb.setStrength((short) 0);
        }

        updateUserInterface();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://kushabk.com.kushequilizer/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://kushabk.com.kushequilizer/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
