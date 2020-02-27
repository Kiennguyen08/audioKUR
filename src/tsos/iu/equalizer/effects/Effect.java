package tsos.iu.equalizer.effects;

//Описание класса Эффектов
public abstract class Effect {
	protected short[] inputAudioStream;
	public abstract short[] createEffect();
	public abstract short[] getOutputAudioStream();
}
