package pdm.progetto.registratore;

import java.io.File;

public class SDGestione {
	File root=new File("mnt/sdcard/RegTooth");
	String[] prova;
	
	public void deleteDirectory(int i)
	{
		File[] afile=root.listFiles();
		DeleteRecursive(afile[i]);
	}
	public File[] getFile()
	{
		return root.listFiles();
	}
	public String[] getListaFile()
	{
		File[] listOfFiles = root.listFiles();
		prova = new String[listOfFiles.length];
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
              prova[i] = listOfFiles[i].getName();
            } else if (listOfFiles[i].isDirectory()) {
              prova[i] = listOfFiles[i].getName();
            }
        }
        return prova;
	}
	public void deleteAllDirectory()
	{
		try{
		for (File child : root.listFiles()) 
			DeleteRecursive(child);
		}catch (Exception e) {		
		}
	}
	
	private void DeleteRecursive(File fileOrDirectory) {
	    if (fileOrDirectory.isDirectory())
	        for (File child : fileOrDirectory.listFiles())
	            DeleteRecursive(child);

	    fileOrDirectory.delete();
	    //elimino dir o file
	}
	  public void delete(String rec)
	  {
		  DeleteRecursive(new File("mnt/sdcard/RegTooth/"+rec));
	  }

}
