package nl.sogeti.com;

import nl.sogeti.logo.SogetiLogoDrawer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EasterEggRunner {
    private static final Pattern POSITION_PATTERN = Pattern.compile("\u001B\\[(\\d+);(\\d+)R");

    private int terminalWidth, terminalHeight;
    private int screenWidth, screenHeight;
    private int x, y, dx, dy;

    public static void main(String[] args) throws IOException, AWTException, InterruptedException {
        if ("size".equals(System.getProperty("track")) && Terminal.isTerminal()) {
            new EasterEggRunner().runWithTerminal();
        } else {
            new EasterEggRunner().run();
        }
    }

    private void run() throws InterruptedException, AWTException {
        readScreenSize();
        setInitialPosition();

        terminalWidth = 102;
        terminalHeight = 70;

        while (true) {
            readScreenSize();
            updatePosition();
            clearTerminalBuffer();
            drawFrame();

            Thread.sleep(40);
        }
    }

    private void runWithTerminal() throws InterruptedException, IOException, AWTException {
        try (final Terminal terminal = new Terminal()) {
            readScreenSize();
            setInitialPosition();

            while (true) {
                readScreenSize();
                updatePosition();
                readTerminalSize();
                clearTerminalBuffer();
                drawFrame();

                Thread.sleep(40);
            }
        }
    }

    private void readScreenSize() {
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = screenSize.width;
        screenHeight = screenSize.height;
    }

    private void setInitialPosition() {
        x = screenWidth / 2;
        y = screenHeight / 2;
    }

    private void readTerminalSize() throws IOException {
        flushInput();
        System.out.print("\u001B[s\u001B[5000;5000H\u001B[6n\u001B[u");
        System.out.flush();
        final Matcher positionMatcher = POSITION_PATTERN.matcher(readUntil('R'));
        if (!positionMatcher.matches()) {
            throw new RuntimeException("No match!");
        }

        terminalHeight = Integer.parseInt(positionMatcher.group(1), 10);
        terminalWidth = Integer.parseInt(positionMatcher.group(2), 10);
    }

    private void updatePosition() {
        dx += 12 * Math.random() - 6;
        dy += 12 * Math.random() - 6;
        if (x < 0 && dx < 0) {
            dx = 2;
        }
        if (y < 0 && dy < 0) {
            dy = 2;
        }
        if (x > screenWidth - terminalWidth) {
            dx = -2;
        }
        if (y > screenHeight - terminalHeight) {
            dy = -2;
        }
        x += dx;
        y += dy;
    }

    private void clearTerminalBuffer() {
        System.out.print("\u001B[2J\u001B[1;1H");
    }

    private void drawFrame() throws AWTException {
        final BufferedImage image = new Robot().createScreenCapture(new Rectangle(x, y, terminalWidth / 2, terminalHeight));
        final int maximumHorizontalRadius = terminalWidth / 3 - 5;
        final int maximumVerticalRadius = (terminalHeight - 44) / 2;
        final EggMetrics eggMetrics;
        if (maximumHorizontalRadius * 22 / 30 > maximumVerticalRadius) {
            eggMetrics = new EggMetrics(maximumVerticalRadius * 30 / 22, maximumVerticalRadius, (terminalWidth - 1) / 2, (terminalHeight - 34) / 2, image, Colors.GREEN.getColor());
        } else {
            eggMetrics = new EggMetrics(maximumHorizontalRadius, maximumHorizontalRadius * 22 / 30, (terminalWidth - 1) / 2, (terminalHeight - 34) / 2, image, Colors.GREEN.getColor());
        }
        EasterEgg.drawEgg(eggMetrics);
        if (terminalWidth > 100) {
            new SogetiLogoDrawer().printSogetiLogo();
        }
        System.out.flush();
    }

    private static void flushInput() throws IOException {
        while (System.in.available() > 0) {
            final int read = System.in.read();
            if (read == -1 || read == 3) {
                System.exit(0);
            }
        }
    }

    private static String readUntil(int lastChar) throws IOException {
        final StringBuilder buffer = new StringBuilder();
        while (true) {
            final int read = System.in.read();
            if (read == -1 || read == 3) {
                System.exit(0);
            }

            buffer.append((char) read);
            if (read == lastChar) {
                return buffer.toString();
            }
        }
    }
}
