import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class Profiler {

    // Mesure le temps d'exécution d'une méthode donnée
    public static <T> T profileExecution(String taskName, ProfilerTask<T> task) {
        System.out.println("Profiling task: " + taskName);

        // Mesurer la mémoire initiale
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage beforeMemory = memoryMXBean.getHeapMemoryUsage();
        long beforeUsedMemory = beforeMemory.getUsed();

        // Mesurer le temps d'exécution
        long startTime = System.nanoTime();
        T result = task.execute();
        long endTime = System.nanoTime();

        // Mesurer la mémoire après exécution
        MemoryUsage afterMemory = memoryMXBean.getHeapMemoryUsage();
        long afterUsedMemory = afterMemory.getUsed();

        // Résultats
        System.out.println("Execution time: " + (endTime - startTime) / 1e6 + " ms");
        System.out.println("Memory used: " + (afterUsedMemory - beforeUsedMemory) / 1024 + " KB");

        return result;
    }

    // Interface fonctionnelle pour les tâches à profiler
    public interface ProfilerTask<T> {
        T execute();
    }
}
