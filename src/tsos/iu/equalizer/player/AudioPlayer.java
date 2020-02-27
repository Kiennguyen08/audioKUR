package tsos.iu.equalizer.player;
//Имопрт библиотек
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import tsos.iu.equalizer.effects.Echo;
import tsos.iu.equalizer.effects.Clipping;
import tsos.iu.equalizer.equalizer.Equalizer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

//Описание класса аудио-плеера. 
//Считывается аудиопоток в буфер. В плеере отслеживаются действия и их результат.
//Устанавливаются эффекты.
//Производится вывод спектра на графиках.

public class AudioPlayer implements LineListener{//this (дословно переводится "этот") - это указатель на объект из которого он был вызван.
	private double volume;
	private final SourceDataLine sourceDataLine;
	private final AudioInputStream ais;
	private final byte[] buff;
        
        public boolean isCalculated = false;
        private final int BUFF_SIZE = 65536;
        
	private short[] sampleBuff;
        
        private final FFT fourierInput;
        public FFT fourierOutput;
        
	private final Echo echo;
	private boolean isEcho;
	
	private final Clipping clipping;
	private double clippingCoef;
	private boolean isClipping;
	
	private final Equalizer equalizer;
	private boolean pause;
	private final AudioFormat format;
        
       
	
	public AudioPlayer(File musicFile) throws UnsupportedAudioFileException, 
						IOException, InterruptedException, LineUnavailableException {
		ReadMusicFile readFile = new ReadMusicFile(musicFile);//объявление читаемого файла
		this.sourceDataLine =  readFile.getSourceDataLine();//его чтение
		this.ais = readFile.getAudioInputStream();//получение входного сигнала
                this.buff = new byte[this.BUFF_SIZE];//создание буфера
		this.sampleBuff = new short[BUFF_SIZE / 2];
		this.echo = new Echo();//создание реверберации
		this.clipping = new Clipping();//создание енвелопа
		this.isEcho = false;
		this.isClipping = false;
		this.clippingCoef = 1.0;
		this.equalizer = new Equalizer(BUFF_SIZE / 2);
		AudioFileFormat aff = new AudioFileFormat();//определение формата
		format = new AudioFormat((float)aff.getSampleRate(), 
				aff.getBits(), aff.getChannels(), 
				aff.isSigned(), aff.isBigEndian());
		this.volume = 1.0;
                this.fourierInput = new FFT();//БПФ входа
                this.fourierOutput = new FFT();//БПФ выхода
	}
        
       
	public void play() {
			try{
				this.sourceDataLine.open(this.format);// информация о формате
				this.sourceDataLine.start();// дает команду воспроизведения
				this.pause = false;
				while ((this.ais.read(this.buff, 0, this.BUFF_SIZE)) != -1) { //Когда весь файл будет воспроизводиться, и нет больше данных, чтобы быть воспроизведены 
                                                                                              //метод read() возвращает -1 (как это происходит с любыми другими ридерами).
                                    
					this.ByteArrayToSamplesArray();
                                        
					//отрисовка без изменения sampleBuff
                                        this.isCalculated = false;
                                        
                                        this.fourierInput.FFTAnalysis(this.sampleBuff, 512);
					if(this.pause) {this.stop();}
					
					if(this.isEcho)
						this.echo(this.sampleBuff);
					
					if(this.isClipping) {
						this.clipping(sampleBuff);
					}
					
					equalizer.setInputSignal(this.sampleBuff);
					this.equalizer.equalization();
					this.sampleBuff = equalizer.getOutputSignal();
                                        
                                        //отрисовка с изменением
                                        this.fourierOutput.FFTAnalysis(this.sampleBuff, 512);
                                        this.isCalculated = true;
					this.SampleArrayByteArray();
					sourceDataLine.write(this.buff, 0, this.buff.length - 0 );
				} System.out.println("END");
                                this.isCalculated = false;
				this.sourceDataLine.drain();
				this.sourceDataLine.close();
			}catch (LineUnavailableException | IOException | InterruptedException | ExecutionException e) {
			}
	}
	

	private void echo(short[] inputSamples) {
		this.echo.setInputSampleStream(inputSamples);
		this.echo.createEffect();//создание эффекта
	}
	
	public boolean echoIsActive() { ////true если эффект в действии, false если произошла ошибка
		return this.isEcho;
	}
	
	public void setEcho(boolean b) {
		this.isEcho = b;//логическая установка реверберации
	}
	
	private void clipping(short[] inputSamples) {
		//this.clipping.setClippingCoef(this.clippingCoef);
		this.clipping.setInputSampleStream(inputSamples);
		this.clipping.createEffect();
	}
	
	public boolean clippingIsActive() { //true если эффект в действии, false если произошла ошибка
		return this.isClipping;
	}
	
	public void setClipping(boolean b) {
		this.isClipping = b;
	}
	
	public void setClippingCoef(double c) {
		this.clippingCoef = c;
	} 
	
	
	private void stop() {
		if(pause) {
			for(;;) {
				try {
					if(!pause) break;
                                        this.isCalculated = false;
					Thread.sleep(50);
				} 
                                catch (InterruptedException e) {
				}
			}
		}
	}
	
	public void setPause(boolean pause) {
		this.pause = pause;
	}
	
	public boolean getPause() {
		return this.pause;
	}
	
	public void setVolume(double volume) {
		this.volume = volume;
	}
	
	public double getVolume() {
		return this.volume;
	}

	@Override
	public void update(LineEvent event) { //Listens to the START and STOP events of the audio line.
 
		LineEvent.Type type = event.getType();
	}
	
        // Возвращает количество байт которое занимает один сэмпл
	public short[] getSamples() {
		return this.sampleBuff;
	}
	
	private void ByteArrayToSamplesArray() {
		for(int i = 0, j = 0; i < this.buff.length; i += 2 , j++) {
			this.sampleBuff[j] = (short) (0.5 *  (ByteBuffer.wrap(this.buff, i, 2).order(
					java.nio.ByteOrder.LITTLE_ENDIAN).getShort()) * this.getVolume());
		}
	}
	
	private void SampleArrayByteArray() {
		for(int i = 0, j = 0; i < this.sampleBuff.length && j < (this.buff.length); i++, j += 2 ) {
			this.buff[j] = (byte)(this.sampleBuff[i]);
			this.buff[j + 1] = (byte)(this.sampleBuff[i] >>> 8);			
		}
	}
	
	public Equalizer getEqualizer() {
		return this.equalizer;
	}
	
	public void close() {
		if(this.ais != null)
			try {
                            this.ais.close();
			} 
                        catch (IOException e) {
			}
		if(this.sourceDataLine != null)
                    this.sourceDataLine.close();
	}
	
        public double[] getFourierInput(){
            return this.fourierInput.getFFTData();
        }
        public double[] getFourierOutput(){
            return this.fourierOutput.getFFTData();
        }
}
