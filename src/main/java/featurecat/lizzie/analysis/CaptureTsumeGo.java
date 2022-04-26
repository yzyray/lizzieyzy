package featurecat.lizzie.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.jdesktop.swingx.util.OS;

import featurecat.lizzie.Lizzie;
import featurecat.lizzie.util.Utils;

public class CaptureTsumeGo {
	private Process process;
	  private BufferedReader inputStream;
	  //private BufferedOutputStream outputStream;
	  private BufferedReader errorStream;
	  private ScheduledExecutorService executor;
	  private ScheduledExecutorService executorErr;
	  
	  public CaptureTsumeGo() {
		  if(start())
		  {
			  initializeStreams();
			   executor = Executors.newSingleThreadScheduledExecutor();
			    executor.execute(this::read);
			    executorErr = Executors.newSingleThreadScheduledExecutor();
			    executorErr.execute(this::readError);
		  }		  
	  }
	
	 private boolean start() {
		    String jarName = "CaptureTsumeGo.jar";
		    File jarFile = new File("captureTsumeGo" + File.separator + jarName);
		    if (!jarFile.exists()) Utils.copyCaptureTsumeGo();
		    boolean success = false;
		    try {		    	
		      if (OS.isWindows()) {		       
		        String java64Path = "jre\\java11\\bin\\java.exe";
		        File java64 = new File(java64Path);

		        if (java64.exists()) {
		          try {
		            process =
		                Runtime.getRuntime()
		                    .exec(java64Path + " -jar captureTsumeGo" + File.separator + jarName);
		            success = true;
		          } catch (Exception e) {
		            success = false;
		            e.printStackTrace();
		          }
		        }
		        if (!success) {
		          String java32Path = "jre\\java8_32\\bin\\java.exe";
		          File java32 = new File(java32Path);
		          if (java32.exists()) {
		            try {
		            	process =
		                  Runtime.getRuntime()
		                      .exec(java32 + " -jar captureTsumeGo" + File.separator + jarName);
		              success = true;
		            } catch (Exception e) {
		              success = false;
		              e.printStackTrace();
		            }
		          }
		        }
		        if (!success) {
		        	process =
		              Runtime.getRuntime()
		                  .exec("java -jar captureTsumeGo" + File.separator + jarName);
		        	success=true;
		        }
		      } else {
		    	  process =
		            Runtime.getRuntime().exec("java -jar captureTsumeGo" + File.separator + jarName);
		    	  success=true;
		      }
		    } catch (Exception e) {
		    	success=false;
		      Utils.showMsg(e.getLocalizedMessage());
		    }
			return success;
		  }
	 
	  private void initializeStreams() {
		    inputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
		   // outputStream = new BufferedOutputStream(process.getOutputStream());
		    errorStream = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		  }
	  
	  private void read() {
		    try {
		      String line = "";
		      // while ((c = inputStream.read()) != -1) {
		      while ((line = inputStream.readLine()) != null) {
		        try {
		          parseLine(line.toString());
		        } catch (Exception ex) {
		          ex.printStackTrace();
		        }
		      }
		      // this line will be reached when engine shuts down
		      System.out.println("Capture process ended.");
		      // Do no exit for switching weights
		      // System.exit(-1);
		    } catch (IOException e) {
		    }
		   
		    process = null;
		    shutdown();
		    return;
		  }
	  
	  private void readError() {
		    String line = "";
		    try {
		      while ((line = errorStream.readLine()) != null) {
		        try {
		        	  Lizzie.gtpConsole.addErrorLine(line + "\n");
		        } catch (Exception e) {
		          e.printStackTrace();
		        }
		      }
		    } catch (IOException e) {
		      // TODO Auto-generated catch block
		      e.printStackTrace();
		    }
		  }
	  
	  private void parseLine(String line) {
		   Lizzie.gtpConsole.addLine(line+ "\n");
		 }
	  
	  public void shutdown() {
		    process.destroy();
		  }
}
