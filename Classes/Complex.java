public class Complex {
    private final double re;
    private final double im;

    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public Complex add(Complex b) {
        return new Complex(this.re + b.re, this.im + b.im);
    }

    public Complex subtract(Complex b) {
        return new Complex(this.re - b.re, this.im - b.im);
    }

    public Complex multiply(Complex b) {
        double real = this.re * b.re - this.im * b.im;
        double imag = this.re * b.im + this.im * b.re;
        return new Complex(real, imag);
    }

    public Complex conjugate() {
        return new Complex(this.re, -this.im);
    }

    public Complex scale(double alpha) {
        return new Complex(this.re * alpha, this.im * alpha);
    }

    public static Complex exp(double x) {
        return new Complex(Math.cos(x), Math.sin(x));
    }

    public double re() {
        return this.re;
    }

    public double im() {
        return this.im;
    }
}
