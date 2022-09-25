Name: Maciuca Alexandru Petru
Group: 324CA
Course: AA
Title: Reduceri polinomiale

January 2021

# About the code
    Associating and modeling practical applications with NP problems.
    Implementation of polynomial reductions to the SAT  problem.

## Structure
    The homework is divided in several classes which extends Task class and
    solve each of the problems. (Retele.java, Reclame.java, Registre.java)
    
    The way in which I have encoded my nodes is explained in the
    mappingFunction of each class.

Retele.java
    For this task, I have used all the recommended reductions that were specified
in the document one by one(a, b and c).

    First set of reductions makes sure that if there is a clique of size K in a given graph G,
    then there must be exactly one vertex v in that clique for each i between 1 and K.
    Formally:
            for each i | 1 <= i <= k, Xiv does exist

    Second set of reductions makes sure that every pair of two nodes selected in the clique,
    is being linked.
    Formally:
            for each i,j i!=j and for each v,w v!=w;
            => -Xiv V Xjw

    Third set of reductions makes sure that a node can not be both the i-th and j-th vertex
    in the clique.(every node from a clique is unique)
    Formally:
            for each i,j i !=j and for each v from V => -Xiv V -Xjv
            for each i and for each v,w v!=w => -Xiv V -Xiw

Reclame.java
    For this task, I have used also all the recommended reductions that were specified
    in the document one by one.

    First and Third set of reductions are identical to the ones used for solving Retele.java

    Second set of reductions has been slightly modified to suit better the problem. I have used
    instead of clause b), the third listed clause from 1.3.2 Reducere Vertex Cover <=p CNF-SAT which
    forces that every link has at least one vertex in the clique so the removal of such nodes would
    completely isolate the others.
    Formally:
            for each v,w v!=w and for each i => Xiv V Xiw

Registre.java
    For this task, I have used only two reductions since one registry can hold multiple variables.
    (those two reductions seem on paper to be sufficient)

    First set of reductions is similar to a) from Lost_Paper.jpeg but it is adapted to work list from
    columns instead of rows.
    
    Same change has been done to the second set of reductions which is similar to c) from Lost_Paper.jpeg.

### How it works

    -call readProblemData()
    Most of the tasks use the following notations:
    K = number of elements from the clique(the restrained set of nodes that we want to obtain)
    N = number of nodes from the original graph G
    M = number of relations between nodes from the initial graph G

    -call formulateOracleQuestions()
    A adjacency matrix is created in order to save all the M relations.
    And an additional matrix is created called myMatrix that stores my encoded values.
    There are n*k encoded values that are being used with the constraints in order to solve
    the SAT problem.

    -call decipherOracleAnswer()
    Transforms a sat.cnf file to a sat.sol file that I have to decipher.
    These constraints mean that if and only if  they are fully satisfied, the problem could return
    "TRUE". Therefore, the problem can be mapped to a SAT problem, considering that for any inputs
    (K,N,M) with "TRUE" answer, we find an entry for the SAT problem with the same value of truth,
    and vice versa, concluding that the problem is reduced polynomially to SAT.

    -call writeAnswer()
    After deciphering the sat.sol file, the specific output has to be written.

    Additional utility functions can be called such as: mapTheMatrix, filterClique,
    solveFirstSetOfClauses, solveSecondSetOfClauses, solveThirdSetOfClauses.

#### Difficulties
    I had a lot of trouble deciding in which language to implement my homework, but since
    the whole semester I have used java, it has been more appealing in the end.

##### Bibliography
    Lost_Paper.jpeg from official homework pdf
    The homework pdf
    Polynomially Reductions laboratory (Seminar 8 Reduceri Polinomiale)


