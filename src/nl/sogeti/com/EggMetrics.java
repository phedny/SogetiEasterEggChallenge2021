package nl.sogeti.com;

import java.awt.image.BufferedImage;

public class EggMetrics {
    private final int horizontalRadius;
    private final int verticalRadius;
    private int centerXPoint;
    private int centerYPoint;
    private final BufferedImage image;
    private final String backgroundColor;
    private int frameHeight;
    private int frameWidth;

    public EggMetrics(final int horizontalRadius, final int verticalRadius, final int centerXPoint, final int centerYPoint, final BufferedImage image, final String backgroundColor) {
        this.horizontalRadius = horizontalRadius;
        this.verticalRadius = verticalRadius;
        modifyCenterXPoint(horizontalRadius, centerXPoint);
        modifyCenterYPoint(verticalRadius, centerYPoint);
        this.image = image;
        this.backgroundColor = backgroundColor;
        calculateFrameHeight(verticalRadius, this.centerYPoint);
        calculateFrameWidth(horizontalRadius, this.centerXPoint);
    }

    public int getHorizontalRadius() {
        return horizontalRadius;
    }

    public int getVerticalRadius() {
        return verticalRadius;
    }

    public int getCenterXPoint() {
        return centerXPoint;
    }

    public int getCenterYPoint() {
        return centerYPoint;
    }

    public BufferedImage getImage() {
        return image;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    private void modifyCenterXPoint(final int horizontalRadius, final int centerXPoint) {
        this.centerXPoint = Math.max(horizontalRadius, centerXPoint);
    }
    private void modifyCenterYPoint(final int verticalRadius, final int centerYPoint) {
        this.centerYPoint = Math.max(verticalRadius, centerYPoint);
    }

    private void calculateFrameWidth(final int horizontalRadius, final int centerXPoint) {
        this.frameWidth = (2 * horizontalRadius) + (centerXPoint - horizontalRadius) * 2;
    }

    private void calculateFrameHeight(final int verticalRadius, final int centerYPoint) {
        this.frameHeight = (2 * verticalRadius) + (centerYPoint - verticalRadius) * 2;
    }
}
