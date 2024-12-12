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
        //double[] outputWav   = vocodeSimple(newPitchWav, 1.0/freqScale);
        //StdAudio.save(outPutFile+"Simple.wav", outputWav);

        // Simple dilatation with overlaping
        //outputWav = vocodeSimpleOver(newPitchWav, 1.0/freqScale);
        //StdAudio.save(outPutFile+"SimpleOver.wav", outputWav);

        // Simple dilatation with overlaping and maximum cross correlation search
        //outputWav = vocodeSimpleOverCross(newPitchWav, 1.0/freqScale);
        //StdAudio.save(outPutFile+"SimpleOverCross.wav", outputWav);

        //joue(outputWav);

        // Some echo above all
        //outputWav = echo(outputWav, 100, 0.7);
        //StdAudio.save(outPutFile+"SimpleOverCrossEcho.wav", outputWav);

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
            throw new IllegalArgumentException("Le tableau d'entrée ne peut pas être nul ou vide et freqScale doit être positif.");
        }
        
        // Calcule la taille du tableau de sortie pour toujours atteindre la fin du tableau d'entrée
        int outputLength = (int) (input.length / freqScale);
        double[] output = new double[outputLength];
        
        // Pour le sous-échantillonnage (freqScale < 1) et le sur-échantillonnage (freqScale > 1)
        for (int i = 0; i < outputLength; i++) {
            // Trouve la position dans le tableau d'entrée en utilisant le rapport inverse de freqScale
            double position = i * (input.length - 1) / (double) (outputLength - 1);
            
            // Calcul de l'index correspondant
            int index = (int) Math.round(position);
            
            if (index >= input.length - 1) {
                output[i] = input[input.length - 1];
            } else {
                output[i] = input[index];
            }
        }
        
        return output;
    }

    /**
     * Simple dilatation, without any overlapping
     * @param inputWav
     * @param dilatation factor
     * @return dilated wav
     */
    public static double[] vocodeSimple(double[] inputWav, double dilatation) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Simple dilatation, with overlapping
     * @param inputWav
     * @param dilatation factor
     * @return dilated wav
     */
    public static double[] vocodeSimpleOver(double[] inputWav, double dilatation) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Simple dilatation, with overlapping and maximum cross correlation search
     * @param inputWav
     * @param dilatation factor
     * @return dilated wav
     */
    public static double[] vocodeSimpleOverCross(double[] inputWav, double dilatation) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Play the wav
     * @param wav
     */
    public static void joue(double[] wav) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Add an echo to the wav
     * @param wav
     * @param delay in msec
     * @param gain
     * @return wav with echo
     */
    public static double[] echo(double[] wav, double delay, double gain) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Display the waveform
     * @param wav
     */
    public static void displayWaveform(double[] wav) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

}
