import java.io.IOException;
import java.nio.file.*;
import java.util.*;

class MermaidFile {
    private static void print(String fmt, Object... args) {
        System.out.println(String.format(fmt, args));
    }

    private static String[] readAllLines(String filePath, boolean log) {
        if (log) print("Reading from '%s' ... ", filePath);
        String text = "";

        try {
            text = Files.readString(Path.of(filePath));
        }
        catch (NoSuchFileException ex) {
            print("Can't find file '%s'", filePath);
        }
        catch (IOException ex) {
            print("Failed to read from '%s'", filePath);
            ex.printStackTrace();
        }

        var lines = text.split("[\\r\\n]+");
        if (log) print("%d lines", lines.length);

       return lines;
    }

    private static String[] getGraphLines(String graphName, String filePath, String[] lines, boolean log) {
        if (log) print("Looking for '%s: %s' ... ", "graphName", graphName);
        var graph = new ArrayList<String>();

        var graphIsHere = false;
        var namesFound = new ArrayList<String>();

        for (var ln : lines) {
            ln = ln.trim();

            if (ln.startsWith("#!mermaid")) {
                if (graphIsHere)
                    break;

                graph.clear();
                continue;
            }

            if (ln.startsWith("---"))
                continue;

            if (ln.contains("title")) {
                var parts = ln.split(":");
                if (parts.length != 2) continue;
                if (!parts[0].trim().toLowerCase().equals("title")) continue;

                if (parts[1].trim().toLowerCase().equals(graphName.toLowerCase())) {
                    graphIsHere = true;
                }

                namesFound.add(parts[1].trim());
                continue;
            }

            if (ln.startsWith("%%")) {
                var pair = ln.substring(2).trim().split(":");

                if (pair.length != 2) continue;

                var argName = pair[0].trim();
                var value = pair[1].trim();

                if (!argName.toLowerCase().equals("graphname")) continue;

                if (value.toLowerCase().equals(graphName.toLowerCase())) {
                    graphIsHere = true;
                }

                namesFound.add(value);
                continue;
            }

            graph.add(ln);
        }

        if (graphIsHere) {
            if (log) print("'%s' is found: %d lines", graphName, graph.size());
            return graph.toArray(new String[graph.size()]);
        }

        var namesInFile = Arrays.toString(namesFound.toArray());
        print("%s: '%s' is not found; (names seen: %s)", filePath, graphName, namesInFile);

        return new String[0];
    }

    public static String[] readGraphLines(String graphName, String filePath, boolean log) {
        var lines = readAllLines(filePath, log);
        var graph = getGraphLines(graphName, filePath, lines, log);

        return graph;
    }
}
