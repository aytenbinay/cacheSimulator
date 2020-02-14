import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Scanner;
public class cachesim {

    int L1s;
    int L1S;
    int L1E;
    int L1b;
    int L1B;
    int L2s;
    int L2S;
    int L2E;
    int L2b;
    int L2B;
    static cache L1I;
    static cache L1D;
    static cache L2;
    //buraya methodlar gelecek
    public cachesim(int L1s,int L1E,int L1b,int L2s,int L2E,int L2b) {
        L1I=new cache(L1s,L1E,L1b);
        L1D=new cache(L1s,L1E,L1b);
        L2=new cache(L2s,L2E,L2b);
        this.L1s=L1s;
        this.L1E=L1E;
        this.L1b=L1b;
        this.L2s=L2s;
        this.L2E=L2E;
        this.L2b=L2b;
    }
    public static void takeTraceFile(String filename) throws IOException {
        Scanner sc=null;
        sc = new Scanner(new File(filename));
        String address;
        String operation;
        String data="";
        String size=null;


        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            Scanner sc2 = new Scanner(line);
            operation = sc2.next();
            address = sc2.next();
            address=address.substring(0,address.length()-1);
            size = sc2.next();

            if(size.substring(size.length()-1).equals(",")){
                size = size.substring(0,size.length()-1);
                data= sc2.next();
            }else{
                data=null;
            }
            trace t=new trace(operation,address,size,data);
            System.out.println(t.operation+" "+t.address+" "+ t.size+ " " + t.data);
            doOperation(t);
            if(Integer.parseInt(t.size) == 0){
                System.out.println("No operation size is zero.");
            }
            System.out.println();
        }
        System.out.println("\nL1I-hits:"+L1I.HitCounter+" L1I-misses:"+L1I.MissCounter+" L1I-evictions:"+L1I.Eviction);
        System.out.println("L1D-hits:"+L1D.HitCounter+" L1D-misses:"+L1D.MissCounter+" L1D-evictions:"+L1D.Eviction);
        System.out.println("L2-hits:"+L2.HitCounter+" L2-misses:"+L2.MissCounter+" L2-evictions:"+L2.Eviction);
        System.out.println();

    }
	 /*String s=TakeTraceDataFromRAM("ram.txt",trace.address,L1I,Integer.parseInt(trace.size));
		 		System.out.println("for trace "+trace.address);
		 		System.out.println(s); */

    public static void doOperation(trace trace) throws IOException {
        if(Integer.parseInt(trace.size) > 0){
            switch(trace.operation) {
                case("I"):
                    IOpr(trace.address);
                    return;
                case("L"):
                    LOpr(trace.address);
                    return;
                case("S"):
                    SOpr(trace.address,trace.size,trace.data);
                    return;
                case("M"):
                    MOpr(trace.address,trace.size,trace.data);
                    return;



            }
        }


    }
    public static void IOpr(String address) throws IOException {
        String dat=TakeAllDataFromRAM("ram.txt", address,L1I);
        int s1=calSetIndex(address, L1I);
        int s2=calSetIndex(address, L2);
        boolean a,b;
        a=searchInCache(address, L1I);
        if(a==true) {
            L1I.HitCounter++;
            System.out.print("L1 hit,");
        }
        else {
            System.out.print("L1 miss,");
            L1I.MissCounter++;
            writeToCache(address, L1I);

        }
        b=searchInCache(address, L2);
        if(b==true) {
            L2.HitCounter++;
            System.out.println("L2 hit.");
        }
        else {
            L2.MissCounter++;
            System.out.println("L2 miss.");
            writeToCache(address, L2);
        }
        if(L2.S>1) {
            System.out.print("Place in L2 set "+s2+",");
        }else {
            System.out.print("Place in L2,");
        }
        if(L1I.S>1) {
            System.out.print("L1I set"+s1);
            System.out.println();
        }else {
            System.out.print("L1I.");
            System.out.println();
        }



    }
    public static void LOpr(String address) throws IOException {
        String dat=TakeAllDataFromRAM("ram.txt", address,L1D);
        boolean a,b;
        int s1=calSetIndex(address, L1D);
        int s2=calSetIndex(address, L2);
        a=searchInCache(address, L1D);
        if(a==true) {
            L1D.HitCounter++;
            System.out.print("L1 hit, ");
        }
        else {
            L1D.MissCounter++;

            System.out.print("L1 miss,");
            writeToCache(address, L1D);


        }
        b=searchInCache(address, L2);
        if(b==true) {
            L2.HitCounter++;
            System.out.println("L2 hit. ");
        }
        else {
            L2.MissCounter++;
            System.out.println("L2 miss.");
            writeToCache(address, L2);
        }
        if(L2.S>1) {
            System.out.print("Place in L2 set "+s2+", ");
        }else {
            System.out.print("Place in L2, ");
        }
        if(L1D.S>1) {
            System.out.print("L1D set"+s1);
            System.out.println();
        }else {
            System.out.print("L1D.");
            System.out.println();
        }



    }
    public static void SOpr(String address, String size, String data) throws IOException {
        boolean a=searchInCache(address, L1D);
        if(a==true) {
            String olddata=null;
            String addfirstpart=null;
            String addlastpart=null;
            String newdata=null;
            int s1=calSetIndex(address, L1D);
            int t1=calTag(address, L1D);
            int dec_add=Integer.parseInt(address,16);
            int start=dec_add%L1D.B;

            for(int i=0;i<L1D.E;i++) {
                if(L1D.cacheset[s1].line[i].tag==t1 && L1D.cacheset[s1].line[i].v==true) {  //cachete varsa cacheteki datayı güncelliyoruz yoksa cache e eklemiyoruz
                    L1D.HitCounter++;
                    System.out.print("L1D hit, ");
                    olddata=L1D.cacheset[s1].line[i].data;
                    addfirstpart=olddata.substring(0,(2*start));
                    addlastpart=olddata.substring(2*start+(2*Integer.parseInt(size)),olddata.length());
                    newdata=addfirstpart+data+addlastpart;
                    L1D.cacheset[s1].line[i].data=newdata;

                }
            }

        }else {
            L1D.MissCounter++;
            System.out.print("L1D miss, ");
        }
        boolean b=searchInCache(address, L2);
        if(b==true) {
            String olddata=null;
            String addfirstpart=null;
            String addlastpart=null;
            String newdata=null;
            int s2=calSetIndex(address, L2);
            int t2=calTag(address, L2);
            int dec_add=Integer.parseInt(address,16);
            int start=dec_add%L2.B;

            for(int i=0;i<L2.E;i++) {
                if(L2.cacheset[s2].line[i].tag==t2 && L2.cacheset[s2].line[i].v==true) {  //cachete varsa cacheteki datayı güncelliyoruz yoksa cache e eklemiyoruz
                    L2.HitCounter++;
                    System.out.println("L2 hit. ");
                    olddata=L2.cacheset[s2].line[i].data;
                    addfirstpart=olddata.substring(0,(2*start));
                    addlastpart=olddata.substring(2*start+(2*Integer.parseInt(size)),olddata.length());
                    newdata=addfirstpart+data+addlastpart;
                    L2.cacheset[s2].line[i].data=newdata;

                }
            }

        }else {
            L2.MissCounter++;
            System.out.println("L2 miss ");
        }

        // rami güncelliyoruz
        updateRAM(address, data);
        System.out.print("Store in ");
        if(a==true) {
            System.out.print("L1D, ");
        }
        if(b==true) {
            System.out.print("L2, ");
        }
        System.out.println("RAM ");


    }
    public static void MOpr(String address, String size, String data) throws IOException {
        boolean a=searchInCache(address, L1D);
        if(a==true) {
            String olddata=null;
            String addfirstpart=null;
            String addlastpart=null;
            String newdata=null;
            int s1=calSetIndex(address, L1D);
            int t1=calTag(address, L1D);
            int dec_add=Integer.parseInt(address,16);
            int start=dec_add%L1D.B;

            for(int i=0;i<L1D.E;i++) {
                if(L1D.cacheset[s1].line[i].tag==t1 && L1D.cacheset[s1].line[i].v==true) {  //cachete varsa cacheteki datayı güncelliyoruz yoksa cache e ekliyoruz
                    L1D.HitCounter++;
                    System.out.print("L1D hit, ");
                    olddata=L1D.cacheset[s1].line[i].data;
                    addfirstpart=olddata.substring(0,(2*start));
                    addlastpart=olddata.substring(2*start+(2*Integer.parseInt(size)),olddata.length());
                    newdata=addfirstpart+data+addlastpart;
                    L1D.cacheset[s1].line[i].data=newdata;

                }
            }

        }else {
            System.out.print("L1D miss, ");
            L1D.MissCounter++;
            writeToCache(address, L1D);
            L1D.HitCounter++;
        }
        boolean b=searchInCache(address, L2);
        if(b==true) {
            String olddata=null;
            String addfirstpart=null;
            String addlastpart=null;
            String newdata=null;
            int s2=calSetIndex(address, L2);
            int t2=calTag(address, L2);
            int dec_add=Integer.parseInt(address,16);
            int start=dec_add%L2.B;

            for(int i=0;i<L2.E;i++) {
                if(L2.cacheset[s2].line[i].tag==t2 && L2.cacheset[s2].line[i].v==true) {  //cachete varsa cacheteki datayı güncelliyoruz yoksa cache e ekliyoruz
                    L2.HitCounter++;
                    System.out.println("L2 hit. ");
                    olddata=L2.cacheset[s2].line[i].data;
                    addfirstpart=olddata.substring(0,(2*start));
                    addlastpart=olddata.substring(2*start+(2*Integer.parseInt(size)),olddata.length());
                    newdata=addfirstpart+data+addlastpart;
                    L2.cacheset[s2].line[i].data=newdata;

                }
            }

        }else {
            L2.MissCounter++;
            System.out.println("L2 miss ");
            writeToCache(address, L2);
            L2.HitCounter++;
        }
        updateRAM(address, data);
        System.out.print("Modify in ");
        if(a==true) {
            System.out.print("L1D, ");
        }
        if(b==true) {
            System.out.print("L2, ");
        }
        System.out.println("RAM ");


    }
    public static boolean searchInCache(String add, cache cache1) {
        int s1=calSetIndex(add, cache1);
        int t1=calTag(add, cache1);
        for(int i=0;i<cache1.E;i++) {
            if(cache1.cacheset[s1].line[i].tag==t1 && cache1.cacheset[s1].line[i].v==true) {
                return true;
            }
        }
        return false;
    }
    public static void writeToCache(String address, cache c) throws IOException {
        int sw=calSetIndex(address, c);
        int tw=calTag(address, c);
        int cnt=0;
        for(int i=0;i<c.E;i++) {
            if( c.cacheset[sw].line[i].v == false) {
                cnt++;
                c.cacheset[sw].line[i].tag=tw;
                c.cacheset[sw].line[i].index=sw;
                c.cacheset[sw].line[i].data=TakeAllDataFromRAM("ram.txt", address,c);
                c.cacheset[sw].line[i].v=true;
                c.timer++;
                c.cacheset[sw].line[i].time=c.timer;
				/* if(c.cacheset[sw].line[c.cacheset[sw].lineindexwithmintime].time > c.cacheset[sw].line[i].time ) {
					 c.cacheset[sw].line[c.cacheset[sw].lineindexwithmintime].time = c.cacheset[sw].line[i].time;
					 c.cacheset[sw].line[c.cacheset[sw].lineindexwithmintime].tag = c.cacheset[sw].line[i].tag;
					 c.cacheset[sw].line[c.cacheset[sw].lineindexwithmintime].v=c.cacheset[sw].line[i].v;
				 }*/
                c.cacheset[sw].lineindexwithmintime=returnMinTimeIndex(c, sw);

                break;

            }

        }

        // eviction yapması gerekiyor......... yeni minline da eklemeli
        if(cnt==0) {
            c.cacheset[sw].line[c.cacheset[sw].lineindexwithmintime].data=TakeAllDataFromRAM("ram.txt", address,c);
            c.cacheset[sw].line[c.cacheset[sw].lineindexwithmintime].tag=tw;
            c.cacheset[sw].line[c.cacheset[sw].lineindexwithmintime].index=sw;
            c.cacheset[sw].line[c.cacheset[sw].lineindexwithmintime].v=true;
            c.timer++;
            c.cacheset[sw].line[c.cacheset[sw].lineindexwithmintime].time=c.timer;
            c.cacheset[sw].lineindexwithmintime=returnMinTimeIndex(c, sw);
            c.Eviction++;

        }


    }
    public static int returnMinTimeIndex(cache c,int setIndex) {
        int min=c.cacheset[setIndex].line[0].time;
        int minindex=0;
        for(int i=0;i<c.E;i++){
            if(c.cacheset[setIndex].line[i].time < min ) {
                min=c.cacheset[setIndex].line[i].time;
                minindex=i;
            }
        }
        return minindex;

    }
    public static String TakeAllDataFromRAM(String RAMfile, String address, cache c) throws IOException {
        int decimalAddress=Integer.parseInt(address,16);
        int r=decimalAddress%c.B;
        int go=decimalAddress-r;
        RandomAccessFile file = new RandomAccessFile(RAMfile, "r");
        file.seek(go*3);
        byte[] bytes = new byte[(c.B)*3];
        file.read(bytes);
        String s=new String(bytes);
        //System.out.println(s);
        return s;

        /*Scanner scn = new Scanner(new File(RAMfile));
        String data="";
        int decimalAddress=Integer.parseInt(address,16);
        int r=decimalAddress%c.B;
        int go=decimalAddress-r;

        for(int i=0;i<go;i++) {
            scn.next();
        }
        for(int i=0;i<c.B;i++) {
            data+=scn.next();
        }
        return data;*/

    }
   /* public static String TakeTraceDataFromRAM(String RAMfile, String address, cache c, int size) throws IOException {
        Scanner scn = new Scanner(new File(RAMfile));
        String data="";
        int decimalAddress=Integer.parseInt(address,16);
        int r=decimalAddress%c.B;
        int go=decimalAddress-r;


        for(int i=0;i<go;i++) {
            scn.next();
        }
        for(int i=0;i<size;i++) {
            data+=scn.next();
        }
        return data;

    }*/
    public static void updateRAM( String address, String data) throws IOException {
        String newdata="";
        for(int i=0; i< data.length();i+=2) {
            newdata+=data.substring(i,i+2);
            newdata+=" ";
        }

        RandomAccessFile raf = new RandomAccessFile("ram.txt", "rw");
        raf.seek(Integer.parseInt(address,16)*3);
        raf.write(newdata.getBytes());

    }

    public static int calTag(String address,cache c) {
        int tagval=0;
        int dec_add=Integer.parseInt(address,16);
        int [] bin= decimaltoBinary(dec_add, c);
        int [] tagbin=new int[bin.length-c.s-c.b];
        for(int i=0;i<tagbin.length;i++) {
            tagbin[i]=bin[i];
        }
        String n=Arrays.toString(tagbin).replaceAll("\\[|\\]|,|\\s", "");
        tagval=Integer.parseInt(n,2);
        return tagval;


    }
    public static int calSetIndex(String address,cache c) {
        int setnumber=0;
        int dec_add=Integer.parseInt(address,16);
        int [] bin= decimaltoBinary(dec_add, c);
        int [] setbin=new int[c.s];



        for(int i=bin.length-(c.s+c.b) , j=0; i<(bin.length-c.b); i++,j++) {
            setbin[j]=bin[i];

        }

        if(c.s>0) {
            String n=Arrays.toString(setbin).replaceAll("\\[|\\]|,|\\s", "");
            setnumber=Integer.parseInt(n,2);
        }else {
            setnumber=0;
        }



        return setnumber;


    }


    public static int[] decimaltoBinary(int decimal,cache c) {
        int counter = 0;
        int number=decimal;
        while (number > 0)
        {
            number= number / 2;
            counter++;
        }
        int size=0;
        if(counter>(c.b+c.s)) {
            size=counter;
        }else {
            size=(c.b+c.s)+1;
        }
        int[] binaryNum = new int[size];
        int i=0;
        while (decimal > 0)
        {
            binaryNum[i] = decimal % 2;
            decimal= decimal / 2;
            i++;

        }

        int[] swapped=new int[size];
        int j=0;
        for(i=swapped.length-1;i>=0;i--) {
            swapped[j]=binaryNum[i];
            j++;

        }

        return swapped;


    }




}
