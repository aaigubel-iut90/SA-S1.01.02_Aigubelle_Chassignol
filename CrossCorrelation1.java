public class CrossCorrelation1 {

    // Implémentation de l'approche naïve
    public static double[] crosscorrelation(double[] signal1, double[] signal2) {
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
}
