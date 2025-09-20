import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class Main {

    // Evaluate polynomial at x (BigInteger version)
    public static BigInteger evaluatePolynomial(List<BigInteger> coeffs, BigInteger x) {
        BigInteger result = BigInteger.ZERO;
        BigInteger power = BigInteger.ONE;
        for (BigInteger c : coeffs) {
            result = result.add(c.multiply(power));
            power = power.multiply(x);
        }
        return result;
    }

    // Build polynomial from roots (BigInteger version)
    public static List<BigInteger> generatePolynomial(List<BigInteger> roots) {
        int degree = roots.size();
        List<BigInteger> coeffs = new ArrayList<>(Collections.nCopies(degree + 1, BigInteger.ZERO));
        coeffs.set(0, BigInteger.ONE); // leading coefficient

        for (BigInteger root : roots) {
            List<BigInteger> newCoeffs = new ArrayList<>(Collections.nCopies(coeffs.size(), BigInteger.ZERO));
            for (int i = 0; i < coeffs.size() - 1; i++) {
                newCoeffs.set(i, newCoeffs.get(i).add(coeffs.get(i).multiply(root.negate())));
                newCoeffs.set(i + 1, newCoeffs.get(i + 1).add(coeffs.get(i)));
            }
            coeffs = newCoeffs;
        }
        return coeffs;
    }

    // Read file content into string
    public static String readFile(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line.trim());
        }
        br.close();
        return sb.toString();
    }

    public static void main(String[] args) {
        try {
            // Load input.json as plain string
            String json = readFile("input.json");

            // Extract n and k
            int n = Integer.parseInt(json.replaceAll(".*\"n\"\\s*:\\s*(\\d+).*", "$1"));
            int k = Integer.parseInt(json.replaceAll(".*\"k\"\\s*:\\s*(\\d+).*", "$1"));

            // Open writer for output file
            PrintWriter writer = new PrintWriter(new FileWriter("output.txt"));

            // Find all base/value pairs
            List<BigInteger> roots = new ArrayList<>();
            String[] entries = json.split("\\},");
            for (String entry : entries) {
                if (entry.contains("\"base\"")) {
                    String baseStr = entry.replaceAll(".*\"base\"\\s*:\\s*\"(\\w+)\".*", "$1");
                    String valueStr = entry.replaceAll(".*\"value\"\\s*:\\s*\"([\\w]+)\".*", "$1");
                    int base = Integer.parseInt(baseStr);
                    BigInteger decimalValue = new BigInteger(valueStr, base);
                    roots.add(decimalValue);
                }
            }

            writer.println("n = " + n + ", k = " + k);
            writer.println("Extracted roots in decimal:");
            for (BigInteger r : roots) {
                writer.println(r.toString());
            }

            // Use first k roots to form polynomial
            List<BigInteger> usedRoots = roots.subList(0, k);
            List<BigInteger> coefficients = generatePolynomial(usedRoots);

            writer.println("Polynomial coefficients (lowest to highest degree):");
            for (BigInteger c : coefficients) {
                writer.println(c.toString());
            }

            // Validate all roots
            writer.println("Validation of roots:");
            for (BigInteger root : roots) {
                BigInteger result = evaluatePolynomial(coefficients, root);
                writer.println("f(" + root + ") = " + result);
            }

            writer.close();
            System.out.println("Output written to output.txt");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} **