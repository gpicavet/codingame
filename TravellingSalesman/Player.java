import java.util.*;
import java.util.stream.Collectors;

class Player {

    Point[] points;
    int[] path;
    int[] bestpath;

    float eval,besteval;
    float[][] distTable;

    Player(Point[] points, int[] path) {
        this.points = points;
        this.path = path;
        this.bestpath = new int[path.length];

        distTable = new float[points.length][points.length];
        for(int i=0; i<points.length; i++) {
            for(int j=0; j<i; j++) {
                float d=(float)Math.sqrt(dist2(i,j));
                distTable[i][j]=d;
                distTable[j][i]=d;
            }
            distTable[i][i]=0;
        }
    }

    void move(int i, int j) {

        for(int k=0; k<(j-i)/2; k++) {
            int tmp = path[i+1+k];
            path[i+1+k]=path[j-k];
            path[j-k]=tmp;
        }
/*
        int evalNew = eval;
        evalNew -= dist2(path[i], path[i+1]);
        evalNew -= dist2(path[j], path[j+1]);
        evalNew += dist2(path[i], path[j]);
        evalNew += dist2(path[i+1], path[j+1]);

        return evalNew;*/
    }

    int dist2(int i0, int i1) {
            Point p0 = points[i0];
            Point p1 = points[i1];
            int dx = p0.x-p1.x;
            int dy = p0.y-p1.y;
            return dx*dx+dy*dy;
    }

    float eval() {
        float r=0;
        for(int i=0; i<path.length-1; i++) {
            //r += dist2(path[i], path[i+1]);
            r += distTable[path[i]][path[i+1]];
        }
        return r;
    }

    void simul(long timeout) {
        SplittableRandom RAND = new SplittableRandom();
        double T=100000;
        double K=0.9997;
        int P = 130;
        long startTime = System.currentTimeMillis();
        eval = eval();
        besteval = eval;
        int iter=0;
        while(System.currentTimeMillis() - startTime < timeout) {
            
            for(int p=0; p<P; p++) {
                int i = RAND.nextInt(points.length);
                int j = (i+2+RAND.nextInt(points.length-3))%points.length;
                if(i>j) {
                    int t=i;
                    i=j;
                    j=t;
                }

                move(i,j);
                float evalNew = eval();

                if(evalNew < eval || RAND.nextDouble() < Math.exp(-(evalNew-eval)/T)) {
                    eval = evalNew;
                    if(eval<besteval) {
                        besteval = eval;
                        System.arraycopy(path, 0, bestpath, 0, path.length);
                    }
                    
                } else {
                    move(i,j);
                }

                iter++;
            }
/*
            if(iter%100000 == 0) {
                System.err.println(iter + " "+ T +" " + eval+" "+besteval);
            }
*/
            T *= K;
        }
        System.err.println(iter+" "+T);
    }

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int N = in.nextInt(); // This variables stores how many nodes are given

        Point[] points = new Point[N];
        int[] path = new int[N+1];
        for (int i = 0; i < N; i++) {
            int x = in.nextInt(); // The x coordinate of the given node
            int y = in.nextInt(); // The y coordinate of the given node
            points[i] = new Point(x,y);
            path[i] = i;
        }
        path[N]=0;


        Player p = new Player(points, path);
        p.simul(4800);


        System.out.println(Arrays.stream(p.bestpath)
        .mapToObj(String::valueOf)
        .collect(Collectors.joining(" "))); // You have to output a valid path
    }
}

class Point {
    int x; 
    int y;
    Point(int x, int y) {this.x=x; this.y=y;}
}
