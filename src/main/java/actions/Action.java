package actions;

import utils.PictureUtil;

import java.awt.image.BufferedImage;

public class Action {

        BufferedImage image;
        int height;
        int width;
        boolean alphaChannel;
        int[] pixelsArray;
   public int[] getRGBInt(BufferedImage image){
        this.image = image;
        this.height = image.getHeight();
        this.width = image.getWidth();
        pixelsArray = new int[height*width];
       return pixelsArray = image.getRGB(0,0,width,height,pixelsArray,0,width);
    }

    public void changeImage(Operation operation) throws Exception {
        for(int i =0; i<pixelsArray.length;i++){
            float[] pixelFloat = PictureUtil.getRGBfromPixel(pixelsArray[i]);
            float[] newPixelFloat = operation.changeRGB(pixelFloat);
            pixelsArray[i] = PictureUtil.getIntFromRGB(newPixelFloat);}
            image.setRGB(0,0,width,height,pixelsArray,0,width);
        }
    }

