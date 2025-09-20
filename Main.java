import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;
import java.util.*;

public class PolynomialSolver {

    // Fetch JSON text from a URL
    private static String fetchJson(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    // Parse "base" and "value" pairs manually from JSON string
    private static Map<String, Map<String, String>> parseJsonRoots(String jsonText) {
        Map<String, Map<String, String>> result = new HashMap<>();

        // remove spaces and newlines for easier parsing
        String clean = jsonText.replaceAll("\\s+", "");

        // split by "},"
        String[] parts = clean.split("},");
        for (String part : parts) {
            if (part.contains("\"keys\"")) continue; // skip keys object

            // find root id
            int quoteIdx = part.indexOf("\"");
            if (quoteIdx == -1) continue;
            String id = part.substring(quoteIdx + 1, part.indexOf("\"", quoteIdx + 1));

            if (!id.matches("\\d+")) continue; // only numeric keys

            // find base
            String base = "";
            String value = "";
            if (part.contains("\"base\"")) {
                int baseStart = part.indexOf("\"base\":\"") + 8;
                int baseEnd = part.indexOf("\"", baseStart);
                base = part.substring(baseStart, baseEnd);
            }
            if (part.contains("\"value\"")) {
                int valStart = part.indexOf("\"value\":\"") + 9;
                int valEnd = part.indexOf("\"", valStart);
                value = part.substring(valStart, valEnd);
            }

            Map<String, String> inner = new HashMap<>();
            inner.put("base", base);
            inner.put("value", value);
            result.put(id, inner);
        }
        return result;
    }

    // Convert base + value to decimal BigInteger
    private static BigInteger parseValue(String baseStr, String value) {
        int base = Integer.parseInt(baseStr);
        return new BigInteger(value, base);
    }

    // Generate polynomial coefficients from roots
    // Polynomial: (x - r1)(x - r2)...(x - rn)
    private static BigInteger[] polynomialFromRoots(List<BigInteger> roots) {
        BigInteger[] coeffs = { BigInteger.ONE }; // start with 1
        for (BigInteger r : roots) {
            BigInteger[] newCoeffs = new BigInteger[coeffs.length + 1];
            Arrays.fill(newCoeffs, BigInteger.ZERO);

            for (int i = 0; i < coeffs.length; i++) {
                // x * coeffs[i]
                newCoeffs[i] = newCoeffs[i].add(coeffs[i]);
                // -r * coeffs[i]
                newCoeffs[i + 1] = newCoeffs[i + 1].subtract(coeffs[i].multiply(r));
            }
            coeffs = newCoeffs;
        }
        return coeffs;
    }

    public static void main(String[] args) {
        try {
            // 🔴 Replace this with your actual raw JSON link
            String jsonUrl = "https://raw.githubusercontent.com/Ajayvarma-github/polynomialproblem/refs/heads/main/Ajay.json";

            // Fetch JSON
            String jsonText = fetchJson(jsonUrl);

            // Parse roots
            Map<String, Map<String, String>> rootsData = parseJsonRoots(jsonText);

            List<BigInteger> roots = new ArrayList<>();
            for (String key : rootsData.keySet()) {
                String base = rootsData.get(key).get("base");
                String value = rootsData.get(key).get("value");
                roots.add(parseValue(base, value));
            }

            // Print roots
            Collections.sort(roots);
            System.out.println("Roots (decimal): " + roots);

            // Polynomial coefficients
            BigInteger[] coeffs = polynomialFromRoots(roots);
            System.out.println("Polynomial coefficients (highest degree → constant):");
            for (BigInteger c : coeffs) {
                System.out.print(c.toString() + " ");
            }
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
