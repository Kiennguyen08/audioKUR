package tsos.iu.equalizer.player;

//Описание класса исходного Аудио-файла (его параметров)
public class AudioFileFormat {
	private final boolean bigEndian;
	private final boolean signed;
	int bits;
	int channels;
	double sampleRate;
	
	public AudioFileFormat() { //описание формата
		this.bigEndian = false;// indicates whether the data for a single sample is stored in big-endian byte order (false means little-endian)
		this.signed = true;//indicates whether the data is signed or unsigned
		this.bits = 16;
		this.channels  = 2; //The number of audio channels in this format (1 for mono, 2 for stereo).
		this.sampleRate= 44100.0; //The number of samples played or recorded per second, for sounds that have this format.
	}
	
	public boolean isBigEndian() {
		return this.bigEndian;
	}
	
	public boolean isSigned() {
		return this.signed;
	}
	
	public int getBits() {
		return this.bits;
	}
	
	public int getChannels() {
		return this.channels;
	}
	
	public double getSampleRate() {
		return this.sampleRate;
	}
			
	
}

