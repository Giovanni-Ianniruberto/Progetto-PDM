package pdm.progetto.registratore;

import java.io.File;
import java.io.IOException;

import android.media.MediaRecorder;
import android.util.Log;

public class AudioRecorder {
	private static final String LOG_TAG = "REC PREPARE";
	private MediaRecorder recorder = new MediaRecorder();
	final String path;
	private int formato=1;
	private String nome;
	private String direc;
	private File rec;
	public AudioRecorder(String path, String formato) {
		this.path = sanitizePath(path,formato);
	}

	private String sanitizePath(String path,String formato) {
		nome=path;
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		if (!path.contains(".")) {
			path += "."+formato;
		}
    
    return path;
	}
	  public String getName(){
		  return nome;
	  }

	  public void delete()
	  {
		  DeleteRecursive(rec);
	  }
	  private void DeleteRecursive(File fileOrDirectory) {
		    if (fileOrDirectory.isDirectory())
		        for (File child : fileOrDirectory.listFiles())
		            DeleteRecursive(child);

		    fileOrDirectory.delete();
		    //elimino dir o file
		}
	  public String getDirRec()
	  {
		  return direc;
	  }

	public void start(String formato, String qualita) throws IOException {
		int qual = 1;
		if(!formato.equals("3gp"))
			this.formato = 2;
		if (!qualita.startsWith("m")) qual = 2;
		String state = android.os.Environment.getExternalStorageState();
		if(!state.equals(android.os.Environment.MEDIA_MOUNTED))  {
			throw new IOException("SD Card is not mounted.  It is " + state + ".");
		}
	    File  regtooth=new File("mnt/sdcard/RegTooth");
	    regtooth.mkdir();
	    File dir=new File("mnt/sdcard/RegTooth/"+this.nome);
	    dir.mkdir();
	    regtooth.getParentFile();

		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(this.formato);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setAudioChannels(qual); 				//1 mono - 2 stereo
		recorder.setAudioEncodingBitRate(8 * qual);   	//BIT
		if(qualita.charAt(13) == '4'){					//BIT RATE
		recorder.setAudioSamplingRate(44100);
		}else recorder.setAudioSamplingRate(96000);
	    rec=new File("mnt/sdcard/RegTooth/"+this.nome);
	    direc="mnt/sdcard/RegTooth/"+this.nome;
	    recorder.setOutputFile("mnt/sdcard/RegTooth/"+this.nome+path);
		try {
			recorder.prepare();
		} catch (IOException e) {
			Log.e(LOG_TAG, "prepare() FALLITO");
		}
		recorder.start();
	}

	public void stop() throws IOException {
		recorder.stop();
		recorder.release();
		recorder=null;
	}
  /*
  public void onPause() {
  	super.onPause();
  	if (recorder!= null) {
          recorder.release();
          recorder= null;
     }
  	}*/
}