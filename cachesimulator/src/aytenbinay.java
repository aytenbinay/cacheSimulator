import java.io.IOException;
import java.lang.*;
public class aytenbinay {
    public static void main(String[] args) throws IOException {
        cachesim caches=new cachesim(Integer.parseInt(args[1]),Integer.parseInt(args[3]),Integer.parseInt(args[5]),Integer.parseInt(args[7]),Integer.parseInt(args[9]),Integer.parseInt(args[11]));
        caches.takeTraceFile(args[13]);
        cache.printcache(cachesim.L1I,"L1I.txt");
        cache.printcache(cachesim.L1D,"L1D.txt");
        cache.printcache(cachesim.L2,"L2.txt");
    }
}
