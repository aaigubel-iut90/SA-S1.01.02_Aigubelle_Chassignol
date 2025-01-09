public class CrossCorrelation1 {

    public static double[] crosscorrelation(double[] signal1, double[] signal2) {
        if (signal1 == null || signal2 == null) {
            throw new IllegalArgumentException("Signals cannot be null");
        }

        int length = Math.min(signal1.length, signal2.length);
        double[] result = new double[length];

        // Simple cross-correlation
        for (int i = 0; i < length; i++) {
            double sum = 0;
            for (int j = 0; j < length - i; j++) {
                sum += signal1[j] * signal2[j + i];
            }
            result[i] = sum;
        }

        return result;  // Renvoie un tableau double[]
    }
}
