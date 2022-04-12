// TicketServerImpl.java
import java.awt.*;
import java.io.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Scanner;


public class TicketServerImpl extends UnicastRemoteObject 
          implements TicketServer, Serializable
{

//  private volatile byte[] byteImage;
//  private volatile double media;
//  private volatile double desvio_padrao;
//  private volatile double K;

  TicketServerImpl() throws RemoteException { }

  public static BufferedImage toBufferedImage(byte[] bytes)
          throws IOException {

    InputStream is = new ByteArrayInputStream(bytes);
    BufferedImage bi = ImageIO.read(is);
    return bi;
  }

  public static byte[] toByteArray(BufferedImage bi, String format)
        throws IOException {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(bi, format, baos);
    byte[] bytes = baos.toByteArray();
    return bytes;

  }

  public static void main(String [] args) {
    // install RMI security manager
    System.setSecurityManager(new RMISecurityManager());
    // arg. 0 = rmi url
    if (args.length!=1) {
      System.err.println("Usage: TicketServerImpl <server-rmi-url>");
      System.exit(-1);
    }
    try {
      String name = args[0];
      TicketServerImpl server = new TicketServerImpl();
      Naming.rebind(name, server);
      System.out.println("Started TicketServer, registered as " + name+"\n");
    }
    catch(Exception e) {
      System.out.println("Caught exception while registering: " + e);
      System.exit(-1);
    }
  }

  public byte[] getBinImage(byte[] byteImage, double media, double desvio_padrao, double K) throws IOException{

      BufferedImage image = toBufferedImage(byteImage);
      int height = image.getHeight();
      int width = image.getWidth();

      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          int color = image.getRGB(x, y);
          int red = (color & 0xff0000) >> 16;
          int green = (color & 0x00ff00) >> 8;
          int blue = (color & 0x0000ff);
          int rgb = (red + green + blue);

          if (rgb >= media - K * desvio_padrao) {
            image.setRGB(x, y, Color.WHITE.getRGB());
          } else {
            image.setRGB(x, y, Color.BLACK.getRGB());
          }

        }
      }

      return toByteArray(image, "png");

  }

}
