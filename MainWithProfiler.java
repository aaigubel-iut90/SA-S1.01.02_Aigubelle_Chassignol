public class MainWithProfiler {

    public static void main(String[] args) {
        // Génération d'un signal d'exemple
        double[] signal1 = generateSignal(1000, 440); // Signal sinusoïdal 440 Hz
        double[] signal2 = generateSignal(1000, 880); // Signal sinusoïdal 880 Hz

        // Profiling de l'approche naïve
        double[] resultNaive = Profiler.profileExecution("Naive Cross-Correlation", () ->
            CrossCorrelation1.crosscorrelation(signal1, signal2)
        );

        // Profiling de l'approche FFT
        double[] resultFFT = Profiler.profileExecution("FFT Cross-Correlation", () ->
            CrossCorrelation2.crosscorrelation(signal1, signal2,false)
        );

        // Profiling de l'implémentation joue
        Profiler.profileExecution("Playing Signal", () ->
            {
                Pauvocoder.joue(signal1);
                return null;
            }
        );
    }

    // Générer un signal sinusoïdal pour les tests
    public static double[] generateSignal(int length, double frequency) {
        double[] signal = new double[length];
        double sampleRate = 44100;
        for (int i = 0; i < length; i++) {
            signal[i] = Math.sin(2 * Math.PI * frequency * i / sampleRate);
        }
        return signal;
    }
}
   
