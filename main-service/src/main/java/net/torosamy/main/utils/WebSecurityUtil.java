package net.torosamy.main.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.jhlabs.image.RippleFilter;
import com.jhlabs.image.TransformFilter;
import com.jhlabs.image.WaterFilter;

import net.torosamy.main.constant.SecurityConstant;
import net.torosamy.main.domain.po.User;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

public class WebSecurityUtil {
    private static final int width = 132;
    private static final int height = 48;
    private static final int length = 5;
    private static final char[] seed = "abcde2345678gfynmnpwx".toCharArray();

    public static String genLoginToken(User user) {
        long expirationTimeInMillis = SecurityConstant.TOKEN_UNIT.toMillis(SecurityConstant.TOKEN_EXPIRE);
        Date expiration = new Date(System.currentTimeMillis() + expirationTimeInMillis);

        return JWT.create()
                .withClaim("username", user.getUsername())
                .withExpiresAt(expiration)
                .withIssuedAt(new Date())
                .sign(Algorithm.HMAC256(SecurityConstant.TOKEN_KEY));
    }


    public static String getText() {
        SecureRandom rand = new SecureRandom();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(seed[rand.nextInt(seed.length)]);
        }
        return text.toString();
    }

    public static BufferedImage createImage(String text) {
        BufferedImage bi = renderWord(text);
        bi = getDistortedImage(bi);
        bi = addBackground(bi);
        return bi;
    }

    // 添加背景色
    public static BufferedImage addBackground(BufferedImage image) {
        int width = image.getWidth(), height = image.getHeight();
        BufferedImage bgImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bgImage.createGraphics();
        g2d.setPaint(new GradientPaint(0, 0, Color.LIGHT_GRAY, width, height, Color.WHITE));
        g2d.fillRect(0, 0, width, height);
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return bgImage;
    }
    public static BufferedImage renderWord(String word) {
        int fontSize = 40;
        Font[] fonts = new Font[]{
                new Font("Arial", Font.BOLD, fontSize), new Font("Courier", Font.BOLD, fontSize)
        };
        Color color = Color.BLACK;
        int charSpace = 2;
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2D = image.createGraphics();
        g2D.setColor(color);

        RenderingHints hints = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        hints.add(new RenderingHints(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY));
        g2D.setRenderingHints(hints);

        FontRenderContext frc = g2D.getFontRenderContext();
        Random random = new Random();

        int startPosY = (height - fontSize) / 5 + fontSize;

        char[] wordChars = word.toCharArray();
        Font[] chosenFonts = new Font[wordChars.length];
        int[] charWidths = new int[wordChars.length];
        int widthNeeded = 0;
        for (int i = 0; i < wordChars.length; i++) {
            chosenFonts[i] = fonts[random.nextInt(fonts.length)];

            char[] charToDraw = new char[]{
                    wordChars[i]
            };
            GlyphVector gv = chosenFonts[i].createGlyphVector(frc, charToDraw);
            charWidths[i] = (int) gv.getVisualBounds().getWidth();
            if (i > 0) {
                widthNeeded = widthNeeded + 2;
            }
            widthNeeded = widthNeeded + charWidths[i];
        }

        int startPosX = (width - widthNeeded) / 2;
        for (int i = 0; i < wordChars.length; i++) {
            g2D.setFont(chosenFonts[i]);
            char[] charToDraw = new char[]{
                    wordChars[i]
            };
            g2D.drawChars(charToDraw, 0, charToDraw.length, startPosX, startPosY);
            startPosX = startPosX + charWidths[i] + charSpace;
        }

        return image;
    }

    public static BufferedImage getDistortedImage(BufferedImage baseImage) {

        BufferedImage distortedImage = new BufferedImage(baseImage.getWidth(),
                baseImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics = (Graphics2D) distortedImage.getGraphics();

        RippleFilter rippleFilter = new RippleFilter();
        rippleFilter.setWaveType(RippleFilter.SINE);
        rippleFilter.setXAmplitude(2.6f);
        rippleFilter.setYAmplitude(1.7f);
        rippleFilter.setXWavelength(15);
        rippleFilter.setYWavelength(5);
        rippleFilter.setEdgeAction(TransformFilter.NEAREST_NEIGHBOUR);

        WaterFilter waterFilter = new WaterFilter();
        waterFilter.setAmplitude(1.5f);
        waterFilter.setPhase(10);
        waterFilter.setWavelength(2);

        BufferedImage effectImage = waterFilter.filter(baseImage, null);
        effectImage = rippleFilter.filter(effectImage, null);

        graphics.drawImage(effectImage, 0, 0, null, null);

        graphics.dispose();

        makeNoise(distortedImage, .1f, .1f, .25f, .25f);
        makeNoise(distortedImage, .1f, .25f, .5f, .9f);
        return distortedImage;
    }

    public static void makeNoise(BufferedImage image, float factorOne,
                                 float factorTwo, float factorThree, float factorFour) {
        Color color = Color.BLACK;

        // image size
        int width = image.getWidth();
        int height = image.getHeight();

        // the points where the line changes the stroke and direction
        Point2D[] pts = null;
        Random rand = new Random();

        // the curve from where the points are taken
        CubicCurve2D cc = new CubicCurve2D.Float(width * factorOne, height
                * rand.nextFloat(), width * factorTwo, height
                * rand.nextFloat(), width * factorThree, height
                * rand.nextFloat(), width * factorFour, height
                * rand.nextFloat());

        // creates an iterator to define the boundary of the flattened curve
        PathIterator pi = cc.getPathIterator(null, 2);
        Point2D tmp[] = new Point2D[200];
        int i = 0;

        // while pi is iterating the curve, adds points to tmp array
        while (!pi.isDone()) {
            float[] coords = new float[6];
            switch (pi.currentSegment(coords)) {
                case PathIterator.SEG_MOVETO:
                case PathIterator.SEG_LINETO:
                    tmp[i] = new Point2D.Float(coords[0], coords[1]);
            }
            i++;
            pi.next();
        }

        pts = new Point2D[i];
        System.arraycopy(tmp, 0, pts, 0, i);

        Graphics2D graph = (Graphics2D) image.getGraphics();
        graph.setRenderingHints(new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON));

        graph.setColor(color);

        // for the maximum 3 point change the stroke and direction
        for (i = 0; i < pts.length - 1; i++) {
            if (i < 3)
                graph.setStroke(new BasicStroke(0.9f * (4 - i)));
            graph.drawLine((int) pts[i].getX(), (int) pts[i].getY(),
                    (int) pts[i + 1].getX(), (int) pts[i + 1].getY());
        }

        graph.dispose();
    }
}
