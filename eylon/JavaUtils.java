import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

class JavaUtils {
    public static String str(Object arg) {
        if (arg == null)
            return "null";
        if (arg instanceof int[])
            return Arrays.toString((int[]) arg);
        if (arg instanceof boolean[])
            return Arrays.toString((boolean[]) arg);
        if (arg instanceof String[])
            return Arrays.toString((String[]) arg);
        return arg.toString();
    }
    
    public static void print(Object... args) {
        for (var arg : args) {
            System.out.print(str(arg));
            System.out.print(' ');
        }
        System.out.println();
    }
    
    public static String input(String prompt) {
        var scanner = new Scanner(System.in);
    
        System.out.print(prompt);
        var answer = scanner.nextLine();
        System.out.println(answer);
        
        scanner.close();
        return answer;
    }    

    public static String os_cmd(String cmd) {
        try {
            var process = Runtime.getRuntime().exec(cmd, null, null);
        
            var reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()));

            var output = reader.readLine();

            reader.close();

            return output;
        } catch (IOException e) {
            print("FAILED: %s", e.getMessage());
            return "";
        }
    }

    public static String getCurrentBranch() {
        var branch = os_cmd("git branch --show-current");
        return branch.toLowerCase();
    }

    public static void store_all_changes_to_github(String page) {
        os_cmd("git add -A");
        os_cmd("git commit -m \"submit " + page + "\"");
        os_cmd("git push");
    }

    private static void autoCommit(String branch, String page) {
        if (Config.noAutoCommitIn.contains(branch)) {
            print("No auto commits for branch", branch, "(commit is skipped)");
            return;
        }

        store_all_changes_to_github(page);
    }

    public static String nowYyyyMmDdHhMmSs() {
        var tm = LocalDateTime.now();
        var pattern = DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss");
        return tm.format(pattern);
    }

    public static String post(String url, String body) {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            
        try {
            var response = client.send(request, BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            var errMsg = String.format("POST has failed: %s", e.getMessage());
            print(errMsg);
            return null;
        }
    }

    public static String submit(String page, String msg, boolean trace) {
        var url = Config.api;
        var docId = Config.docId;
        var branch = getCurrentBranch();
        var now = nowYyyyMmDdHhMmSs();

        autoCommit(branch, page);

        String template = """
            {   
                "now": "$NOW$",
                "docId": "$DOCID$",
                "sheet": "$SHEET$",
                "branch": "$BRANCH$",
                "lesson": "$LESSON$",
                "fname": "$FNAME$",
                "src": "",
                "log": "$LOG$",
                "tags": ""
            }
            """;
            
        var body = template
            .replace("$NOW$", now)
            .replace("$DOCID$", docId)
            .replace("$SHEET$", "Submit")
            .replace("$BRANCH$", branch)
            .replace("$LESSON$", page)
            .replace("$FNAME$", "ok")
            .replace("$LOG$", msg);

        var response = post(url, body);

        if (trace)
            print(response);

        return "'" + page + "' is submitted to Gosha. Great job! 👍";
    }

    public static String submit(String page, String msg) {
            return submit(page, msg, false);
    }
}
