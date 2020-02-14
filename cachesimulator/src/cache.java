import java.io.*;
import java.io.FileNotFoundException;
public class cache {
    int s=0;
    int S=0;
    int b=0 ;
    int E=0;

    int HitCounter=0;
    int MissCounter=0;
    int Eviction=0;
    int B =0;
    set [] cacheset=null;
    int timer=0;


    public cache(int s,int E,int b) {
        this.s=s;
        this.b=b;
        this.E=E;
        this.B =(int) Math.pow(2,b);
        this.S=(int)Math.pow(2,s);
        cacheset=new set[S];
        for(int i=0;i<S;i++) {
            cacheset[i]=new set(E);
        }

    }
    public static void printcache(cache l, String filename) throws FileNotFoundException{
        PrintStream o = new PrintStream(new File(filename));
        PrintStream console = System.out;
        for(int i=0;i<l.S;i++) {
            for(int j=0; j<l.E;j++) {
                System.setOut(o);
               System.out.print(l.cacheset[i].line[j].tag+" "+l.cacheset[i].line[j].time+" "+l.cacheset[i].line[j].v+" "+l.cacheset[i].line[j].data);
               System.out.println();

            }
            System.setOut(o);
            System.out.println();

        }



    }
    public static void writeToPosition(String filename, String data1, long position) throws IOException {
        RandomAccessFile writer = new RandomAccessFile(filename, "rw");
        writer.seek(position);
        writer.writeChars(data1);
        writer.close();
    }


}
