import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

class DibFile {
    public static class Section {
        public String language;
        public String text;

        public Section(String language, String text) {
            this.language = language;
            this.text = text;
        }

        public String toString() {
            return "[" + language + "](" + text.length() + " characters)";
        }
    }

    private static void print(String fmt, Object... args) {
        System.out.println(String.format(fmt, args));
    }

    private static String readAllText(String filePath, boolean log) {
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

       return text;
    }

    public static Section[] getAllSections(String language, String path, boolean log) {
        var text = readAllText(path, log);

        var sectionStart = "(?m)(^#!\\w+$)";
        var chunks = text.split(sectionStart);
        var parser = Pattern.compile(sectionStart, Pattern.MULTILINE);
        var all = new ArrayList<Section>();
        var chunkNo = 1;            // first chunk is empty (split artifact)

        for (var matcher = parser.matcher(text); matcher.find(); chunkNo++) {
            if (!matcher.group().equals(language))
                continue;

            var chunk = chunkNo < chunks.length ? chunks[chunkNo] : "";         // get last chunk (or empty if last section is empty)

            all.add(new Section(language, chunk));
        }

        return all.toArray(Section[]::new);
    }
}
