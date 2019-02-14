package pers.lbw.seckill.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.springframework.stereotype.Component;
 
/**
 * 生成验证码图片的工具类
 */
@Component
public class VerifyCodeImageUtil {
 
    public static final String VERIFY_CODES = "1234567890aAaBbCcDdEeFfGgHhJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz";
    private static final Random random = new Random();
    private static final char[] ops=new char[] {'+','-'};
    private static final ScriptEngineManager sem=new ScriptEngineManager();
    private static final ScriptEngine engin=sem.getEngineByName("JavaScript");
 
    /**
     * 生成验证码
     *
     * @param verifySize
     *            验证码长度
     * @return
     */
    public String genVerifyCode(int verifySize) {
        return genVerifyCode(verifySize, VERIFY_CODES);
    }
 
    /**
     * 使用指定源生成验证码
     *
     * @param verifySize
     *            验证码长度
     * @param sources
     *            验证码字符源
     * @return
     */
    public String genVerifyCode(int verifySize, String sources) {
        if (sources == null || sources.length() == 0) {
            sources = VERIFY_CODES;
        }
        int codesLen = sources.length();
        Random rand = new Random(System.currentTimeMillis());
        StringBuilder verifyCode = new StringBuilder(verifySize);
        for (int i = 0; i < verifySize; i++) {
            verifyCode.append(sources.charAt(rand.nextInt(codesLen - 1)));
        }
        return verifyCode.toString();
    }
 
    /**
     * 绘制图片,输出指定验证码图片的Base64字符串
     *
     * @param w
     *            图片宽度
     * @param h
     *            图片高度
     * @param code
     *            验证码
     * @throws IOException
     */
    public String getImageBase64(int w, int h, String code) throws IOException{
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int verifySize = code.length();
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Random rand = new Random();
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color[] colors = new Color[5];
        Color[] colorSpaces = new Color[] { Color.WHITE, Color.CYAN, Color.GRAY, Color.LIGHT_GRAY, Color.MAGENTA,
                Color.ORANGE, Color.PINK, Color.YELLOW };
        float[] fractions = new float[colors.length];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = colorSpaces[rand.nextInt(colorSpaces.length)];
            fractions[i] = rand.nextFloat();
        }
        Arrays.sort(fractions);
 
        g2.setColor(Color.GRAY);// 设置边框色
        g2.fillRect(0, 0, w, h);
 
        Color c = getRandColor(200, 250);
        g2.setColor(c);// 设置背景色
        g2.fillRect(0, 2, w, h - 4);
 
        // 绘制干扰线
        Random random = new Random();
        g2.setColor(getRandColor(160, 200));// 设置线条的颜色
        for (int i = 0; i < 20; i++) {
            int x = random.nextInt(w - 1);
            int y = random.nextInt(h - 1);
            int xl = random.nextInt(6) + 1;
            int yl = random.nextInt(12) + 1;
            g2.drawLine(x, y, x + xl + 40, y + yl + 20);
        }
 
        // 添加噪点
        float yawpRate = 0.05f;// 噪声率
        int area = (int) (yawpRate * w * h);
        for (int i = 0; i < area; i++) {
            int x = random.nextInt(w);
            int y = random.nextInt(h);
            int rgb = getRandomIntColor();
            image.setRGB(x, y, rgb);
        }
 
        shear(g2, w, h, c);// 使图片扭曲
 
        g2.setColor(getRandColor(100, 160));
        int fontSize = h - 4;
        Font font = new Font("Algerian", Font.ITALIC, fontSize);
        g2.setFont(font);
        char[] chars = code.toCharArray();
        for (int i = 0; i < verifySize; i++) {
            AffineTransform affine = new AffineTransform();
            affine.setToRotation(Math.PI / 4 * rand.nextDouble() * (rand.nextBoolean() ? 1 : -1),
                    (w / verifySize) * i + fontSize / 2, h / 2);
            g2.setTransform(affine);
            g2.drawChars(chars, i, 1, ((w - 10) / verifySize) * i + 5, h / 2 + fontSize / 2 - 10);
        }
 
        g2.dispose();
        ImageIO.write(image, "jpg", os);
        os.close();
        byte[] bsImageStr = os.toByteArray();
        //data:image/jpeg;base64,是固定前缀
        String res = "data:image/jpeg;base64,"+Base64.getEncoder().encodeToString(bsImageStr);
        return res;
    }
 
    private Color getRandColor(int fc, int bc) {
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }
 
    private int getRandomIntColor() {
        int[] rgb = getRandomRgb();
        int color = 0;
        for (int c : rgb) {
            color = color << 8;
            color = color | c;
        }
        return color;
    }
 
    private int[] getRandomRgb() {
        int[] rgb = new int[3];
        for (int i = 0; i < 3; i++) {
            rgb[i] = random.nextInt(255);
        }
        return rgb;
    }
 
    private void shear(Graphics g, int w1, int h1, Color color) {
        shearX(g, w1, h1, color);
        shearY(g, w1, h1, color);
    }
 
    private void shearX(Graphics g, int w1, int h1, Color color) {
        int period = random.nextInt(2);
 
        boolean borderGap = true;
        int frames = 1;
        int phase = random.nextInt(2);
 
        for (int i = 0; i < h1; i++) {
            double d = (double) (period >> 1)
                    * Math.sin((double) i / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
            g.copyArea(0, i, w1, 1, (int) d, 0);
            if (borderGap) {
                g.setColor(color);
                g.drawLine((int) d, i, 0, i);
                g.drawLine((int) d + w1, i, w1, i);
            }
        }
 
    }
 
    private void shearY(Graphics g, int w1, int h1, Color color) {
        int period = random.nextInt(40) + 10; // 50;
 
        boolean borderGap = true;
        int frames = 20;
        int phase = 7;
        for (int i = 0; i < w1; i++) {
            double d = (double) (period >> 1)
                    * Math.sin((double) i / (double) period + (6.2831853071795862D * (double) phase) / (double) frames);
            g.copyArea(i, 0, 1, h1, 0, (int) d);
            if (borderGap) {
                g.setColor(color);
                g.drawLine(i, (int) d, i, 0);
                g.drawLine(i, (int) d + h1, i, h1);
            }
 
        }
    }
 
    public static void main(String[] args) throws IOException {
        VerifyCodeImageUtil vciu = new VerifyCodeImageUtil();
        int w = 200;
        int h = 80;
        String verifyCode = vciu.genVerifyCode(4);
        String imageBase64 = vciu.getImageBase64(w, h, verifyCode);
        System.err.println(imageBase64);
        
        String me = vciu.genMEVerifyCode(10);
        
        System.err.println(me+"="+vciu.calc(me));
    }

    /**
     * 生成i以内加减的数学表达式字符串(乘除在页面显示模糊，暂时不做)
     * @param i 表达式中数字的范围,即(0,i)
     * @return 返回数学表达式
     */
	public String genMEVerifyCode(int i) {
		int num1 = random.nextInt(i);
		int num2 = random.nextInt(i);
		int num3 = random.nextInt(i);
		char op1=ops[random.nextInt(ops.length)];
		char op2=ops[random.nextInt(ops.length)];
		String exp=""+num1+op1+num2+op2+num3;
		return exp;
	}
	
	/**
	 * 计算数学表达式的结果并返回(用ScriptEngin计算)
	 * @param exp 数学表达式
	 * @return 计算结果，出现异常返回null
	 */
	public Integer calc(String exp) {
		try {
			Integer res = (Integer) engin.eval(exp);
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
