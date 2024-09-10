package utils;

import actions.Action;
import actions.Filters;
import actions.Operation;
import commands.AppBotCommands;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;

public class PictureUtil {
    String path;
   public BufferedImage getPicture(String path) throws IOException {
        this.path = path;
        File file = new File(this.path);
        BufferedImage input = ImageIO.read(file);
        return input;
    }

    public void writePicture(BufferedImage picture) throws IOException {
File output = new File("received_image.jpeg");
ImageIO.write(picture, "jpeg",output);

    }

    public static float[] getRGBfromPixel(int pixel){
       Color color = new Color(pixel);
       return color.getRGBComponents(null);
    }

    public static int getIntFromRGB (float[] pixel) throws Exception {
       Color color = null;
       if(pixel.length == 3){
           color = new Color(pixel[0],pixel[1],pixel[2]);
       } else if(pixel.length==4){
           color = new Color (pixel[0],pixel[1],pixel[2], pixel[3]);
       }if(color!=null) {
            return color.getRGB();
        }
       throw new Exception("invalid color");

    }
    public static void saveImage(String url, String fileName) throws IOException {
        URL urlModel = new URL(url);
        InputStream inputStream = urlModel.openStream();
        OutputStream outputStream = new FileOutputStream(fileName);
        byte[] b = new byte[2048];
        int length;
        while((length = inputStream.read(b)) != -1){
            outputStream.write(b, 0, length);
        }
        inputStream.close();
        outputStream.close();
    }
    public static void processingImage(String path, Operation operation){
        PictureUtil pictureUtil = new PictureUtil();
        BufferedImage input = null;
        try {
            input = pictureUtil.getPicture(path);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        Action action = new Action();
        action.getRGBInt(input);
        Filters filters = new Filters();
        try {
            action.changeImage(operation);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        try {
            pictureUtil.writePicture(input);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static Operation getOperation(String operationName){
        Filters filters = new Filters();
        Method[] classMethods = filters.getClass().getDeclaredMethods();
        for (Method method : classMethods){
            if (method.isAnnotationPresent(AppBotCommands.class)){
                AppBotCommands command = method.getAnnotation(AppBotCommands.class);
                  if(command.name().equals(operationName)){
                  return  (f) ->(float[]) method.invoke(filters, f);
            }

        }
         }
        return null;
    }



}
