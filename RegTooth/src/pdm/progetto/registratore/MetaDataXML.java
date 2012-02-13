package pdm.progetto.registratore;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.view.View;
import android.widget.LinearLayout;

public class MetaDataXML
{
	private String nomefile;
	private String cartella;
	private Calendar today = Calendar.getInstance(Locale.getDefault());
	File filexml = new File("mnt/sdcard/RegTooth/"+cartella+this.nomefile);
	private Document doc;
	
	public void creaMetaDataXML(String nomefile)
	{
		cartella = nomefile;
		this.nomefile = sanitizePath(nomefile);
		
		try {
			 
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
	 
			// root elements
			Document doc = docBuilder.newDocument();
			Element registrazione = doc.createElement("registrazione");
			doc.appendChild(registrazione);
/*	 
			// staff elements
			Element staff = doc.createElement("Staff");
			rootElement.appendChild(staff);
	 
			// set attribute to staff element
			Attr attr = doc.createAttribute("id");
			attr.setValue("1");
			staff.setAttributeNode(attr);
	 
			// shorten way
			// staff.setAttribute("id", "1");
*/
			// Elementi della registrazione (ROOT ELEMENT)
			Element corso = doc.createElement("corso");
			corso.appendChild(doc.createTextNode("Nessun corso inserito"));
			registrazione.appendChild(corso);
			
			Element professore = doc.createElement("professore");
			professore.appendChild(doc.createTextNode("Nessun professore inserito"));
			registrazione.appendChild(professore);
			
			Element numero = doc.createElement("numero");
			numero.appendChild(doc.createTextNode("Lezione numero"));
			registrazione.appendChild(numero);
			
			Element data = doc.createElement("data");
			data.appendChild(doc.createTextNode(""+today.get(Calendar.YEAR)+"-"+(today.get(Calendar.MONTH)+1)+
				       								"-"+today.get(Calendar.DAY_OF_MONTH)));
			registrazione.appendChild(data);
			
			Element aula = doc.createElement("aula");
			aula.appendChild(doc.createTextNode("Nessuna aula inserita"));
			registrazione.appendChild(aula);
			
			Element note = doc.createElement("note");
			note.appendChild(doc.createTextNode("Inserisci delle note personali"));
			registrazione.appendChild(note);
	 
			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			
			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);
			StreamResult result = new StreamResult(filexml);
			transformer.transform(source, result);
			creaFile(filexml);
		  } catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		  } catch (TransformerException tfe) {
			tfe.printStackTrace();
		  }
	}
	
	public void creaFile(File file_xml)
	{
		file_xml.renameTo(new File("mnt/sdcard/RegTooth/"+cartella+this.nomefile));
		try {
			file_xml.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		filexml.delete();
	}
	public void salvaFile(File xml_salva)
	{
		try {
			xml_salva.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private String sanitizePath(String path) {
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		if (!path.contains(".")) {
			path += ".xml";
		}
    return path;
	}
    public void changeLayoutVisibility(LinearLayout ll_infonote, LinearLayout ll_et_inv)
    {	    	
    	if(ll_infonote.getVisibility() == View.VISIBLE)
    	{
	    	ll_infonote.setVisibility(View.GONE);
	    	ll_et_inv.setVisibility(View.VISIBLE);
    	}else {
	    	ll_et_inv.setVisibility(View.GONE);
	    	ll_infonote.setVisibility(View.VISIBLE);

    	}
    }
    public void setDoc(Document doc)
    {
    	this.doc = doc;
    }
    public Document getDoc()
    {
    	return doc;
    }
}
