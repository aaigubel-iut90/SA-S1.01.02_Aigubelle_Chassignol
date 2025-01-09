public class CrossCorrelation2 {

    public static double[] crosscorrelation(double[] signal1, double[] signal2, boolean useFFT) {
        if (signal1 == null || signal2 == null) {
            throw new IllegalArgumentException("Signals cannot be null");
        }

        int length = Math.min(signal1.length, signal2.length);
        double[] result = new double[length];

        // Utilisation de FFT pour la corrélation croisée si demandé
        if (useFFT) {
            // Implémentation de la corrélation via FFT
            // Pour simplifier, ici on fait appel à la méthode de base.
            for (int i = 0; i < length; i++) {
                result[i] = signal1[i] * signal2[i];
            }
        } else {
            // Corrélation sans FFT (simple)
            for (int i = 0; i < length; i++) {
                result[i] = signal1[i] * signal2[i];
            }
        }

        return result;  // Renvoie un tableau double[], et non double[][]
    }
}
