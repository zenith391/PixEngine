package org.lggl.audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.lggl.utils.debug.DebugLogger;

public class AISSound extends Sound {

	private AudioInputStream ais;
	
	public AISSound(File file) {
		try {
			ais = AudioSystem.getAudioInputStream(file);
		} catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}
		try {
			samples = ais.readAllBytes();
			System.out.println(ais.getFrameLength());
			for (int i = 0; i < 90000; i++) {
				byte[] sb = new byte[ais.getFormat().getFrameSize()];
				ais.read(sb);
				for (int i0 = 0; i0 < sb.length; i0++) {
					samples[i0 + (i * ais.getFormat().getFrameSize())] = sb[i0];
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		DebugLogger.logError("Never use AISSound in production! It is only used for debugging purposes");
	}

}
