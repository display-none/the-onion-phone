package org.theonionphone.utils.locator;

import org.theonionphone.TheOnionPhone;
import org.theonionphone.audio.AudioManager;
import org.theonionphone.audio.AudioManagerImpl;
import org.theonionphone.audio.DummyAudioManager;
import org.theonionphone.identity.IdentityManager;
import org.theonionphone.identity.IdentityManagerImpl;
import org.theonionphone.network.AnonimityNetwork;
import org.theonionphone.network.TorNetwork;
import org.theonionphone.protocol.ProtocolManagement;
import org.theonionphone.protocol.ProtocolManagementImpl;
import org.theonionphone.ui.UserInterface;
import org.theonionphone.ui.UserInterfaceImpl;

import android.content.Context;

/**
 * Utility for service location.
 * 
 * As a singleton will lazily create services (more specifically ServiceHolders) 
 * and hold references to them until cleanUp() is called.
 *
 */
public class ServiceLocator {
	
	private static ServiceLocator instance;

	private ServiceHolder<AudioManagerImpl> audioMangerHolder;
//	private ServiceHolder<DummyAudioManager> audioMangerHolder;
	private ServiceHolder<TorNetwork> anonimityNetworkHolder;
	private ServiceHolder<IdentityManagerImpl> identityManagerHolder;
	private ServiceHolder<ProtocolManagementImpl> protocolManagementHolder;
	private ServiceHolder<UserInterfaceImpl> userInterfaceHolder;
	
	private Context context;

	private ServiceLocator() {
		context = TheOnionPhone.getContext();
	}
	
	public synchronized AnonimityNetwork getAnonimityNetwork() {
		if(anonimityNetworkHolder == null) {
			anonimityNetworkHolder = new ServiceHolder<TorNetwork>(context, TorNetwork.class);
		}
		
		return anonimityNetworkHolder.getService();
	}
	
	public synchronized AudioManager getAudioManager() {
		if(audioMangerHolder == null) {
			audioMangerHolder = new ServiceHolder<AudioManagerImpl>(context, AudioManagerImpl.class);
//			audioMangerHolder = new ServiceHolder<DummyAudioManager>(context, DummyAudioManager.class);
		}
		
		return audioMangerHolder.getService();
	}
	
	public synchronized IdentityManager getIdentityManager() {
		if(identityManagerHolder == null) {
			identityManagerHolder = new ServiceHolder<IdentityManagerImpl>(context, IdentityManagerImpl.class);
		}
		
		return identityManagerHolder.getService();
	}
	
	public synchronized ProtocolManagement getProtocolManagement() {
		if(protocolManagementHolder == null) {
			protocolManagementHolder = new ServiceHolder<ProtocolManagementImpl>(context, ProtocolManagementImpl.class);
		}
		
		return protocolManagementHolder.getService();
	}
	
	public synchronized UserInterface getUserInterface() {
		if(userInterfaceHolder == null) {
			userInterfaceHolder = new ServiceHolder<UserInterfaceImpl>(context, UserInterfaceImpl.class);
		}
		
		return userInterfaceHolder.getService();
	}
	
	public static synchronized ServiceLocator getInstance() {
		if(instance == null) {
			instance = new ServiceLocator();
		}
		return instance;
	}
	
	/**
	 * Unbinds all bound services.
	 */
	public void cleanUp() {
		cleanUpHolder(anonimityNetworkHolder);
		cleanUpHolder(audioMangerHolder);
		cleanUpHolder(identityManagerHolder);
		cleanUpHolder(protocolManagementHolder);
		cleanUpHolder(userInterfaceHolder);
	}

	private void cleanUpHolder(ServiceHolder<?> holder) {
		if(holder != null) {
			holder.cleanUp();
		}
	}
}
