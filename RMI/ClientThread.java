public class ClientThread implements Runnable{

    TicketServer server;
    byte[] byteImage;
    double media;
    double desvio_padrao;
    double K;

    ClientThread(TicketServer server, byte[] byteImage, double media, double desvio_padrao, double K){
        this.server = server;
        this.byteImage = byteImage;
        this.media = media;
        this.desvio_padrao = desvio_padrao;
        this.K = K;
    }

    public void run(){
        try {
            this.byteImage = this.server.getBinImage(this.byteImage, this.media, this.desvio_padrao, this.K);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] getImage(){
        return this.byteImage;
    }

}
