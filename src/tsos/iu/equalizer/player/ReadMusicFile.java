package tsos.iu.equalizer.player;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class ReadMusicFile{
	private AudioInputStream ais;
	private byte[] outputSignal;
	private SourceDataLine sdl; //The SourceDataLine interface provides a method for writing audio data to the data line's buffer.
	public ReadMusicFile(File filePath) throws UnsupportedAudioFileException, IOException, InterruptedException, LineUnavailableException {
			
		if(filePath != null) { ////Получаем AudioInputStream
			this.ais = AudioSystem.getAudioInputStream(filePath);// получаем поток с аудио-данными
			AudioFormat format = ais.getFormat(); // получаем информацию о формате
			this.sdl = AudioSystem.getSourceDataLine(format); // количество кадров в файле
			this.sdl.flush();//flush() метод отбрасывает любые доступные данные с очередями от внутреннего буфера
		}
		
	}
	
	public byte[] getOutputSignal() {
		return this.outputSignal;
	}
	
	public AudioInputStream getAudioInputStream() {
		return this.ais;
	}
	
	public void closeAudioInputStream() {
		try {
			this.ais.close();
		} catch (IOException e) {
		}
	}
	
	public SourceDataLine getSourceDataLine() {
		return this.sdl;
	}
}

