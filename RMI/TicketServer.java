// TicketServer.java

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.rmi.*;

public interface TicketServer extends Remote
{
  public byte[] getBinImage(byte[] image, double media, double desvio_padrao, double K) throws IOException;
}

