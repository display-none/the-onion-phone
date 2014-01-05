package org.theonionphone.utils.locator;

import org.theonionphone.TheOnionPhone;
import org.theonionphone.audio.AudioManager;
import org.theonionphone.audio.AudioManagerImpl;
import org.theonionphone.network.AnonimityNetwork;
import org.theonionphone.network.TorNetwork;
import org.theonionphone.protocol.ProtocolManagement;
import org.theonionphone.protocol.ProtocolManagementImpl;

import android.content.Context;

public class ServiceLocator {
	
	private static ServiceLocator instance;

	private ServiceHolder<AudioManagerImpl> audioMangerHolder;
	private ServiceHolder<TorNetwork> anonimityNetworkHolder;
	private ServiceHolder<ProtocolManagementImpl> protocolManagementHolder;
	
	private Context context;
	
	private ServiceLocator() {
		context = TheOnionPhone.getContext();
	}
	
	public synchronized AudioManager getAudioManager() {
		if(audioMangerHolder == null) {
			audioMangerHolder = new ServiceHolder<AudioManagerImpl>(context, AudioManagerImpl.class);
		}
		
		return audioMangerHolder.getService();
	}
	
	public synchronized AnonimityNetwork getAnonimityNetwork() {
		if(anonimityNetworkHolder == null) {
			anonimityNetworkHolder = new ServiceHolder<TorNetwork>(context, TorNetwork.class);
		}
		
		return anonimityNetworkHolder.getService();
	}
	
	public synchronized ProtocolManagement getProtocolManagement() {
		if(protocolManagementHolder == null) {
			protocolManagementHolder = new ServiceHolder<ProtocolManagementImpl>(context, ProtocolManagementImpl.class);
		}
		
		return protocolManagementHolder.getService();
	}
	
	public static synchronized ServiceLocator getInstance() {
		if(instance == null) {
			instance = new ServiceLocator();
		}
		return instance;
	}
}
