package org.theonionphone;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Set;

import org.theonionphone.R;
import org.theonionphone.audio.AudioManager;
import org.theonionphone.audio.AudioManagerImpl;
import org.theonionphone.common.CallInfo;
import org.theonionphone.common.exceptions.MainProtocolException;
import org.theonionphone.identity.Identity;
import org.theonionphone.network.AnonimityNetwork;
import org.theonionphone.protocol.ProtocolManagement;
import org.theonionphone.ui.UserInterface;
import org.theonionphone.utils.locator.ServiceLocator;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {
	

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initializeGlobalApplicationContext();
        
        
//        (new Thread() {
//        	public void run() {
//        		final ServiceLocator locator = ServiceLocator.getInstance();
//        		
////        		try {
////					Thread.sleep(10000);
////				} catch (InterruptedException e) { }
//        
//		        final AudioManager audioManager = locator.getAudioManager();
//        		
////        		final AudioManager audioManager = new AudioManagerImpl();
//		        
//        		FileOutputStream fos;
//        		File file = new File("/sdcard/sth.bytes");
//        		try {
//					file.createNewFile();
//					fos = new FileOutputStream(file);
//				} catch (IOException e1) {
//					throw new RuntimeException(e1);
//				}
//        		
//        		
//		        InputStream is = audioManager.getStream();
//		        audioManager.startSending();
//		        
//		        try {
//		        	Thread.sleep(5000);
//		        } catch (InterruptedException e) { }
//		        
//		        audioManager.stopSending();
//
//		        try {
//			        byte[] buf = new byte[7];
//			        while(is.read(buf) != -1) {
//			        	fos.write(buf);
//			        }
//		        
//			        is.close();
//		        	fos.close();
//		        } catch (IOException e) {
//		        	return;
//		        }
//		        
//		        FileInputStream fis;
//		        try {
//		        	fis = new FileInputStream(file);
//		        } catch (IOException e) {
//		        	return;
//		        }
//		        audioManager.playStream(fis);
//		        
//		        try {
//		        	Thread.sleep(5000);
//		        } catch (InterruptedException e) { }
//		        
//		        
//		        audioManager.stopPlaying();
//		        
//		        file.delete();
//		        try {
//		        	fis.close();
//		        } catch (IOException e) {
//		        	return;
//		        }
//        	}
//    	}).start();
        
        (new Thread() {
        	public void run() {
        		
        		try {
					Thread.sleep(7000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		
        		ServiceLocator.getInstance().cleanUp();
        		
//        		Provider[] providers = Security.getProviders();
//        		for (Provider provider : providers) {
//        		    Log.i("CRYPTO","provider: "+provider.getName());
//        		    Set<Provider.Service> services = provider.getServices();
//        		    for (Provider.Service service : services) {
//        		        Log.i("CRYPTO","  algorithm: "+service.getAlgorithm());
//        		        if(service.getAlgorithm().equals("ECDH")) {
//        		        	Log.i("dupa", "finally");
//        		        	
//        		        }
//        		    }
//        		}
        		
        		
        		AnonimityNetwork anonimityNetwork = ServiceLocator.getInstance().getAnonimityNetwork();
                anonimityNetwork.startConnectionListener();
                
//                ProtocolManagement protocolManagement = ServiceLocator.getInstance().getProtocolManagement();
//                Identity identity = new Identity();
//                identity.setNetworkIdentifier("192.168.2.113");
//                CallInfo callInfo = new CallInfo();
//                callInfo.setIdentity(identity);
////                callInfo.setShouldIntroduce(true);
////                callInfo.setRequiresIntroduction(true);
//                protocolManagement.startCall(callInfo);
                
                
        	};
        }).start();
         
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
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
	
	public void acceptCall(View view) {
		(new Thread() {
			public void run() {
//				UserInterface userInterface = ServiceLocator.getInstance().getUserInterface();
//				userInterface.acceptCall();
				AudioManager audioManager = ServiceLocator.getInstance().getAudioManager();
				audioManager.next();
				
			};
		}).start();
	}
    
}
