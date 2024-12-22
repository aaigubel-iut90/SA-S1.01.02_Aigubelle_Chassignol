import static java.lang.System.exit;

public class Pauvocoder {

    // Processing SEQUENCE size (100 msec with 44100Hz samplerate)
    final static int SEQUENCE = StdAudio.SAMPLE_RATE/10;

    // Overlapping size (20 msec)
    final static int OVERLAP = SEQUENCE/5 ;
    // Best OVERLAP offset seeking window (15 msec)
    final static int SEEK_WINDOW = 3*OVERLAP/4;

    public static void main(String[] args) {
        if (args.length < 2)
        {
            System.out.println("usage: pauvocoder <input.wav> <freqScale>\n");
            exit(1);
        }


        String wavInFile = args[0];
        double freqScale = Double.valueOf(args[1]);
        String outPutFile= wavInFile.split("\\.")[0] + "_" + freqScale +"_";

        // Open input .wev file
        double[] inputWav = StdAudio.read(wavInFile);

        // Resample test
        double[] newPitchWav = resample(inputWav, freqScale);
        StdAudio.save(outPutFile+"Resampled.wav", newPitchWav);

        // Simple dilatation
        double[] outputWav   = vocodeSimple(newPitchWav, 1.0/freqScale);
        StdAudio.save(outPutFile+"Simple.wav", outputWav);

        // Simple dilatation with overlaping
        outputWav = vocodeSimpleOver(newPitchWav, 1.0/freqScale);
        StdAudio.save(outPutFile+"SimpleOver.wav", outputWav);

        // Simple dilatation with overlaping and maximum cross correlation search
        outputWav = vocodeSimpleOverCross(newPitchWav, 1.0/freqScale);
        StdAudio.save(outPutFile+"SimpleOverCross.wav", outputWav);

        joue(outputWav);

        // Some echo above all
        outputWav = echo(outputWav, 100, 0.7);
        StdAudio.save(outPutFile+"SimpleOverCrossEcho.wav", outputWav);

        // Display waveform
        //displayWaveform(outputWav);

    }

    /**
     * Resample inputWav with freqScale
     * @param inputWav
     * @param freqScale
     * @return resampled wav
     */
    public static double[] resample(double[] input, double freqScale) {
        if (input == null || input.length == 0 || freqScale <= 0) {
            throw new IllegalArgumentException("Input array cannot be null or empty, and freqScale must be positive.");
        }

        // Calculate the length of the resampled array
        int outputLength = (int) (input.length / freqScale);
        double[] output = new double[outputLength];

        // Perform resampling by interpolation
        for (int i = 0; i < outputLength; i++) {
            double position = i * freqScale; // Corresponding position in the input array
            int index = (int) Math.floor(position);

            if (index >= input.length - 1) {
                // If we exceed the input array, use the last value
                output[i] = input[input.length - 1];
            } else {
                // Linear interpolation between two samples
                double fraction = position - index;
                output[i] = input[index] * (1 - fraction) + input[index + 1] * fraction;
            }
        }

        return output;
    }

    /**
 * Implements a minimal time dilation algorithm.
 * 
 * @param input     the input signal as an array of doubles
 * @param timeScale the time dilation factor (greater than 1 compresses, less than 1 dilates)
 * @return a new signal array with time dilation/compression applied
 */
public static double[] vocodeSimple(double[] input, double timeScale) {
    if (input == null || input.length == 0) {
        throw new IllegalArgumentException("Input array cannot be null or empty.");
    }
    if (timeScale <= 0) {
        throw new IllegalArgumentException("TimeScale must be positive.");
    }

    // Calculate the size of the output array
    int outputLength = (int) (input.length / timeScale);
    double[] output = new double[outputLength];

    // Perform time dilation/compression using linear interpolation
    for (int i = 0; i < outputLength; i++) {
        // Map output index to input index
        double position = i * timeScale; // Corresponding position in the input array
        int index = (int) Math.floor(position);

        if (index >= input.length - 1) {
            // If we exceed the input array, use the last value
            output[i] = input[input.length - 1];
        } else {
            // Linear interpolation between two samples
            double fraction = position - index;
            output[i] = input[index] * (1 - fraction) + input[index + 1] * fraction;
        }
    }

    return output;
}


