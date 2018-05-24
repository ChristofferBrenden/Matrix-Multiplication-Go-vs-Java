package main

import (
	"bufio"
	"fmt"
	"log"
	"os"
	"runtime"
	"runtime/debug"
	"strconv"
	"strings"
	"sync"
	"time"
)

func multiply(A [][]int, B [][]int) [][]int {

	sizeA := len(A)
	sizeB := len(B)

	n := make([][]int, sizeA)
	for i := range n {
		n[i] = make([]int, sizeB)
	}

	for i := 0; i < sizeA; i++ {
		for k := 0; k < sizeB; k++ {
			temp := A[i][k]
			for j := 0; j < sizeB; j++ {
				n[i][j] += temp * B[k][j]
			}
		}
	}
	return n
}

func splitMatrix(nrOfThreads int, matrix [][]int) (matrixes [][][]int) {

	splitter := len(matrix) / nrOfThreads

	for i := 0; i < nrOfThreads; i++ {
		matrixes = append(matrixes, matrix[splitter*i:(splitter*(i+1))])
	}

	return
}

func multiplyStuff(finalMatrix *[][][]int, matrix1 [][]int, matrix2 [][]int, i int) {
	(*finalMatrix)[i] = multiply(matrix1, matrix2)
}

func readFile(filePath string) (matrix1 [][]int, matrix2 [][]int) {
	file, err := os.Open(filePath)
	if err != nil {
		log.Fatal(err)
	}
	defer file.Close()

	var temp []int
	matrixNr := 1
	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		words := strings.Fields(scanner.Text())
		if len(words) != 0 {
			for _, element := range words {
				i, err := strconv.Atoi(element)
				if err != nil {
					log.Fatal(err)
				}
				temp = append(temp, i)
			}
			if matrixNr == 1 {
				matrix1 = append(matrix1, temp)
			} else {
				matrix2 = append(matrix2, temp)
			}
			temp = nil
		} else {
			matrixNr = 2
		}

	}

	if err := scanner.Err(); err != nil {
		log.Fatal(err)
	}
	return
}

func main() {
	file := os.Args[1]
	nrOfThreads, err := strconv.Atoi(os.Args[2])
	if err != nil {
		log.Fatal("USAGE: " + os.Args[0] + " <file> <nrOfThreads>")
	}

	debug.SetGCPercent(-1)

	if nrOfThreads <= 0 {
		runtime.GOMAXPROCS(1)
	} else if nrOfThreads >= 16 {
		runtime.GOMAXPROCS(8)
	} else {
		runtime.GOMAXPROCS(nrOfThreads)
	}

	var wg sync.WaitGroup
	finishedMatrix := make([][][]int, nrOfThreads)

	matrix1, matrix2 := readFile(file)

	if len(matrix1) != len(matrix2) || (nrOfThreads != 0 && len(matrix1)%nrOfThreads != 0) {
		log.Fatal("USAGE: " + os.Args[0] + " <file> <nrOfThreads>")
	}

	var start int64

	if nrOfThreads == 0 {
		start = time.Now().UnixNano()
		multiply(matrix1, matrix2)
	} else {
		matrixes := splitMatrix(nrOfThreads, matrix1)

		start = time.Now().UnixNano()
		for i := 0; i < nrOfThreads; i++ {
			wg.Add(1)
			go func(index int) {
				defer wg.Done()
				multiplyStuff(&finishedMatrix, matrixes[index], matrix2, index)
			}(i)
		}

		wg.Wait()
	}

	end := time.Now().UnixNano()
	fmt.Printf("Execution took %d ns\n", (end - start))
	runtime.GC()
}
