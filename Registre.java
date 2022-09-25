import java.io.*;
import java.util.*;

class Registre extends Task {
    public Map<Integer, ArrayList<Integer>> adjacencyMap = new LinkedHashMap<>();
    public int N;
    public int M;
    public int K;
    public int[][] myMatrix; // it helps with the mapping of the elements with a specific formula
    public String result;
    public String[] solutionElem;
    public String solutionSize;

    public static void main(String[] args) throws IOException, InterruptedException {
        Registre registre = new Registre();
        registre.solve();
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
         * N = number of variables
         * M = number of links between the variables
         * K = number of registers
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
            int numOfClauses = K + K * N * N;
            Writer.write("p cnf ");
            Writer.write(N + " " + numOfClauses + "\n");

            myMatrix = new int[N + 1][K + 1];
            mapTheMatrix(myMatrix);
            solveFirstSetOfClauses(Writer);
            solveSecondSetOfClauses(Writer);

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
            for (int i = 1; i <= N; i++) {
                for (int j = 1; j <= K; j++) {
                    for (Integer cliqueElem : clique) {
                        if (cliqueElem.equals(myMatrix[i][j])) {
                            System.out.print(j + " ");
                        }
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
        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= K; j++) {
                myMatrix[i][j] = (i - 1) * N + j;
            }
        }
    }

    public void solveFirstSetOfClauses(FileWriter Writer) throws IOException {
        /*
         * First set of clauses
         * a) from Lost_Paper.jpeg
         * for each 1 <= v <= n, Xvi does exist
         * [modified clause for columns]
         */
        for (int v = 1; v <= N; v++) {
            for (int i = 1; i <= K; i++) {
                Writer.write(myMatrix[v][i] + " ");
            }
            Writer.write(0 + " " + "\n");
        }
    }

    public void solveSecondSetOfClauses(FileWriter Writer) throws IOException {
        /*
         * Second set of clauses
         * c) from Lost_Paper.jpeg
         * for each i and for each v connected to w => -Xvi V -Xwi
         * [modified clause for columns]
         */
        for (int i = 1; i <= K; i++) {
            for (int v = 1; v <= N; v++) {
                for (int w = 1; w <= N; w++) {
                    if (adjacencyMap.get(v) != null && adjacencyMap.get(v).contains(w)) {
                        Writer.write(-myMatrix[v][i] + " " + -myMatrix[w][i] + " " + 0 + "\n");
                    }
                }
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