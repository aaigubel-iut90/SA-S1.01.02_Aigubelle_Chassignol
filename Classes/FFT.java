public class FFT {

    public static Complex[] fft(double[] input) {
        int n = input.length;
        Complex[] complexInput = new Complex[n];
        for (int i = 0; i < n; i++) {
            complexInput[i] = new Complex(input[i], 0);
        }
        return fft(complexInput);
    }

    public static Complex[] fft(Complex[] input) {
        int n = input.length;
        if (n == 1) return new Complex[]{input[0]};

        // Diviser en deux sous-séries
        Complex[] even = new Complex[n / 2];
        Complex[] odd = new Complex[n / 2];
        for (int i = 0; i < n / 2; i++) {
            even[i] = input[2 * i];
            odd[i] = input[2 * i + 1];
        }

        // FFT récursive
        Complex[] fftEven = fft(even);
        Complex[] fftOdd = fft(odd);

        // Combinaison
        Complex[] result = new Complex[n];
        for (int k = 0; k < n / 2; k++) {
            Complex t = Complex.exp(-2 * Math.PI * k / n).multiply(fftOdd[k]);
            result[k] = fftEven[k].add(t);
            result[k + n / 2] = fftEven[k].subtract(t);
        }
        return result;
    }

    public static Complex[] ifft(Complex[] input) {
        int n = input.length;
        Complex[] conjugated = new Complex[n];
        for (int i = 0; i < n; i++) {
            conjugated[i] = input[i].conjugate();
        }
        Complex[] fftResult = fft(conjugated);
        for (int i = 0; i < n; i++) {
            fftResult[i] = fftResult[i].conjugate().scale(1.0 / n);
        }
        return fftResult;
    }
}
