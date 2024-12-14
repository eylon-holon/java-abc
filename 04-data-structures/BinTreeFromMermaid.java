import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

class BinTreeFromMermaid {
    private static String[] readAllLinesFrom(String filePath, boolean log) {
        if (log) System.out.print(String.format("Reading from '%s' ... ", filePath));
        String text = "";

        try {
            text = Files.readString(Path.of(filePath));
        }
        catch (NoSuchFileException ex) {
            System.out.println(String.format("Can't find file '%s'", filePath));
        }
        catch (IOException ex) {
            System.out.println(String.format("Failed to read from '%s'", filePath));
            ex.printStackTrace();
        }

        var lines = text.split("[\\r\\n]+");
        if (log) System.out.println(String.format("%d lines", lines.length));

       return lines;
    }

    private static String[] getGraphLines(String graphName, String[] lines, boolean log) {
        if (log) System.out.print(String.format("Looking for '%s: %s' ... ", "graphName", graphName));
        var graph = new ArrayList<String>();

        for (var ln : lines) {
            ln = ln.trim();

            if (ln.startsWith("#!mermaid")) {
                graph.clear();
                continue;
            }

            if (ln.startsWith("%%")) {
                var pair = ln.substring(2).trim().split(":");

                if (pair.length != 2)
                    continue;

                var argName = pair[0].trim();
                var value = pair[1].trim();

                if (!argName.toLowerCase().equals("graphname"))
                    continue;

                if (!value.toLowerCase().equals(graphName.toLowerCase()))
                    continue;

                if (log) System.out.println(String.format("%d lines", graph.size()));

                return graph.toArray(new String[graph.size()]);
            }

            graph.add(ln);
        }

        System.out.println(String.format("Graph '%s' is not found", graphName));
        System.out.println(String.format("Please add '%%%% graphName: %s' as a last line to your mermaid graph", graphName));

        return new String[0];
    }

    static class Node {
        public Node(String name) {
            this.name = name;
        }

        public String name;
        public int value = 0;
        public ArrayList<Node> children = new ArrayList<>();
        public BinNode target = new BinNode(0);
    }

    static class Parser {
        private Node root;
        private HashMap<String, Node> nodes = new HashMap<>();

        private Node getNode(String name) {
            var node = nodes.get(name);

            if (node != null)
                return node;

            node = new Node(name);
            nodes.put(name, node);

            return node;
        }

        private Node parseNode(String strNode) {
            var parts = strNode.split("\\(\\(");
            var node = getNode(parts[0]);

            if (parts.length == 1)
                return node;

            var value = parts[1].replace("))", "");
            node.value = Integer.parseInt(value);

            return node;
        }

        private void add(Node left, Node right) {
            if (root == null)
                root = left;

            left.children.add(right);
        }

        public void nextNode(String ln) {
            var parts = ln.split("---");

            if (parts.length != 2)
                return;
            
            var left = parseNode(parts[0]);
            var right = parseNode(parts[1]);
            add(left, right);
        }

        public void nextNull(String ln) {
            var parts = ln.split("~~~");

            if (parts.length != 2)
                return;
            
            var left = parseNode(parts[0]);
            add(left, null);
        }

        public BinNode toBinTree() {
            Queue<Node> que = new LinkedList<>();
            que.add(root);

            while (!que.isEmpty()) {
                var node = que.remove();
                node.target.setValue(node.value);

                for (var child : node.children) {
                    if (child != null)
                        que.add(child);
                }

                var count = node.children.size();

                if (count > 0) {
                    var child = node.children.get(0);
                    if (child != null)
                        node.target.setLeft(child.target);
                }

                if (count > 1) {
                    var child = node.children.get(1);
                    if (child != null)
                        node.target.setRight(child.target);
                }

                if (count > 2) {
                    System.out.println(String.format("WARN: node %s has %d children", node.name, count));
                }
            }

            return root.target;
        }
    }

    private static BinNode parseLines(String[] lines, boolean log) {
        if (lines.length == 0)
            return null;

        var parser = new Parser();

        for (var ln : lines) {
            if (ln.contains("---")) {
                parser.nextNode(ln);
            } else
            if (ln.contains("~~~")) {
                parser.nextNull(ln);
            }
        }

        return parser.toBinTree();
    }

    public static BinNode buildFromMermaid(String graphName, String filePath, boolean... logFlags) {
        var log = logFlags.length > 0 && logFlags[0];
        var lines = readAllLinesFrom(filePath, log);
        var graph = getGraphLines(graphName, lines, log);
        var tree = parseLines(graph, log);

        return tree;
    }
}
