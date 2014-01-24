package org.theonionphone.audio;

import static org.mockito.BDDMockito.*;

import java.io.InputStream;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.theonionphone.audio.AudioInput;
import org.theonionphone.audio.AudioInputWorker;
import org.theonionphone.audio.codecs.Codec;
import org.theonionphone.common.exceptions.AudioManagerException;

import android.util.Log;
import junit.framework.TestCase;


public class AudioInputTest extends TestCase {

	@Mock
	private Codec codec;
	@Mock
	private AudioInputWorker worker;
	
	private AudioInput instance;
	
	@Override
	protected void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		instance = new AudioInput(codec);
	}
	
	public void test_getStream_shouldInitializeWorkerAndReturnInputStream() throws Exception {
		//when
		InputStream result = instance.getStream();
		
		//then
		assertNotNull("input stream should not be null", result);
		assertNotNull("worker should be initialized", instance.getWorker());
	}
	
	public void test_getStream_shouldThrowException_whenPreviousStreamWasNotReleased() throws Exception {
		//given
		instance.getStream();
		Exception ex = null;
		
		//when
		try {
			instance.getStream();
		} catch(AudioManagerException e) {
			ex = e;
		} finally {
			assertNotNull(ex);
		}
	}
	
	public void test_startSending_shouldStartWorker() throws Exception {
		//given
		instance.setWorker(worker);
		
		//when
		instance.startSending();
		
		//then
		verify(worker).start();
	}
	
	public void test_stopSending_shouldStopWorkerAndNullTheReference() throws Exception {
		//given
		instance.setWorker(worker);
		
		//when
		instance.stopSending();
		
		//then
		verify(worker).stopAndClose();
		assertNull("reference to worker should be cleared", instance.getWorker());
	}
	
}
