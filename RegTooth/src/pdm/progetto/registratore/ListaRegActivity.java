package pdm.progetto.registratore;
/*Ho lasciato un commento a sinistra quando deve essere aggiornarta la lista
 Il copia-incolla è sempre comodo :D (sempre se servono questi metodi...)
				
				adapter.notifyDataSetChanged();
				lv.invalidateViews();
				lv.postInvalidate();
				
*/
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class ListaRegActivity extends Activity
{
    private ListView lv;
    private ArrayAdapter<String> adapter;
    private String[] prova;
    private SDGestione sdg = new SDGestione();
    private String formato;
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listareg);
        formato=getIntent().getExtras().getString("formato");
        lv = (ListView) findViewById(R.id.listView1);
        prova = sdg.getListaFile();//prende tutti i file e cartelle all'interno della cartella nel quale vengono salvati i file reg.
        final Button btnelimina = (Button) findViewById(R.id.elimina);
        final Button condividi = (Button) findViewById(R.id.condividi);
        btnelimina.setEnabled(false);
        condividi.setEnabled(false);
        adapter = new ArrayAdapter<String>(ListaRegActivity.this, android.R.layout.simple_list_item_checked,prova){

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view =super.getView(position, convertView, parent); 
				return view;
			}
        };
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setAdapter(adapter);
        //Attiva i bottoni per le operazioni di condivisione ed eliminazione (singolo file)
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				btnelimina.setEnabled(true);
		        condividi.setEnabled(true);
			}
		});
        //BOTTONE ELIMINA TUTTI I FILE
        Button btneliminatutto = (Button) findViewById(R.id.eliminatutto);
        btneliminatutto.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				sdg.deleteAllDirectory();
				btnelimina.setEnabled(false);
				condividi.setEnabled(false);
//QUI AGGIORNARE LISTA
			}
		});
        //BOTTONE ELIMINA IL FILE SELEZIONATO
        btnelimina.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				sdg.delete(prova[lv.getCheckedItemPosition()]);
				btnelimina.setEnabled(false);
				condividi.setEnabled(false);
//QUI AGGIORNARE LISTA
			}
		});
        //CODICE PER ANDARE ALL'ACTIVITY DELLA RIPRODUZIONE AUDIO E METADATA ASSOCIATO AL FILE SELEZIONATO
        lv.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener (){
        		   
        		  public boolean onItemLongClick(AdapterView<?> av, View v, int pos, long id) { 
        			String nome=prova[pos];
      				Intent intent=new Intent(ListaRegActivity.this,InfoAndNoteActivity.class);
      				intent.putExtra("DIR AUDIO", "mnt/sdcard/RegTooth/"+nome+"/"+nome);
      				intent.putExtra("formato", formato);
      				startActivity(intent);
        		  return false; 
        } 
       }); 
    }
}