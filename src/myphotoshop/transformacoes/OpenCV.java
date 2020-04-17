package myphotoshop.transformacoes;

import javafx.scene.image.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.LinkedList;
import java.util.List;
import javafx.embed.swing.SwingFXUtils;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;

import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.KeyPoint;
import org.opencv.imgproc.Imgproc;

public class OpenCV
{
    public static Image suavizacao_gausiana(Image image, int value)
    {
        Mat img = BufferedImageToMat(SwingFXUtils.fromFXImage(image, null));
        Mat img_gaus = Mat.zeros(img.rows(), img.cols(), img.type());

        //borramento Gaussiano
        value = value - (value % 2) + 1;
        Imgproc.GaussianBlur(img, img_gaus, new Size(value, value), -1, -1, Imgproc.BORDER_DEFAULT);

        return SwingFXUtils.toFXImage(matToBufferedImage(img_gaus), null);
    }
    
    public static Image Brilho(Image imagem, double contraste, double brilho)
    {
        Mat image = BufferedImageToMat(SwingFXUtils.fromFXImage(imagem, null));
        Mat newImage = Mat.zeros(image.rows(), image.cols(), image.type());
        
        byte[] imageData = new byte[(int) (image.total()*image.channels())];
        image.get(0, 0, imageData);
        
        image.convertTo(newImage, -1, contraste, brilho);
        
        return SwingFXUtils.toFXImage(matToBufferedImage(newImage), null);
    }

