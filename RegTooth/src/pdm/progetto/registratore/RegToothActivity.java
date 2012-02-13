package pdm.progetto.registratore;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class RegToothActivity extends Activity
{
    /** Called when the activity is first created. */
	private Calendar today = Calendar.getInstance(Locale.getDefault());
	private ToggleButton btnrec;
	private ImageButton btnstop;
	private TextView testoDisplay;
	private LinearLayout ldisplay;
	private Chronometer cronometro;
	private AudioRecorder mr;
	private int i = 0;
	private String path="-VR-"+today.get(Calendar.YEAR)+"-"+(today.get(Calendar.MONTH)+1)+
			"-"+today.get(Calendar.DAY_OF_MONTH)+"_"+today.get(Calendar.HOUR_OF_DAY)+today.get(Calendar.MINUTE);
	private boolean verificareg = false;
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    	final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        ldisplay = (LinearLayout) findViewById(R.id.Layoutdisplay);
        testoDisplay = (TextView) findViewById(R.id.textDisplay);
        btnrec = (ToggleButton) findViewById(R.id.imageRec);
        btnstop = (ImageButton) findViewById(R.id.imageStop);
        ListView lv = (ListView) findViewById(R.id.listView1);
        cronometro = (Chronometer) findViewById(R.id.chronometer1);
        cronometro.setText("00:00");
        String[] arrayData = getResources().getStringArray(R.array.array_data);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				this, R.layout.simple_row, R.id.labelItem, arrayData) 
				{
					public View getView(int position, View convertView, ViewGroup parent) 
					{
						View adapterView = super.getView(position, convertView, parent);
						ImageView imageItem = (ImageView)adapterView.findViewById(R.id.immagineLista);
						if(position == 0)
						{
							imageItem.setImageResource(R.drawable.option2);
						}
						else {
							imageItem.setImageResource(R.drawable.lista2);
						}
						return adapterView;
					}
				};
		lv.setAdapter(arrayAdapter);
		btnrec.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
				// TODO Auto-generated method stub
//	REC: CODICE		
				if(isChecked) {
					btnrec.setBackgroundResource(R.drawable.pausa2);
					testoDisplay.setText(R.string.rec_display);
					testoDisplay.setTextColor(getResources().getColor(R.color.rosso));
					ldisplay.setBackgroundColor(getResources().getColor(R.color.rossopaco));
//	START CRONOMETRO					
				    int stoppedMilliseconds = 0;

				    	String chronoText = cronometro.getText().toString();
				        String array[] = chronoText.split(":");
				        if (array.length == 2) {
				        	stoppedMilliseconds = Integer.parseInt(array[0]) * 60 * 1000
				            + Integer.parseInt(array[1]) * 1000;
				        } else if (array.length == 3) {
				        	stoppedMilliseconds = Integer.parseInt(array[0]) * 60 * 60 * 1000 
				            + Integer.parseInt(array[1]) * 60 * 1000
				            + Integer.parseInt(array[2]) * 1000;
				        }

				        cronometro.setBase(SystemClock.elapsedRealtime() - stoppedMilliseconds);
				        cronometro.start();
//	FINE START CRONOMETRO	
//	START REGISTRAZIONE 
					try {
						i++;
						mr=new AudioRecorder(String.format("%03d", i)+path, prefs.getString("formato", "No Text Value"));
						mr.start(prefs.getString("formato", "ERROR"),prefs.getString("qualita", "ERROR"));
						verificareg = true;
						} catch (IOException e) {
							e.printStackTrace();
						} 
//	FINE REGISTRAZIONE
				}
//	FINE REC: CODICE
//	PAUSA: CODICE				
				else {
					btnrec.setBackgroundResource(R.drawable.record2);
					testoDisplay.setTextColor(getResources().getColor(R.color.testoAndroid));
					testoDisplay.setTextColor(getResources().getColor(R.color.rosso));
					testoDisplay.setText(R.string.pausa);
					cronometro.stop(); //si ferma il cronometro
					verificareg = false;
				}
			}
		});
//	STOP: CODICE		
		btnstop.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				if(verificareg)
				{
				btnrec.setChecked(false);
				testoDisplay.setText(R.string.stop_display);
				ldisplay.setBackgroundColor(getResources().getColor(android.R.color.background_dark));
				testoDisplay.setTextColor(getResources().getColor(R.color.testoAndroid));
/*RESET -->*/	cronometro.setBase(SystemClock.elapsedRealtime());
				//Bisogna mettere il toast dentro l'if. si deve visualizzare solo se è in rec
				Toast toast = Toast.makeText(getApplicationContext(), 
						+R.string.sms_toast, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.TOP, 0, 105);
				toast.show();
//	STOP REGISTRAZIONE
				try {
				mr.stop();
				}catch (IOException e) {
					e.printStackTrace();
				}
				verificareg = false;
				}
				MetaDataXML mdxml = new MetaDataXML();
				mdxml.creaMetaDataXML(String.format("%03d", i)+path);
//	FINE STOP REGISTRAZIONE
			} 
		});
//	FINE STOP: CODICE
//	GESTIONE CLICK DELLA LISTA ACTIVITY (OPZIONI, LISTA REG)
		lv.setClickable(true);
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				// TODO Auto-generated method stub
				if(position == 0)
				{
					Intent intent = new Intent(RegToothActivity.this,OpzioniActivity.class);
					startActivity(intent);
				}else{
					Intent intent = new Intent(RegToothActivity.this,ListaRegActivity.class);
					intent.putExtra("formato", prefs.getString("formato", "ERROR"));
					startActivity(intent);
				}
			}
		});
    }
}
