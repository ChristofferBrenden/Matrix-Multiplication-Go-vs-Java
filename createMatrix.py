import random
random.seed(1234)

def createRandomMatrix(n):
    maxVal = 1000
    matrix = []
    for i in range(n):
        matrix.append([random.randint(0,maxVal) for el in range(n)])
    return matrix

def saveMatrix(matrixA, matrixB, filename):
    f = open(filename, 'w')
    for i, matrix in enumerate([matrixA, matrixB]):
        if i != 0:
            f.write("\n")
        for line in matrix:
            f.write("\t".join(map(str, line)) + "\n")
n = 4096
matrixA = createRandomMatrix(n)
matrixB = createRandomMatrix(n)
saveMatrix(matrixA, matrixB, "4096.in")
