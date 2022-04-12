// TicketClient.java

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.rmi.*;
import java.util.Arrays;
import java.util.Scanner;

public class TicketClient implements Serializable {

  public static byte[] toByteArray(BufferedImage bi, String format)
          throws IOException {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(bi, format, baos);
    byte[] bytes = baos.toByteArray();
    return bytes;

  }

  public static BufferedImage toBufferedImage(byte[] bytes)
          throws IOException {

    InputStream is = new ByteArrayInputStream(bytes);
    BufferedImage bi = ImageIO.read(is);
    return bi;

  }

  public static void main(String [] args) {

//      String fileDir;
      Scanner sc = new Scanner(System.in);

      System.setSecurityManager(new RMISecurityManager());

      if (args.length<1) {
        System.err.println("Usage: TicketClient <server-rmi-url>");
        System.exit(-1);
      }

      int noOfRemoteElements = args.length;

      String[] fullname = new String[args.length];
      for(int i=0; i<args.length; i++){
          fullname[i] = args[i];
      }
      TicketServer[] server = new TicketServer[args.length];
      try {
          for(int i=0; i<args.length; i++){
              server[i] = (TicketServer)Naming.lookup(fullname[i]);
          }
      } catch (Exception e) {
        System.out.println("Caught an exception doing name lookup on "+fullname[0]
               +": "+e);
        System.exit(-1);
      }

      System.out.println("Insira a o diretorio da imagem, em PNG, que deseja transformar: ");

      File imageFile = new File(sc.next());
      if(imageFile == null){
        System.out.println("Imagem nao encontrada");
        System.exit(-1);
      }

      BufferedImage image = null;

      try {
        image = ImageIO.read(imageFile);
      }catch (Exception e){
        System.out.println("Erro ao ler a imagem");
        System.exit(-1);
      }

      double K = 0.3f;
      double media = 0;
      double desvio_padrao = 0;
      try{
          int height = image.getHeight();
          int width  = image.getWidth();

          for(int y=0; y< height; y++){
              for(int x=0; x< width; x++){
                  int color = image.getRGB(x, y);
                  int red   = (color & 0xff0000) >> 16;
                  int green = (color & 0x00ff00) >> 8;
                  int blue  = (color & 0x0000ff);

                  int rgb = (red+green+blue);
                  media += rgb / (double)( height * width );
              }
          }

          for(int y=0; y< height; y++){
              for(int x=0; x < width; x++){
                  int color = image.getRGB(x, y);
                  int red   = (color & 0xff0000) >> 16;
                  int green = (color & 0x00ff00) >> 8;
                  int blue  = (color & 0x0000ff);
                  int rgb = (red+green+blue);
                  desvio_padrao += Math.pow(rgb - media, 2);
              }
          }

          desvio_padrao = Math.sqrt( desvio_padrao / (height * width) );
      } catch ( Exception e ) {
          System.out.println(e);
      }

      try {

          byte[][] byteArrays = new byte[noOfRemoteElements][];

          int heigthIter = image.getHeight() / noOfRemoteElements;
          int widthIter  = image.getWidth();

          Thread[] t = new Thread[noOfRemoteElements];
          ClientThread[] clientThread = new ClientThread[noOfRemoteElements];

          for(int i=0; i<noOfRemoteElements; i++){
              byte[] b = toByteArray(image.getSubimage(0, i * heigthIter, widthIter, heigthIter), "png");
              clientThread[i] = new ClientThread(server[i], b, media, desvio_padrao, K);
              t[i] = new Thread(clientThread[i]);
              t[i].start();
          }

          BufferedImage[] images = new BufferedImage[noOfRemoteElements];
          for(int i=0; i<noOfRemoteElements; i++){
              t[i].join();
              byteArrays[i] = clientThread[i].getImage();
              images[i] = toBufferedImage(byteArrays[i]);
          }

          Graphics2D g = image.createGraphics();
          for(int i=0; i<noOfRemoteElements; i++){
              g.drawImage(images[i], 0, i * heigthIter, null);
          }

      } catch (Exception e) {
          System.out.println("Não foi possível converter a imagem: "+e);
          System.exit(-1);
      }

      File grayImage = new File(imageFile.getName().replace(".png", "") + "-bin.png");

      try {
        ImageIO.write(image, "png", grayImage);
        System.out.println(">> Imagem convertida e salva com sucesso! \""+grayImage.getAbsolutePath()+"\"");
        System.out.println("   \""+grayImage.getAbsolutePath()+"\" \n");
      } catch (IOException e) {
        e.printStackTrace();
      }
  }

}

