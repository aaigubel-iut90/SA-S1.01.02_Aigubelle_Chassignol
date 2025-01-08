public class Correlation {

    // Implémentation de l'approche naïve
    public static double[] naiveCrossCorrelation(double[] signal1, double[] signal2) {
        int n = signal1.length;
        double[] result = new double[n];

        for (int tau = 0; tau < n; tau++) {
            double sum = 0.0;
            for (int i = 0; i < n - tau; i++) {
                sum += signal1[i] * signal2[i + tau];
            }
            result[tau] = sum;
        }
        return result;
    }

    // Implémentation de l'approche FFT
    public static double[] fftCrossCorrelation(double[] signal1, double[] signal2) {
        int n = signal1.length;

        // Étape 1 : Étendre les signaux pour éviter les effets de bord
        double[] paddedSignal1 = padSignal(signal1, 2 * n);
        double[] paddedSignal2 = padSignal(signal2, 2 * n);

        // Étape 2 : Calcul de la FFT des signaux
        Complex[] fft1 = FFT.fft(paddedSignal1);
        Complex[] fft2 = FFT.fft(paddedSignal2);

        // Étape 3 : Multiplication par le conjugué complexe
        Complex[] product = new Complex[fft1.length];
        for (int i = 0; i < fft1.length; i++) {
            product[i] = fft1[i].multiply(fft2[i].conjugate());
        }

        // Étape 4 : Transformée inverse (IFFT)
        Complex[] ifft = FFT.ifft(product);

        // Retourner seulement la partie réelle des n premiers échantillons
        double[] result = new double[n];
        for (int i = 0; i < n; i++) {
            result[i] = ifft[i].re();
        }

        return result;
    }

    // Méthode pour ajouter du padding à un signal
    private static double[] padSignal(double[] signal, int newLength) {
        double[] padded = new double[newLength];
        System.arraycopy(signal, 0, padded, 0, signal.length);
        return padded;
    }
}
