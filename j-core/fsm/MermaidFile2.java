import java.util.*;
import org.yaml.snakeyaml.Yaml;

class MermaidFile2 {
    public static class Graph {
        public String name;
        public String[] lines;
        public FSM.Props props;

        public Graph(String name, String[] lines, FSM.Props props) {
            this.name = name;
            this.lines = lines;
            this.props = props;
        }

        public String name() {
            return props.get("title");
        }

        public String toString() {
            return String.format("%s[%d lines, %d props]", name, lines.length, props.size());
        }
    }

    private static boolean theyAreSame(String lhs, String rhs) {
        if (lhs == null && rhs == null)
            return true;
        if (lhs == null || rhs == null)
            return false;
        return 0 == lhs.compareToIgnoreCase(rhs);
    }

    private static void print(String fmt, Object... args) {
        System.out.println(String.format(fmt, args));
    }

    private static String[] parseLines(String text) {
        var all = text.split("[\\r\\n]+");

        var lines = new ArrayList<String>();

        for (var ln: all) {
            ln = ln.trim();

            if (ln.equals(""))
                continue;

            lines.add(ln);
        }

        return lines.toArray(String[]::new);
    }

    private static FSM.Props parseProperties(String text, boolean log) {
        var map = new HashMap<String, String>();

        var docs = text.split("(?m)(^---.*$)");

        if (docs.length < 3) {
            if (log) print("WARN ===%s===\nYAML header is not found", text);
            return new FSM.Props(map);
        }

        var parser = new Yaml();
        Map<String, Object> yaml = parser.load(docs[1]);

        yaml.forEach((k, v) -> map.put(k.toLowerCase(), v.toString()));

        return new FSM.Props(map);
    }

    private static void parseGraphNameIfTitleIsMissing(String[] lines, FSM.Props props) {
        if (props.containsKey("title"))
            return;

        for (var ln: lines) {
            if (!ln.startsWith("%%")) continue;

            var pair = ln.substring(2).trim().split(":");

            if (pair.length != 2) continue;

            var argName = pair[0].trim();
            var value = pair[1].trim();

            if (!argName.toLowerCase().equals("graphname")) continue;

            props.put("title", value);
            break;
        }
    }

    private static Graph parseGraph(DibFile.Section section, boolean log) {
        var lines = parseLines(section.text);
        var props = parseProperties(section.text, log);

        parseGraphNameIfTitleIsMissing(lines, props);

        if (!props.containsKey("title")) {
            if (log) print("WARN ===%s===\nGraph name is not found (nor title, nor graphName)", section.text);
            props.put("title", "n/a");
        }

        var graphName = props.get("title");

        return new Graph(graphName, lines, props);
    }

    public static Graph loadGraph(String graphName, String path, boolean log) {

        var sections = DibFile.getAllSections("#!mermaid", path, log);
        
        var namesFound = new ArrayList<String>();
        Graph first = null; 

        for (var section: sections) {
            var graph = parseGraph(section, log);

            if (theyAreSame(graphName, graph.name())) {
                if (log) print("%s: %s is found", path, graphName);

                if (first != null) {
                    print("%s: ERROR: two graphs with the same name '%s'", path, graphName);
                    return null;
                }

                first = graph;
            }

            namesFound.add(graph.name());
        }

        if (first != null)
            return first;

        var namesInFile = Arrays.toString(namesFound.toArray());
        print("%s: '%s' is not found; (found graphs: %s)", path, graphName, namesInFile);

        return null;
    }
}
