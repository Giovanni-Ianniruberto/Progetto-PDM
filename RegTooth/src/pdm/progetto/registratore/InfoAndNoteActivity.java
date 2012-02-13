package pdm.progetto.registratore;
/* PS: questo codice è ancora un bel po' incasinato, vedremo di migliorarlo un pochettino in futuro
 Qui oltre alla listview, c'è anche da aggiornare lo stato dei bottoni (verso la fine di questo codice)
 (leggere sempre i commenti a sinistra per facilitare la ricerca del codice)
 
 				arrayAdapter.notifyDataSetChanged(); (l'ho lasciato nel codice)
 				lv.invalidateViews();
				lv.postInvalidate();
  */
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class InfoAndNoteActivity extends Activity implements Runnable,OnClickListener
{
	private MediaPlayer musica;
	private ProgressBar progressBar;
	private Button start, pause, stop;
	private TextView status;
	private Uri uri;
	private int currentPosition;
	private NodeList childreg;
	private Element eElement;
	private MetaDataXML gestione_xml = new MetaDataXML();
	private String nome;
	private String[] arrayData;
	private ArrayAdapter<String> arrayAdapter;
	private Thread thread;
	private boolean running = false;
	private boolean check_btn_start = true;
	private boolean check_btn_pause = true;
	private boolean check_btn_stop = true;
	private boolean pausa = false;
	private boolean usato_mediaplayer = false;
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_and_note);
		nome=getIntent().getExtras().getString("DIR AUDIO");
		String formato=getIntent().getExtras().getString("formato");
		File reg=new File(nome+"."+formato);
		uri=Uri.fromFile(reg);
		progressBar=(ProgressBar)findViewById(R.id.progressBar1);
		status = (TextView) findViewById(R.id.testo);
		start=(Button) findViewById(R.id.play);
		pause=(Button) findViewById(R.id.pause);
		stop=(Button) findViewById(R.id.stop);
		start.setOnClickListener(this);
        pause.setOnClickListener(this);
        stop.setOnClickListener(this);
        pause.setEnabled(!check_btn_pause);
        stop.setEnabled(!check_btn_stop);
//GESTIONE METADATA: INIZIO
        ListView lv = (ListView) findViewById(R.id.listViewXML);
        final File fxml = new File(nome+".xml");
        try
        {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fxml);
		gestione_xml.setDoc(doc);
		doc.getDocumentElement().normalize();
	
		//PS: Nel nostro caso abbiamo un solo nodo per ogni file
		NodeList nodi_reg = doc.getElementsByTagName("registrazione");//prende la lista di tutti i NODI registrazione
		Node registrazione = nodi_reg.item(0);	//prende dalla lista il NODO registrazione n° 0
		childreg = registrazione.getChildNodes();//questa è la lista dei NODI CHILD dentro registrazione
		arrayData = new String[childreg.getLength()];
		for (int i = 0 ; i < childreg.getLength(); i++)
		{
			Node app = childreg.item(i);//prendi l'i-esimo NODO CHILD 
		    eElement = (Element) registrazione;
			arrayData[i] = getTagValue(app.getNodeName(), eElement);
		}
		arrayAdapter = new ArrayAdapter<String>(
				this, R.layout.metadatalista, R.id.xml_valore, arrayData) 
				{
					public View getView(int position, View convertView, ViewGroup parent) 
					{
						View adapterView = super.getView(position, convertView, parent);
						TextView tv_child = (TextView)adapterView.findViewById(R.id.xml_titolo);
						Node app = childreg.item(position);
							if("corso".equals(app.getNodeName()))
								tv_child.setText("Corso");//DA SETTARE DALLE RISORSE
							else if("professore".equals(app.getNodeName()))
								tv_child.setText("Professore");
							else if("numero".equals(app.getNodeName()))
								tv_child.setText("Lezione numero");
							else if("data".equals(app.getNodeName()))
								tv_child.setText("Data");
							else if("aula".equals(app.getNodeName()))
								tv_child.setText("Aula");
							else
								tv_child.setText("Note");
						return adapterView;
					}
				};
				lv.setAdapter(arrayAdapter);
        }catch (Exception e) {
    		e.printStackTrace();
  	  	}
