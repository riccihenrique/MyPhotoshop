
package myphotoshop.transformacoes;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class Basicas 
{
    public static Image tonsCinza(Image img)
    {
        BufferedImage bimg = SwingFXUtils.fromFXImage(img, null);
        
        int pixel[] = {0, 0, 0, 0};
        WritableRaster raster = bimg.getRaster();
        int cinza;
        for (int i = 0; i < img.getHeight(); i++)
            for (int j = 0; j < img.getWidth(); j++) 
            {
                raster.getPixel(j, i, pixel);
                cinza = (int) (0.21 * pixel[0] + 0.72 * pixel[1] + 0.07 *pixel[2]);
                pixel[0] = cinza;
                pixel[1] = cinza;
                pixel[2] = cinza;
                raster.setPixel(j, i, pixel);
            }
        return SwingFXUtils.toFXImage(bimg, null);
    }
    
    public static Image caneta(Image image, int x, int y, Color c)
    {
        BufferedImage bimg = SwingFXUtils.fromFXImage(image, null);
        WritableRaster raster = bimg.getRaster(); 
        int pixel[] = {(int)(c.getRed() * 255), (int)(c.getGreen() * 255),(int)(c.getBlue() * 255), 255};
        
        System.out.println(pixel[0] + ", " + pixel[1] + ", " + pixel[2] );
        
        raster.setPixel(x, y, pixel);        
        raster.setPixel(x + 1, y, pixel);
        raster.setPixel(x + 1, y + 1, pixel);         
        raster.setPixel(x, y + 1, pixel);
        raster.setPixel(x - 1, y, pixel);
        raster.setPixel(x - 1, y - 1, pixel);         
        raster.setPixel(x, y - 1, pixel);
        
        return SwingFXUtils.toFXImage(bimg, null);
    }
    
    public static Image EspelhaHorizontal(Image img)
    {
        BufferedImage bimg = SwingFXUtils.fromFXImage(img, null), bnimg = new BufferedImage((int) img.getWidth(),(int) img.getHeight(), BufferedImage.TYPE_INT_RGB);
        
        int pixel[] = {0, 0, 0, 0}, pixel1[] = {0, 0, 0, 0};
        WritableRaster raster = bimg.getRaster();
        WritableRaster nraster = bnimg.getRaster();
        for(int i = 0; i < img.getWidth()/2; i++)
            for(int j = 0; j < img.getHeight(); j++)
            {
                raster.getPixel(i, j, pixel);
                raster.getPixel((int) img.getWidth() - i - 1, j, pixel1);
                nraster.setPixel(i, j, pixel1);
                nraster.setPixel((int) img.getWidth() - i - 1, j, pixel);
            }        
        
        return SwingFXUtils.toFXImage(bnimg, null);
    }
    
    public static Image EspelhaVertical(Image img)
    {
        BufferedImage bimg = SwingFXUtils.fromFXImage(img, null), bnimg = new BufferedImage((int) img.getWidth(),(int) img.getHeight(), BufferedImage.TYPE_INT_RGB);
        
        int pixel[] = {0, 0, 0, 0}, pixel1[] = {0, 0, 0, 0};
        WritableRaster raster = bimg.getRaster();
        WritableRaster nraster = bnimg.getRaster();
        for(int i = 0; i < img.getWidth(); i++)
            for(int j = 0; j < img.getHeight()/2; j++)
            {
                raster.getPixel(i, j, pixel);
                raster.getPixel(i, (int) img.getHeight() - j - 1, pixel1);
                nraster.setPixel(i, j, pixel1);
                nraster.setPixel(i, (int) img.getHeight() - j - 1, pixel);
            }        
        
        return SwingFXUtils.toFXImage(bnimg, null);
    }
}
