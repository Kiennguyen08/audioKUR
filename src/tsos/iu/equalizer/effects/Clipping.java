package tsos.iu.equalizer.effects;

public class Clipping extends Effect {
	
	private short maxAmplitude;
	private short minAmplitude;
	private static final short standartMax = 3500;
	private static final short standartMin = -3500;
	private double coef;
	
	public Clipping() {
		super(); 
		this.coef = 1.0;
	}
	
	public void setInputSampleStream(short[] inputAudioStream) {
		this.inputAudioStream = inputAudioStream;		
	}

	@Override
	public synchronized short[] createEffect() {
		this.setMaxAndMinAmpl();
		for(int i = 0; i < this.inputAudioStream.length; i ++) {
			if(this.inputAudioStream[i] > this.maxAmplitude)
				this.inputAudioStream[i] = (this.maxAmplitude);
			else if(this.inputAudioStream[i] < this.minAmplitude)
				this.inputAudioStream[i] = (this.minAmplitude);
		}
		return this.inputAudioStream;
	}
	
	private void  setMaxAndMinAmpl() {
		this.maxAmplitude = (short) (Clipping.standartMax * this.coef);
		this.minAmplitude = (short) (Clipping.standartMin * this.coef);
	}
	
	public void setClippingCoef(double coef) {
		this.coef = coef;
	}

	@Override
	public synchronized short[] getOutputAudioStream() {
		return this.inputAudioStream;
	}

}