import java.io.*;
import java.util.*;

class Retele extends Task {

    public int N;
    public int M;
    public int K;
    public Map<Integer, ArrayList<Integer>> adjacencyMap = new LinkedHashMap<>();
    public String result;
    public String solutionSize;
    public String[] solutionElem;
    public int[][] myMatrix; // it helps with the mapping of the elements with a specific formula

    public static void main(String[] args) throws IOException, InterruptedException {
        Retele retele = new Retele();
        retele.solve();
    }

    @Override
    public void solve() throws IOException, InterruptedException {
        this.readProblemData();
        this.formulateOracleQuestion();
        this.askOracle();
        this.decipherOracleAnswer();
        this.writeAnswer();
    }

    @Override
    public void readProblemData() throws IOException {
        BufferedReader buffScanner = new BufferedReader(new InputStreamReader(System.in));
        String[] dataInput = buffScanner.readLine().split(" ");
        /*
         * Read every param from first line
         * N = number of nodes
         * M = number of links between the nodes
         * K = size of the clique
         */
        N = Integer.parseInt(dataInput[0]);
        M = Integer.parseInt(dataInput[1]);
        K = Integer.parseInt(dataInput[2]);

        /*
         * This looks symmetrical since it is an undirected graph
         * v = first node
         * w = second node
         */
        for (int i = 0; i < M; i++) {
            dataInput = buffScanner.readLine().split(" ");
            Integer v = Integer.parseInt(dataInput[0]);
            Integer w = Integer.parseInt(dataInput[1]);

            if (adjacencyMap.containsKey(v)) {
                adjacencyMap.get(v).add(w);
            } else if (!adjacencyMap.containsKey((v))) {
                adjacencyMap.put(v, new ArrayList<>());
                adjacencyMap.get(v).add(w);
            }
            if (adjacencyMap.containsKey(w)) {
                adjacencyMap.get(w).add(v);
            } else if (!adjacencyMap.containsKey((w))) {
                adjacencyMap.put(w, new ArrayList<>());
                adjacencyMap.get(w).add(v);
            }
        }
    }

    @Override
    public void formulateOracleQuestion() throws IOException {
        try {
            FileWriter Writer = new FileWriter("sat.cnf");
            int numOfClauses = K + K * K * N * N + K * N * N + N * K * K;
            Writer.write("p cnf ");
            Writer.write(N + " " + numOfClauses + "\n");

            myMatrix = new int[K + 1][N + 1];
            mapTheMatrix(myMatrix);

            solveFirstSetOfClauses(Writer);
            solveSecondSetOfClauses(Writer);
            solveThirdSetOfClauses(Writer);

            Writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void decipherOracleAnswer() throws IOException {
        BufferedReader fileReader = new BufferedReader(new FileReader("sat.sol"));

        result = fileReader.readLine();
        /*
         * If it is not necessary and the answer is "FALSE" then
         * solutionSize and solutionElem will not be used
         */
        if (result.equals("True")) {
            // solutionSize is not being used since it does exactly
            // what solutionElem.length does
            solutionSize = fileReader.readLine();
            solutionElem = fileReader.readLine().split(" ");
        }
    }

    @Override
    public void writeAnswer() throws IOException {
        if (result.equals("False")) {
            System.out.println(result);
        } else {
            System.out.println(result);
            ArrayList<Integer> clique = filterClique(solutionElem);

            /*
             * Find each element from clique from the original matrix
             * and print the position of the node from the original Graph which
             * is stored in the "j" index
             */
            for (int i = 0; i < K; i++) {
                for (int j = 1; j <= N; j++) {
                    if (clique.get(i).equals(myMatrix[i + 1][j])) {
                        System.out.print(j + " ");
                    }
                }
            }
        }
    }

    public void solveFirstSetOfClauses(FileWriter Writer) throws IOException {
        /*
         * First set of clauses
         * a) from Lost_Paper.jpeg
         * for each 1 <= i <= k, Xiv does exist
         */
        for (int i = 1; i <= K; i++) {
            for (int v = 1; v <= N; v++) {
                Writer.write(myMatrix[i][v] + " ");
            }
            Writer.write(0 + "\n");
        }
    }

    public void solveSecondSetOfClauses(FileWriter Writer) throws IOException {
        /*
         * Second set of clauses
         * b) from Lost_Paper.jpeg
         * for each i != j and v!= w => -Xiv V -Xjw
         */
        for (int i = 1; i <= K; i++) {
            for (int j = 1; j <= K; j++) {
                for (int v = 1; v <= N; v++) {
                    for (int w = 1; w <= N; w++) {
                        if (adjacencyMap.get(v) != null) {
                            if (!adjacencyMap.get(v).contains(w) && i != j) {
                                Writer.write(-myMatrix[i][v] + " " + -myMatrix[j][w] + " " + 0 + "\n");
                            }
                        }
                    }
                }
            }
        }
    }

    public void solveThirdSetOfClauses(FileWriter Writer) throws IOException {
        /*
         * Third set of clauses
         * c) from Lost_Paper.jpeg
         * for each i != j, and v from V => -Xiv V -Xjv
         */
        for (int i = 1; i <= K; i++) {
            for (int j = 1; j <= K; j++) {
                if (i != j) {
                    for (int v = 1; v <= N; v++) {
                        Writer.write(-myMatrix[i][v] + " " + -myMatrix[j][v] + " " + 0 + "\n");
                    }
                }
            }
        }

        /*
         * Third set of clauses
         * c) from Lost_Paper.jpeg
         * for each i and for each v != w => -Xiv V -Xiw
         */
        for (int i = 1; i <= K; i++) {
            for (int v = 1; v <= N; v++) {
                for (int w = 1; w <= N; w++) {
                    if (v != w) {
                        Writer.write(-myMatrix[i][v] + " " + -myMatrix[i][w] + " " + 0 + "\n");
                    }
                }
            }
        }
    }

    public void mapTheMatrix(int[][] myMatrix) {
        /*
         * The way I encode my matrix is the following:
         * ex: N = 7 nodes
         *     y11 = (1-1)*7 + 1 = 1
         *     y43 = (4-1)*7 + 3 = 24
         *     y77 = (7-1)*7 + 7 = 49
         *     To retrieve the value we can simply divide the encoded value by N
         *     i = enc_val / n + 1 and j = enc_val % n
         *     if enc_val % n == 0 => index j = n, index i = enc_val / n
         * ex2:
         *     y77 = 49
         *     i = 49 / 7 + 1 = 7;
         *     j = 49 % 7 = 0 => j = 7
         * ex3:
         *     y43 = 24
         *     i = 24 / 7 = 3 + 1 => i = 4
         *     j = 24 % 7 = 3
         */
        for (int i = 1; i <= K; i++) {
            for (int j = 1; j <= N; j++) {
                myMatrix[i][j] = (i - 1) * N + j;
            }
        }
    }

    public ArrayList<Integer> filterClique(String[] solutionElem) {
        /*
         * Populate clique with the positive numbers from sat.sol
         * It filters the solutionElem list, removing all negative
         * values
         */
        ArrayList<Integer> clique = new ArrayList<>();

        for (int i = 0; i < solutionElem.length; i++) {
            if (Integer.parseInt(solutionElem[i]) > 0) {
                clique.add(Integer.parseInt(solutionElem[i]));
            }
        }
        return clique;
    }
}