    /**
 * Implements a time dilation algorithm with overlapping to reduce clicks.
 * 
 * @param input     the input signal as an array of doubles
 * @param timeScale the time dilation factor (greater than 1 compresses, less than 1 dilates)
 * @return a new signal array with time dilation/compression applied
 */
public static double[] vocodeSimpleOver(double[] input, double timeScale) {
    if (input == null || input.length == 0) {
        throw new IllegalArgumentException("Input array cannot be null or empty.");
    }
    if (timeScale <= 0) {
        throw new IllegalArgumentException("TimeScale must be positive.");
    }

    // Sequence size (100ms at sample rate)
    final int sequenceSize = StdAudio.SAMPLE_RATE / 10;
    final int overlapSize = sequenceSize / 5; // 20ms overlap

    // Calculate the output length
    int outputLength = (int) (input.length / timeScale);
    double[] output = new double[outputLength];

    // Output position tracking
    int outputIndex = 0;

    for (int inputIndex = 0; inputIndex + sequenceSize <= input.length; 
         inputIndex += sequenceSize - overlapSize) {

        // Extract a single sequence
        double[] sequence = new double[sequenceSize];
        System.arraycopy(input, inputIndex, sequence, 0, sequenceSize);

        // Scale the sequence using vocodeSimple
        double[] scaledSequence = vocodeSimple(sequence, timeScale);

        // Add scaled sequence to the output with overlap
        for (int i = 0; i < scaledSequence.length; i++) {
            if (outputIndex + i < output.length) {
                // Apply crossfade during overlap region
                if (i < overlapSize) {
                    double fadeIn = (double) i / overlapSize;
                    double fadeOut = 1.0 - fadeIn;
                    output[outputIndex + i] = fadeOut * output[outputIndex + i] + fadeIn * scaledSequence[i];
                } else {
                    output[outputIndex + i] = scaledSequence[i];
                }
            }
        }

        // Move the output index forward by the non-overlapping portion
        outputIndex += sequenceSize - overlapSize;
    }

    return output;
}


    /**
 * Implements a time dilation algorithm with overlapping and cross-correlation optimization.
 * 
 * @param input     the input signal as an array of doubles
 * @param timeScale the time dilation factor (greater than 1 compresses, less than 1 dilates)
 * @return a new signal array with time dilation/compression applied
 */
public static double[] vocodeSimpleOverCross(double[] input, double timeScale) {
    if (input == null || input.length == 0) {
        throw new IllegalArgumentException("Input array cannot be null or empty.");
    }
    if (timeScale <= 0) {
        throw new IllegalArgumentException("TimeScale must be positive.");
    }

    // Sequence size (100ms at sample rate)
    final int sequenceSize = StdAudio.SAMPLE_RATE / 10;
    final int overlapSize = sequenceSize / 5; // 20ms overlap
    final int seekWindowSize = overlapSize / 2; // Seek window for cross-correlation

    // Calculate the output length
    int outputLength = (int) (input.length / timeScale);
    double[] output = new double[outputLength];

    // Output position tracking
    int outputIndex = 0;

    for (int inputIndex = 0; inputIndex + sequenceSize <= input.length; 
         inputIndex += sequenceSize - overlapSize) {

        // Extract a single sequence
        double[] sequence = new double[sequenceSize];
        System.arraycopy(input, inputIndex, sequence, 0, sequenceSize);

        // Scale the sequence using vocodeSimple
        double[] scaledSequence = vocodeSimple(sequence, timeScale);

        // Perform cross-correlation directly to find the best overlap position
        int bestOffset = 0;
        double maxCorrelation = Double.NEGATIVE_INFINITY;

        for (int offset = -seekWindowSize; offset <= seekWindowSize; offset++) {
            double correlation = 0.0;

            // Compute correlation for this offset
            for (int i = 0; i < overlapSize; i++) {
                int outputPosition = outputIndex + i + offset;
                if (outputPosition < 0 || outputPosition >= output.length) {
                    continue; // Ignore out-of-bounds indices
                }

                correlation += output[outputPosition] * scaledSequence[i];
            }

            // Update the best offset if this one has a higher correlation
            if (correlation > maxCorrelation) {
                maxCorrelation = correlation;
                bestOffset = offset;
            }
        }

        // Add scaled sequence to the output with overlap at the best offset
        for (int i = 0; i < scaledSequence.length; i++) {
            int adjustedIndex = outputIndex + i + bestOffset;
            if (adjustedIndex < 0 || adjustedIndex >= output.length) continue;

            // Apply crossfade during overlap region
            if (i < overlapSize) {
                double fadeIn = (double) i / overlapSize;
                double fadeOut = 1.0 - fadeIn;
                output[adjustedIndex] = fadeOut * output[adjustedIndex] + fadeIn * scaledSequence[i];
            } else {
                output[adjustedIndex] = scaledSequence[i];
            }
        }

        // Move the output index forward by the non-overlapping portion
        outputIndex += sequenceSize - overlapSize;
    }

    return output;
}


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


    /**
 * Add an echo to the input signal.
 * 
 * @param input   the input signal as an array of doubles
 * @param delayMs the delay of the echo in milliseconds
 * @param attn    the attenuation factor for the echo (between 0 and 1)
 * @return a new signal array with the echo added
 */
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


    /**
     * Display the waveform
     * @param wav
     */
    public static void displayWaveform(double[] wav) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
