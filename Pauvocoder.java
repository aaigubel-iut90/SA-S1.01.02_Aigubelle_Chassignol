public class Pauvocoder {

    public static void joue(double[] input) {
        if (input == null || input.length == 0) {
            throw new IllegalArgumentException("Input array cannot be null or empty.");
        }

        // Définir la taille de la fenêtre glissante pour l'affichage en temps réel (en nombre d'échantillons)
        final int windowSize = 100;  // Taille de la fenêtre en échantillons
        final int sampleRate = StdAudio.SAMPLE_RATE;

        // Lecture des échantillons audio
        for (int i = 0; i < input.length; i++) {
            // Jouer l'échantillon actuel
            StdAudio.play(input[i]);

            // Affichage en temps réel des échantillons dans la fenêtre glissante
            if (i % windowSize == 0) {
                // Affichage des 100 derniers échantillons
                System.out.println("Échantillons de " + i + " à " + Math.min(i + windowSize, input.length) + ":");
                for (int j = i; j < Math.min(i + windowSize, input.length); j++) {
                    System.out.print(String.format("%.4f ", input[j]));
                }
                System.out.println("\n---");
            }

            // Attendre un petit délai avant de lire le prochain échantillon (en fonction du taux d'échantillonnage)
            try {
                Thread.sleep(1000 / sampleRate);  // Attente correspondant à un échantillon (en ms)
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static double[] echo(double[] input, double delayMs, double attn) {
        if (input == null || input.length == 0) {
            throw new IllegalArgumentException("Input array cannot be null or empty.");
        }
        if (delayMs <= 0) {
            throw new IllegalArgumentException("Delay must be greater than 0 milliseconds.");
        }
        if (attn < 0 || attn > 1) {
            throw new IllegalArgumentException("Attenuation (attn) must be between 0 and 1.");
        }

        // Convert delay from milliseconds to number of samples
        int sampleRate = StdAudio.SAMPLE_RATE;
        int delaySamples = (int) (delayMs / 1000.0 * sampleRate);

        // Create an output array with room for the echo
        double[] output = new double[input.length + delaySamples];

        // Copy the original signal into the output
        for (int i = 0; i < input.length; i++) {
            output[i] = input[i];
        }

        // Add the echo with attenuation
        for (int i = 0; i < input.length; i++) {
            int echoIndex = i + delaySamples;
            if (echoIndex < output.length) {
                output[echoIndex] += input[i] * attn;
            }
        }

        return output;
    }
}
