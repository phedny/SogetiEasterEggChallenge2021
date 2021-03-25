package nl.sogeti.com;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Terminal implements Closeable {
    private static final File TTY = new File("/dev/tty");

    private final String sttySettings;
    private final Thread shutdownHook = new Thread(this::restore);

    public Terminal() throws IOException, InterruptedException {
        sttySettings = stty("-g");
        Runtime.getRuntime().addShutdownHook(shutdownHook);

        stty("-echo");
        stty("-icanon");
        stty("min", "1");

//        System.out.print("\u001B[?1049h");
//        System.out.flush();
    }

    public static boolean isTerminal() {
        try {
            new FileInputStream(TTY);
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    @Override
    public void close() {
        Runtime.getRuntime().removeShutdownHook(shutdownHook);
        restore();
    }

    private void restore() {
        try {
            stty(sttySettings);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private String stty(String... args) throws IOException, InterruptedException {
        final Process process = new ProcessBuilder(Stream.concat(Stream.of("/bin/stty"), Stream.of(args)).collect(Collectors.toList()))
                .redirectInput(ProcessBuilder.Redirect.from(TTY))
                .start();
        if (process.waitFor() != 0) {
            throw new RuntimeException("stty finished with non-zero exit code");
        }

        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            final List<String> lines = new ArrayList<>();
            while (true) {
                final String line = reader.readLine();
                if (line == null) {
                    return String.join("\n", lines);
                }

                lines.add(line);
            }
        }
    }
}