//MODIFICA METADATA: INIZIO
		lv.setClickable(true);
		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				// TODO Auto-generated method stub
				final Node node = childreg.item(position);
		    	final LinearLayout ll_infonote = (LinearLayout)findViewById(R.id.linearlayout_infonote);
		    	final LinearLayout ll_et_inv = (LinearLayout)findViewById(R.id.linearlayout_et_invisibile);
				gestione_xml.changeLayoutVisibility(ll_infonote, ll_et_inv);
				setTextInET(getTagValue(node.getNodeName(), eElement));
				Button btn_annulla = (Button) findViewById(R.id.btn_indietro);
				Button btn_cancellatutto = (Button) findViewById(R.id.btn_cancellatesto);
				Button btn_salva = (Button) findViewById(R.id.btn_salva);
				btn_annulla.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						// TODO Auto-generated method stub
						gestione_xml.changeLayoutVisibility(ll_infonote, ll_et_inv);
					}
				});
				btn_cancellatutto.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						// TODO Auto-generated method stub
						EditText et = (EditText) findViewById(R.id.et_note);
						et.setText("");
					}
				});
				btn_salva.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						// TODO Auto-generated method stub
					try
					{
						EditText et = (EditText) findViewById(R.id.et_note);
						node.setTextContent(et.getText().toString());
						TransformerFactory transformerFactory = TransformerFactory.newInstance();
						Transformer transformer = transformerFactory.newTransformer();
						DOMSource source = new DOMSource(gestione_xml.getDoc());
						File salvataggio = new File(nome+".xml");
						StreamResult result = new StreamResult(salvataggio);
						transformer.transform(source, result);
						gestione_xml.salvaFile(salvataggio);
					  } catch (TransformerException tfe) {
							tfe.printStackTrace();
					  }
//AGGIORNARE LISTVIEW
					arrayAdapter.notifyDataSetChanged();
					}
				});

			}
			
		});
//MODIFICA METADATA: FINE
//GESTIONE METADATA: FINE
	}
	//onClick dei BOTTONI START PAUSA STOP
	public void onClick(View v) {
		//BOTTONE START
        if (v.equals(start)) {
        	usato_mediaplayer = true;
    		if(pausa)
    		{
    			pausa = false;
    			musica.start();
                start.setEnabled(!check_btn_start);
                pause.setEnabled(check_btn_pause);
                stop.setEnabled(check_btn_stop);
    		}else{
            if (musica!= null && musica.isPlaying()) return;
            musica=MediaPlayer.create(this, uri);
            musica.start();               
            status.setText("PLAY");         
            progressBar.setVisibility(ProgressBar.VISIBLE);
            progressBar.setProgress(0);
            progressBar.setMax(musica.getDuration());
            start.setEnabled(!check_btn_start);
            pause.setEnabled(check_btn_pause);
            stop.setEnabled(check_btn_stop);
//START THREAD
    		if(!running){
    			running = true;
    			thread = new Thread(this,"MyRunnableThread");
    			thread.start();
    		}
    		}
        }
        //BOTTONE PAUSA
        if (v.equals(pause) && musica!=null) {
        	musica.pause();            
            status.setText("PAUSA");
            progressBar.setVisibility(ProgressBar.VISIBLE);
        	start.setEnabled(check_btn_start);
        	pause.setEnabled(!check_btn_pause);
        	stop.setEnabled(check_btn_stop);
        	pausa = false;
        }
        //BOTTONE STOP
        if (v.equals(stop) && musica!=null) {
        	musica.stop();            
            status.setText(R.string.btn_stop);
            progressBar.setVisibility(ProgressBar.VISIBLE);
            progressBar.setProgress(0);
    		if(running){
    			running = false;
    			thread.interrupt();
    		}
        	start.setEnabled(check_btn_start);
        	pause.setEnabled(!check_btn_pause);
        	stop.setEnabled(!check_btn_stop);
        }
    }

	//DURANTE L'ESECUZIONE
    public void run() {
        currentPosition= 0;
        int total = musica.getDuration();
        
        while (musica!=null && currentPosition<total) 
        {
            try {
                Thread.sleep(10);
                currentPosition= musica.getCurrentPosition();
                
            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
                return;
            }            
            progressBar.setProgress(currentPosition);
        }

//BISOGNA CHE SI AGGIORNI LA GRAFICA PER POTER VEDERE QUESTE MODIFICHE
/*a meno che non è sbagliata la condizione: quando finisce il while currentPosizion dovrebbe già essere maggiore di total
 * Per sicurezza ho aggiunto 1000 (1secondo) ma i bottono non cambiano stato*/
        if(currentPosition +1000 > total)
        {
        	start.setEnabled(check_btn_start);
        	pause.setEnabled(!check_btn_pause);
        	stop.setEnabled(!check_btn_stop);
        }

    }

	    protected void onDestroy(){	
	    	super.onDestroy();
	    	if(usato_mediaplayer)
	    	{
	    	musica.release();
            thread.stop();
	    	}
	    }
//GESTIONE METADATA PARTE 2
	    private static String getTagValue(String sTag, Element eElement) {
	    	NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
	     
	            Node nValue = (Node) nlList.item(0);
	     
	    	return nValue.getNodeValue();
	    }

	    private void setTextInET(String testo)
	    {
	    	EditText et = (EditText) findViewById(R.id.et_note);
	    	et.setText(testo);
	    }
}