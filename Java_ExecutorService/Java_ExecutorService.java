import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;

public class Java_ExecutorService {
    public List<ArrayList<ArrayList<Integer>>> read(String filename) {
        ArrayList<ArrayList<Integer>> A = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Integer>> B = new ArrayList<ArrayList<Integer>>();

        String thisLine;

        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));

            while ((thisLine = br.readLine()) != null) {
                if (thisLine.trim().equals("")) {
                    break;
                } else {
                    ArrayList<Integer> line = new ArrayList<Integer>();
                    String[] lineArray = thisLine.split("\t");
                    for (String number : lineArray) {
                        line.add(Integer.parseInt(number));
                    }
                    A.add(line);
                }
            }

            while ((thisLine = br.readLine()) != null) {
                ArrayList<Integer> line = new ArrayList<Integer>();
                String[] lineArray = thisLine.split("\t");
                for (String number : lineArray) {
                    line.add(Integer.parseInt(number));
                }
                B.add(line);
            }
            br.close();
        } catch (IOException e) {
            System.err.println("Error: " + e);
        }

        List<ArrayList<ArrayList<Integer>>> res = new LinkedList<ArrayList<ArrayList<Integer>>>();
        res.add(A);
        res.add(B);
        return res;
    }

    public int[][] matrixMultiplication(ArrayList<ArrayList<Integer>> A, ArrayList<ArrayList<Integer>> B, int m, int n) {
        int[][] C = new int[m][n];

        for (int i = 0; i < m; i++) {
            for (int k = 0; k < n; k++) {
                int temp = A.get(i).get(k);
                for (int j = 0; j < n; j++) {
                    C[i][j] += temp * B.get(k).get(j);
                }
            }
        }

        return C;
    }

    public ArrayList<ArrayList<ArrayList<Integer>>> splitMatrix(ArrayList<ArrayList<Integer>> A, int nrOfThreads) {
        int n = A.size();
        int m = n / nrOfThreads;
        ArrayList<ArrayList<ArrayList<Integer>>> B = new ArrayList<ArrayList<ArrayList<Integer>>>();

        for (int i = 0; i < nrOfThreads; i++){
            B.add(new ArrayList<ArrayList<Integer>>(A.subList(i*m, (i+1)*m)));
        }
        return B;
    }

    public static void main(String[] args) {
        String filename = "";
        int nrOfThreads = 0;
        if (args.length < 2) {
            System.out.println(
                    "Missing filename and/or number of threads\nUSAGE: java Java_ExecutorService <file> <nrOfThreads>");
            System.exit(1);
        } else {
            filename = args[0];
            nrOfThreads = Integer.parseInt(args[1]);
            Java_ExecutorService java_ExecutorService = new Java_ExecutorService();
            java_ExecutorService.start(filename, nrOfThreads);
        }

    }

    public void start(String filename, int nrOfThreads) {
        List<ArrayList<ArrayList<Integer>>> matrices = read(filename);
        ArrayList<ArrayList<Integer>> A = matrices.get(0);
        ArrayList<ArrayList<Integer>> B = matrices.get(1);

        if (nrOfThreads <= 0) {
            int n = A.size();
            long startTime = System.nanoTime();
            int[][] C = matrixMultiplication(A, B, n, n);
            long endTime = System.nanoTime();

            System.out.println("Execution took " + (endTime - startTime) + " ns");
        } else {
            if (A.size() % nrOfThreads != 0) {
                System.out.println("Size of matrix is not divisible by the supplied number of threads");
                System.exit(1);
            }
            ArrayList<int[][]> result = new ArrayList<int[][]>();
            int[][] empty = new int[][]{{}};

            for(int i = 0; i < nrOfThreads; i++){
                result.add(empty);
            }

            ArrayList<ArrayList<ArrayList<Integer>>> workerMatrices = splitMatrix(A, nrOfThreads);

            long startTime = System.nanoTime();

            ExecutorService ex = Executors.newFixedThreadPool(nrOfThreads);
            for (int i = 0; i < nrOfThreads; i++) {
                ex.execute(new Worker(workerMatrices.get(i), B, i, result));
            }
            ex.shutdown();

            try {
                ex.awaitTermination(10L, TimeUnit.MINUTES);
            } catch(Exception e){
                System.err.println(e);
            }

            long endTime = System.nanoTime();

            System.out.println("Execution took " + (endTime - startTime) + " ns");
        }
    }

    class Worker implements Runnable {
        private ArrayList<ArrayList<Integer>> A;
        private ArrayList<ArrayList<Integer>> B;
        private int index;
        private ArrayList<int[][]> result;
        private int m;
        private int n;

        public Worker(ArrayList<ArrayList<Integer>> A, ArrayList<ArrayList<Integer>> B, int index,
                ArrayList<int[][]> result) {
            this.A = A;
            this.B = B;
            this.index = index;
            this.result = result;
            this.m = A.size();
            this.n = B.size();
        }

        @Override
        public void run() {
            this.result.set(this.index, matrixMultiplication(this.A, this.B, this.m, this.n));
        }

    }
}
