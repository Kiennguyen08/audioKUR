package tsos.iu.equalizer.player;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

// Описавание формирование выходного потока для воспроизведения
public class AudioOutputStream {
    private final ArrayList<Byte> inputStream;
	private AudioInputStream outputAudioStream;
	
	public AudioOutputStream(ArrayList<Byte> inputStream) {
		this.inputStream = inputStream;
		this.createAudioStream(this.inputStream);
	}
	
	private AudioInputStream createAudioStream(ArrayList<Byte> inputStream) {
		AudioFileFormat aff = new AudioFileFormat();//объект, представляющий воспроизводимый файл, и получить сведения о формате файла и записи звука в нём
                
                    AudioFormat format = new AudioFormat((float)aff.getSampleRate(), 
									aff.getBits(), aff.getChannels(), 
									aff.isSigned(), aff.isBigEndian());
		
		final byte[] byteBuffer = new byte[inputStream.size()];// Используется для хранения считанной информации и в качестве источника для записи
		// заполнение данных
		for(int i = 0; i < inputStream.size(); i++) {
			byteBuffer[i] = inputStream.get(i);
		}
		
		ByteArrayInputStream bais = new ByteArrayInputStream(byteBuffer);//представляет входной поток, использующий в качестве источника данных массив байтов
		
		this.outputAudioStream = new AudioInputStream(bais, format, byteBuffer.length / 2);
		
		return this.outputAudioStream;
	}
	
	public AudioInputStream getAudioStream() {
		return this.outputAudioStream;
	}
}
