public class CrossCorrelation2 {

    /**
     * Effectue une transformation FFT ou IFFT.
     * 
     * @param real     Tableau des parties réelles.
     * @param imag     Tableau des parties imaginaires.
     * @param inverse  Si true, calcule l'IFFT. Sinon, calcule la FFT.
     * @return Tableau 2D : première ligne = partie réelle, deuxième ligne = partie imaginaire.
     */
    public static double[][] crosscorrelation(double[] real, double[] imag, boolean inverse) {
        int n = real.length;

        // Vérifie que la taille est une puissance de 2
        if (Integer.bitCount(n) != 1) {
            throw new IllegalArgumentException("La taille du tableau doit être une puissance de 2.");
        }

        // Base case: si la taille est 1, retourne directement l'entrée
        if (n == 1) {
            return new double[][] { { real[0] }, { imag[0] } };
        }

        // Divise pour régner : décompose le signal en pairs et impairs
        double[] realEven = new double[n / 2];
        double[] imagEven = new double[n / 2];
        double[] realOdd = new double[n / 2];
        double[] imagOdd = new double[n / 2];

        for (int i = 0; i < n / 2; i++) {
            realEven[i] = real[2 * i];
            imagEven[i] = imag[2 * i];
            realOdd[i] = real[2 * i + 1];
            imagOdd[i] = imag[2 * i + 1];
        }

        // Appels récursifs
        double[][] fftEven = crosscorrelation(realEven, imagEven, inverse);
        double[][] fftOdd = crosscorrelation(realOdd, imagOdd, inverse);

        // Prépare le résultat
        double[][] result = new double[2][n];

        // Calcul des coefficients
        double angle = 2 * Math.PI / n * (inverse ? -1 : 1); // Angle pour FFT ou IFFT
        double wReal = 1.0; // Partie réelle de w
        double wImag = 0.0; // Partie imaginaire de w
        double wnReal = Math.cos(angle); // Partie réelle de racine de l'unité
        double wnImag = Math.sin(angle); // Partie imaginaire de racine de l'unité

        for (int k = 0; k < n / 2; k++) {
            // Calcul de w * fftOdd[k]
            double tempReal = wReal * fftOdd[0][k] - wImag * fftOdd[1][k];
            double tempImag = wReal * fftOdd[1][k] + wImag * fftOdd[0][k];

            // Combine les parties réelle et imaginaire
            result[0][k] = fftEven[0][k] + tempReal;
            result[1][k] = fftEven[1][k] + tempImag;

            result[0][k + n / 2] = fftEven[0][k] - tempReal;
            result[1][k + n / 2] = fftEven[1][k] - tempImag;

            // Met à jour w (w *= wn)
            double newWReal = wReal * wnReal - wImag * wnImag;
            double newWImag = wReal * wnImag + wImag * wnReal;
            wReal = newWReal;
            wImag = newWImag;
        }

        // Si c'est une IFFT, divise par la taille pour normaliser
        if (inverse) {
            for (int i = 0; i < n; i++) {
                result[0][i] /= n;
                result[1][i] /= n;
            }
        }

        return result;
    }
}
