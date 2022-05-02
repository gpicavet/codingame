import java.util.*;
import java.util.stream.Collectors;

/**
 * Simulated Annealing using 2-opt heuristic
 */
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
                float d=dist(i,j);
                distTable[i][j]=d;
                distTable[j][i]=d;
            }
            distTable[i][i]=0;
        }
    }

    float dist(int i0, int i1) {
            Point p0 = points[i0];
            Point p1 = points[i1];
            int dx = p0.x-p1.x;
            int dy = p0.y-p1.y;
            return (float)Math.sqrt(dx*dx+dy*dy);
    }

    float diffEval2opt(int i, int j) {
        //2-opt diff
        float diff=0;
        diff -= distTable[path[i]][path[i+1]];
        diff -= distTable[path[j]][path[j+1]];
        diff += distTable[path[i]][path[j]];
        diff += distTable[path[i+1]][path[j+1]];

        return diff;

    }

    void move2opt(int i, int j) {
        //2-opt : reconnecting by switching points
        for(int k=0; k<(j-i)/2; k++) {
            int tmp = path[i+1+k];
            path[i+1+k]=path[j-k];
            path[j-k]=tmp;
        }

    }


    float eval() {
        float r=0;
        for(int i=0; i<path.length-1; i++) {
            r += distTable[path[i]][path[i+1]];
        }
        return r;
    }

    void simul(double T, double K, int P, long timeout) {
        SplittableRandom RAND = new SplittableRandom();

        long startTime = System.currentTimeMillis();
        eval = eval();
        besteval = eval;
        int iter=0;
        while(System.currentTimeMillis() - startTime < timeout) {
            
            for(int p=0; p<P; p++) {
                //choose 2 points for k-opt
                int i = RAND.nextInt(points.length-4+1);
                int j = (i+2+RAND.nextInt(points.length-i-2));

                
                float evalNew = eval + diffEval2opt(i,j);
                move2opt(i,j);
                
                if(evalNew < eval || RAND.nextDouble() < Math.exp(-(evalNew-eval)/T)) {
                    eval = evalNew;
                    //keep best eval ever
                    if(eval<besteval) {
                        besteval = eval;
                        System.arraycopy(path, 0, bestpath, 0, path.length);
                    }
                    
                } else {
                    //revert by redoing move
                    move2opt(i,j);
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
        p.simul(1000000, 0.9999, 150, 4900);


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
