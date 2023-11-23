package mjw.java.lang;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ProcessTest
{
    @Test
    void startNotepad() throws IOException, InterruptedException
    {
        var builder = new ProcessBuilder();
        builder.command("notepad.exe");

        Process process = builder.start();
        var ret = process.waitFor();
        System.out.printf("Program exited with code: %d", ret);
    }

    @Test
    void seeOutput() throws IOException
    {
        var processBuilder = new ProcessBuilder();

        processBuilder.command("cmd.exe", "/c", "ping -n 3 google.com");

        var process = processBuilder.start();

        try (var reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {

            String line;

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

    @Test
    void startJar() throws IOException
    {
        List<String> commands = new ArrayList<>();
        commands.add("java");
        commands.add("-jar");
        commands.add("D:\\Dinosaur-1.2.0.free.jar");
        var builder = new ProcessBuilder();
        builder.command(commands);

        Process process = builder.start();

        try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

    @Test
    void redirectOutput() throws IOException
    {
        var homeDir = System.getProperty("user.home");

        var processBuilder = new ProcessBuilder();

        processBuilder.command("cmd.exe", "/c", "date /t");

        var fileName = new File(String.format("%s/Documents/output.txt", homeDir));

        processBuilder.redirectOutput(fileName);

        var process = processBuilder.start();

        try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }

    @Test
    void redirectIO() throws IOException
    {
        var processBuilder = new ProcessBuilder();

        processBuilder.command("cat")
                .redirectInput(new File("src/resources", "input.txt"))
                .redirectOutput(new File("src/resources/", "output.txt"))
                .start();
    }

    @Test
    void inheritIO() throws IOException, InterruptedException
    {
        var processBuilder = new ProcessBuilder();

        processBuilder.command("cmd.exe", "/c", "dir");

        var process = processBuilder.inheritIO().start();

        int exitCode = process.waitFor();
        System.out.printf("Program ended with exitCode %d", exitCode);
    }

    @Test
    void environment()
    {
        var pb = new ProcessBuilder();
        var env = pb.environment();

        env.forEach((s, s2) -> {
            System.out.printf("%s %s %n", s, s2);
        });

        System.out.printf("%s %n", env.get("PATH"));
    }
}
