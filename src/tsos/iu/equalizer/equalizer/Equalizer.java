package tsos.iu.equalizer.equalizer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// Описание класса Эквалайзера. Создание фильтров
public class Equalizer {
	
	private short[] inputSignal;
	private short[]outputSignal;
	private Filter[] filters;
        // объявление констант 
	private final static int COUNT_OF_BANDS= 6;
	private final static char COUNT_OF_THREADS = 1;
	private final int lenghtOfInputSignal;
	ExecutorService pool; //ExecutorService исполняет асинхронный код в одном или нескольких потоках
	
	public Equalizer(final int lenghtOfInputSignal) { // 
               
		pool = Executors.newFixedThreadPool(COUNT_OF_THREADS); // формирование пула с 6ю потоками
		this.lenghtOfInputSignal = lenghtOfInputSignal; // поределение длины входного сигнала
		this.createFilters();
	}
	
	public void setInputSignal(short[] inputSignal) {
		this.inputSignal = inputSignal; //объявление входного сигнала
		this.outputSignal = new short[this.lenghtOfInputSignal]; //объявление выходного сигнала длиной = входному сигналу
		this.filters[0].settings(FilterCoef.COEFS_OF_BAND_0, // применение коэффициентов для каждого рассчитанного фильтра из класса FilterCoef
				FilterCoef.COUNT_OF_COEFS, this.inputSignal);
		this.filters[1].settings(FilterCoef.COEFS_OF_BAND_1, 
				FilterCoef.COUNT_OF_COEFS, this.inputSignal);
		this.filters[2].settings(FilterCoef.COEFS_OF_BAND_2, 
				FilterCoef.COUNT_OF_COEFS, this.inputSignal);
		this.filters[3].settings(FilterCoef.COEFS_OF_BAND_3, 
				FilterCoef.COUNT_OF_COEFS, this.inputSignal);
		this.filters[4].settings(FilterCoef.COEFS_OF_BAND_4, 
				FilterCoef.COUNT_OF_COEFS, this.inputSignal);
		this.filters[5].settings(FilterCoef.COEFS_OF_BAND_5, 
				FilterCoef.COUNT_OF_COEFS, this.inputSignal);
	}
	
	
	private void createFilters() {
		this.filters = new  Filter [Equalizer.COUNT_OF_BANDS] ;
		this.filters[0] = new Filter(this.lenghtOfInputSignal);
		this.filters[1] = new Filter(this.lenghtOfInputSignal);
		this.filters[2] = new Filter(this.lenghtOfInputSignal);
		this.filters[3] = new Filter(this.lenghtOfInputSignal);
		this.filters[4] = new Filter(this.lenghtOfInputSignal);
		this.filters[5] = new Filter(this.lenghtOfInputSignal);
		
}

	
	public void equalization( ) throws InterruptedException, ExecutionException {
		Future<short[]>[] fs = new Future[Equalizer.COUNT_OF_BANDS]; // асинхронный код, который применяет к каждой полосе свои коэффициенты
		for(int i = 0; i < Equalizer.COUNT_OF_BANDS; i++){ 
			fs[i] = pool.submit(this.filters[i]);
		}
		
		for(int i = 0; i < this.outputSignal.length; i++) { // формирование выходного сигнала путем сложения всех сигналов по каждой полосе пропускания
			this.outputSignal[i] += fs[0].get()[i] +
						fs[1].get()[i] +
						fs[2].get()[i] +
                                                fs[3].get()[i] +
                                                fs[4].get()[i] +
						fs[5].get()[i];		
		}
	}
	
	public Filter getFilter(short nF) {
		return this.filters[nF];
	}
	
	public short[] getOutputSignal() {
		try {
			Thread.sleep(0);
		} catch (InterruptedException e) {
		}
		return this.outputSignal;
	}
	
	public void close() {
		if(this.pool != null) {
			this.pool.shutdown();
		}
	}
}
