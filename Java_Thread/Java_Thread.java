import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

public class Java_Thread {
    public List<ArrayList<ArrayList<Integer>>> read(String filename) {
        ArrayList<ArrayList<Integer>> A = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Integer>> B = new ArrayList<ArrayList<Integer>>();

        String thisLine;

        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));

            // Begin reading A
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

            // Begin reading B
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
        // initialise C
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

    public ArrayList<ArrayList<Integer>> splitMatrix(ArrayList<ArrayList<Integer>> A, int threadNr, int nrOfThreads) {
        int n = A.size();
        int m = n / nrOfThreads;
        ArrayList<ArrayList<Integer>> B = new ArrayList<ArrayList<Integer>>();

        for (int i = threadNr * m; i < threadNr * m + m; i++) {
            B.add(A.get(i));
        }
        return B;
    }

    public void printMatrix(int[][] matrix) {
        for (int[] line : matrix) {
            int i = 0;
            StringBuilder sb = new StringBuilder(matrix.length);
            for (int number : line) {
                if (i != 0) {
                    sb.append("\t");
                } else {
                    i++;
                }
                sb.append(number);
            }
            System.out.println(sb.toString());
        }
    }

    public static void main(String[] args) {
        String filename = "";
        int nrOfThreads = 0;
        if (args.length < 2) {
            System.out.println(
                    "Missing filename and/or number of threads\nUSAGE: java Java_Thread <file> <nrOfThreads>");
            System.exit(1);
        } else {
            filename = args[0];
            nrOfThreads = Integer.parseInt(args[1]);
            Java_Thread java_Thread = new Java_Thread();
            java_Thread.start(filename, nrOfThreads);
        }

    }

    public void start(String filename, int nrOfThreads) {
        List<ArrayList<ArrayList<Integer>>> matrices = read(filename);
        ArrayList<ArrayList<Integer>> A = matrices.get(0);
        ArrayList<ArrayList<Integer>> B = matrices.get(1);

        // Check input nr of threads
        if (nrOfThreads <= 0) {
            // Start multiplication without multithreading
            int n = A.size();
            long startTime = System.nanoTime();
            int[][] C = matrixMultiplication(A, B, n, n);
            long endTime = System.nanoTime();
            
            //printMatrix(C);
            
            System.out.println("Execution took " + (endTime - startTime) + " ns");
        } else {
            if (A.size() % nrOfThreads != 0) {
                System.out.println("Size of matrix is not divisible by the supplied number of threads");
                System.exit(1);
            }
            // Create submatrixes and threads
            ArrayList<Worker> threads = new ArrayList<Worker>();
            ArrayList<int[][]> result = new ArrayList<int[][]>();
            int[][] empty = new int[][]{{}};

            for (int i = 0; i < nrOfThreads; i++) {
                threads.add(new Worker(splitMatrix(A, i, nrOfThreads), B, i, result));
                result.add(empty);
            }

            // Start threads
            long startTime = System.nanoTime();
            for (int i = 0; i < nrOfThreads; i++) {
                threads.get(i).start();
            }

            // Wait for threads to die
            for (int i = 0; i < nrOfThreads; i++) {
                try {
                    threads.get(i).join();
                } catch (Exception e) {
                    System.err.println(e);
                }
            }
            long endTime = System.nanoTime();

            // for (int[][] matrix : result) {
            //     printMatrix(matrix);
            //     System.out.println("\n");
            // }
            System.out.println("Execution took " + (endTime - startTime) + " ns");
        }
    }

    class Worker extends Thread {
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
