package pl.net.hola.theonionphone;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import pl.net.hola.theonionphone.audio.AudioManager;
import pl.net.hola.theonionphone.audio.AudioManagerImpl;
import pl.net.hola.theonionphone.utils.locator.ServiceLocator;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initializeGlobalApplicationContext();
        
        
        (new Thread() {
        	public void run() {
        		final ServiceLocator locator = ServiceLocator.getInstance();
        		
//        		try {
//					Thread.sleep(10000);
//				} catch (InterruptedException e) { }
        
		        final AudioManager audioManager = locator.getAudioManager();
        		
//        		final AudioManager audioManager = new AudioManagerImpl();
		        
        		FileOutputStream fos;
        		File file = new File("/sdcard/sth.bytes");
        		try {
					file.createNewFile();
					fos = new FileOutputStream(file);
				} catch (IOException e1) {
					throw new RuntimeException(e1);
				}
        		
        		
		        InputStream is = audioManager.getStream();
		        audioManager.startSending();
		        
		        try {
		        	Thread.sleep(5000);
		        } catch (InterruptedException e) { }
		        
		        audioManager.stopSending();

		        try {
			        byte[] buf = new byte[7];
			        while(is.read(buf) != -1) {
			        	fos.write(buf);
			        }
		        
			        is.close();
		        	fos.close();
		        } catch (IOException e) {
		        	return;
		        }
		        
		        FileInputStream fis;
		        try {
		        	fis = new FileInputStream(file);
		        } catch (IOException e) {
		        	return;
		        }
		        audioManager.playStream(fis);
		        
		        try {
		        	Thread.sleep(5000);
		        } catch (InterruptedException e) { }
		        
		        
		        audioManager.stopPlaying();
		        
		        file.delete();
		        try {
		        	fis.close();
		        } catch (IOException e) {
		        	return;
		        }
        	}
    	}).start();
        
        this.getApplication();
    }


    private void initializeGlobalApplicationContext() {
		TheOnionPhone.initializeContext(this);
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