    public static BufferedImage matToBufferedImage(Mat bgr)
    {
        int width = bgr.width();
        int height = bgr.height();
        BufferedImage image;
        WritableRaster raster;
        if (bgr.channels() == 1)
        {
            image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
            raster = image.getRaster();
            byte[] px = new byte[1];
            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    bgr.get(y, x, px);
                    raster.setSample(x, y, 0, px[0]);
                }
            }
        }
        else
        {
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            raster = image.getRaster();
            byte[] px = new byte[3];
            int[] rgb = new int[4];
            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                        bgr.get(y, x, px);
                        rgb[0] = px[2];
                        rgb[1] = px[1];
                        rgb[2] = px[0];
                        raster.setPixel(x, y, rgb);
                }
            }
        }
        return image;
    }
    
    public static Mat BufferedImageToMat(BufferedImage bimage)
    {
        int width = bimage.getWidth();
        int height = bimage.getHeight();
        int type = CvType.CV_8UC3;
        Mat newMat = new Mat(height, width, type);
        WritableRaster raster;
        raster = bimage.getRaster();
        byte[] px = new byte[3];
        int[] rgb = new int[4];
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                raster.getPixel(x, y, rgb);
                px[2] = (byte)rgb[0];    px[1] = (byte)rgb[1];    px[0] = (byte)rgb[2];     
                newMat.put(y, x, px);
            }
        }
        return newMat;
    }
    
    public static Image escala(Image image, double angle, double scale)
    {
        Mat img = BufferedImageToMat(SwingFXUtils.fromFXImage(image, null));
        Mat warp_img = Mat.zeros(img.rows(), img.cols(), img.type());
        
        Point center = new Point(img.cols() / 2, img.rows() / 2);
        //Obtem a matriz de rotacao
        Mat rot_mat = Imgproc.getRotationMatrix2D(center, angle, scale / 5);
        //Rotaciona a image warp
        Imgproc.warpAffine(img, warp_img, rot_mat, warp_img.size() );
        
        return SwingFXUtils.toFXImage(matToBufferedImage(warp_img), null);
    }
    
    public static Image borda_prewitt(Image image)
    {
        Mat img = BufferedImageToMat(SwingFXUtils.fromFXImage(image, null));
        Mat img_gray = Mat.zeros(img.rows(), img.cols(), img.type());
        Mat grad = Mat.zeros(img.rows(), img.cols(), img.type());

        //converte para cinza
        Imgproc.cvtColor(img, img_gray, Imgproc.COLOR_BGR2GRAY);
        //img_gray = img;
        //borramento Gaussiano
        Imgproc.GaussianBlur(img, img, new Size(5,5), 1, 1, Imgproc.BORDER_DEFAULT);

        //gera grad_x e grad_y
        Mat grad_x = Mat.zeros(img.rows(), img.cols(), img.type());
        Mat grad_y = Mat.zeros(img.rows(), img.cols(), img.type());
        Mat abs_grad_x = Mat.zeros(img.rows(), img.cols(), img.type());
        Mat abs_grad_y = Mat.zeros(img.rows(), img.cols(), img.type());
        
        int prewitty[][] = { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };
        int prewittx[][] = { { -1, -2, -1 }, { 0, 0, 0 }, { 1, 2, 1 } };
        Mat Mprewittx = new Mat(new Size(3,3), CvType.CV_32FC1);
        Mat Mprewitty = new Mat(new Size(3,3), CvType.CV_32FC1);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
               Mprewittx.put(i,j,prewittx[i][j]); 
               Mprewitty.put(i,j,prewitty[i][j]); 
            }
        }

        Imgproc.filter2D(img_gray, grad_x, -1, Mprewittx); //gradiente X
        Core.convertScaleAbs(grad_x, abs_grad_x);
        
        Imgproc.filter2D(img_gray, grad_y, -1, Mprewitty); //gradiente Y
        Core.convertScaleAbs(grad_y, abs_grad_y);

        Core.addWeighted(abs_grad_x, 0.5, abs_grad_y, 0.5, 0, grad);   //gradiente total
        
        return SwingFXUtils.toFXImage(matToBufferedImage(grad), null);
    }
    
    public static Image panorama(Image img1, Image img2)
    {
        Mat im1 = BufferedImageToMat(SwingFXUtils.fromFXImage(img1, null));
        Mat im2 = BufferedImageToMat(SwingFXUtils.fromFXImage(img2, null));
	
        MatOfKeyPoint keypointsCena = new MatOfKeyPoint();
        MatOfKeyPoint keypointsObj = new MatOfKeyPoint();
        Mat descriptorsCena = new Mat();
        Mat descriptorsObj = new Mat();
        
        //calculando keypoints SIFT
        FeatureDetector detector = FeatureDetector.create(FeatureDetector.SIFT);
        detector.detect(im1, keypointsCena);
        detector.detect(im2, keypointsObj);
		
        //calculando descritores SIFT
        DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.SIFT); 
        extractor.compute(im1, keypointsCena, descriptorsCena);
        extractor.compute(im2, keypointsObj, descriptorsObj);
        DescriptorMatcher matcher = 	DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_L1);
        MatOfDMatch matches = new MatOfDMatch();
        matcher.match(descriptorsObj, descriptorsCena, matches);
        
        //extraindo os matches 
        List<DMatch> matchesList = matches.toList();
        LinkedList<DMatch> good_matches = new LinkedList<DMatch>();
        MatOfDMatch gm = new MatOfDMatch();
        for (int i = 0; i < matchesList.size(); i++) 
            good_matches.addLast(matchesList.get(i));
        gm.fromList(good_matches);        
        
        //localiza o objeto
        //pontos do obj e da cena
        LinkedList<Point> objList = new LinkedList<Point>();
        LinkedList<Point> sceneList = new LinkedList<Point>();
        List<KeyPoint> keypoints_objectList = keypointsObj.toList();
        List<KeyPoint> keypoints_sceneList = keypointsCena.toList();

        for (int i = 0; i < good_matches.size(); i++) 
        {
            objList.addLast(keypoints_objectList.get(good_matches.get(i).queryIdx).pt);
            sceneList.addLast(keypoints_sceneList.get(good_matches.get(i).trainIdx).pt);
        }
        
        MatOfPoint2f obj = new MatOfPoint2f();
        obj.fromList(objList);

        MatOfPoint2f scene = new MatOfPoint2f();
        scene.fromList(sceneList);
        
        //calcula RANSAC
        Mat mask = new Mat();
        Mat H = Calib3d.findHomography(obj, scene, org.opencv.calib3d.Calib3d.RANSAC, 3.0, mask);         
        Mat imgPano = new Mat();
        int transx = (int)H.get(0, 2)[0];
        int altura = im2.rows();
        int largura = im2.cols() + transx; 
        Imgproc.warpPerspective(im2,imgPano,H,new Size(largura, altura),Imgproc.INTER_LINEAR,Imgproc.BORDER_CONSTANT,Scalar.all(-1));
        
        double vPano[];
        double vCena[];
        double vDest[] = new double[3];
        int tam = transx/4;
        double peso;
        double passo = 1.0/tam;
        for (int i = 0; i < im1.rows(); i++)
        {   peso = 1.0;
            for (int j = 0; j < im1.cols(); j++)
            {
                if (j+tam < im1.cols())
                    imgPano.put(i, j, im1.get(i,j));
                else
                {   vCena = im1.get(i,j);    vPano = imgPano.get(i,j);
                    vDest[0] = vCena[0]*peso + vPano[0]*(1.0-peso);
                    vDest[1] = vCena[1]*peso + vPano[1]*(1.0-peso);
                    vDest[2] = vCena[2]*peso + vPano[2]*(1.0-peso);
                    imgPano.put(i, j, vDest);       peso=peso-passo;
                }
            }            
        }
        
        return SwingFXUtils.toFXImage(matToBufferedImage(imgPano), null);
    }
    
    public static Image panorama(Image img1, Image img2, Image img3)
    {
        Mat im1 = BufferedImageToMat(SwingFXUtils.fromFXImage(img1, null));
        Mat im2;        
	int c = 1;
        while(c >= 0)
        {
            if(c == 1)
                im2 = BufferedImageToMat(SwingFXUtils.fromFXImage(img2, null));
            else
                im2 = BufferedImageToMat(SwingFXUtils.fromFXImage(img3, null));
            
            MatOfKeyPoint keypointsCena = new MatOfKeyPoint();
            MatOfKeyPoint keypointsObj = new MatOfKeyPoint();
            Mat descriptorsCena = new Mat();
            Mat descriptorsObj = new Mat();

            //calculando keypoints SIFT
            FeatureDetector detector = FeatureDetector.create(FeatureDetector.SIFT);
            detector.detect(im1, keypointsCena);
            detector.detect(im2, keypointsObj);

            //calculando descritores SIFT
            DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.SIFT); 
            extractor.compute(im1, keypointsCena, descriptorsCena);
            extractor.compute(im2, keypointsObj, descriptorsObj);
            DescriptorMatcher matcher = 	DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_L1);
            MatOfDMatch matches = new MatOfDMatch();
            matcher.match(descriptorsObj, descriptorsCena, matches);

            //extraindo os matches 
            List<DMatch> matchesList = matches.toList();
            LinkedList<DMatch> good_matches = new LinkedList<DMatch>();
            MatOfDMatch gm = new MatOfDMatch();
            for (int i = 0; i < matchesList.size(); i++) 
                good_matches.addLast(matchesList.get(i));
            gm.fromList(good_matches);        

            //localiza o objeto
            //pontos do obj e da cena
            LinkedList<Point> objList = new LinkedList<Point>();
            LinkedList<Point> sceneList = new LinkedList<Point>();
            List<KeyPoint> keypoints_objectList = keypointsObj.toList();
            List<KeyPoint> keypoints_sceneList = keypointsCena.toList();

            for (int i = 0; i < good_matches.size(); i++) 
            {
                objList.addLast(keypoints_objectList.get(good_matches.get(i).queryIdx).pt);
                sceneList.addLast(keypoints_sceneList.get(good_matches.get(i).trainIdx).pt);
            }

            MatOfPoint2f obj = new MatOfPoint2f();
            obj.fromList(objList);

            MatOfPoint2f scene = new MatOfPoint2f();
            scene.fromList(sceneList);

            //calcula RANSAC
            Mat mask = new Mat();
            Mat H = Calib3d.findHomography(obj, scene, org.opencv.calib3d.Calib3d.RANSAC, 3.0, mask);         
            Mat imgPano = new Mat();
            int transx = (int)H.get(0, 2)[0];
            int altura = im2.rows();
            int largura = im2.cols() + transx; 
            Imgproc.warpPerspective(im2,imgPano,H,new Size(largura, altura),Imgproc.INTER_LINEAR,Imgproc.BORDER_CONSTANT,Scalar.all(-1));

            double vPano[];
            double vCena[];
            double vDest[] = new double[3];
            int tam = transx/4;
            double peso;
            double passo = 1.0/tam;
            for (int i = 0; i < im1.rows(); i++)
            {   peso = 1.0;
                for (int j = 0; j < im1.cols(); j++)
                {
                    if (j+tam < im1.cols())
                        imgPano.put(i, j, im1.get(i,j));
                    else
                    {   vCena = im1.get(i,j);    vPano = imgPano.get(i,j);
                        vDest[0] = vCena[0]*peso + vPano[0]*(1.0-peso);
                        vDest[1] = vCena[1]*peso + vPano[1]*(1.0-peso);
                        vDest[2] = vCena[2]*peso + vPano[2]*(1.0-peso);
                        imgPano.put(i, j, vDest);       peso=peso-passo;
                    }
                }            
            }
            im1 = imgPano;
            c--;
        }        
        return SwingFXUtils.toFXImage(matToBufferedImage(im1), null);
    }
}
