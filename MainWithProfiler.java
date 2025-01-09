public class MainWithProfiler {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: MainWithProfiler <input.wav> <freqScale>");
            return;
        }

        // Le fichier .wav et le facteur de mise à l'échelle sont passés en arguments
        String inputFilePath = args[0];
        double freqScale = Double.parseDouble(args[1]);

        // Chargement du fichier WAV
        double[] signal = StdAudio.read(inputFilePath);

        if (signal == null || signal.length == 0) {
            System.out.println("Erreur: Impossible de charger le fichier audio.");
            return;
        }

        // Affichage des informations du signal
        System.out.println("Signal chargé: " + inputFilePath + ", Taille du signal: " + signal.length + " échantillons.");

        // Profiling de l'approche naïve de la corrélation croisée
        double[] resultNaive = Profiler.profileExecution("Naive Cross-Correlation", () -> {
            // CrossCorrelation1 doit renvoyer un tableau double[], pas un double[][]
            return CrossCorrelation1.crosscorrelation(signal, signal); 
        });

        // Profiling de l'approche FFT de la corrélation croisée
        double[] resultFFT = Profiler.profileExecution("FFT Cross-Correlation", () -> {
            // CrossCorrelation2 doit renvoyer un tableau double[], pas un double[][]
            return CrossCorrelation2.crosscorrelation(signal, signal, false); 
        });

        // Profiler pour la méthode de lecture du signal
        Profiler.profileExecution("Playing Signal", () -> {
            Pauvocoder.joue(signal);
            return null;
        });

        // Ajout d'un écho et sauvegarde
        double[] signalAvecEcho = Pauvocoder.echo(signal, 100, 0.7);
        String outputFile = "output_with_echo.wav";
        StdAudio.save(outputFile, signalAvecEcho);
        System.out.println("Fichier sauvegardé sous : " + outputFile);
    }
}
