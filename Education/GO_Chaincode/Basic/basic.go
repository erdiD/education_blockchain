package main

import (
	"fmt"
	"math"
)

const s string = "constant"

func main() {
	//Print Hello World
	fmt.Println("1. Hello World")

	//Values
	fmt.Println("\n2. Values")
	fmt.Println("go" + "lang")
	fmt.Println("1+1 =", 1+1)
	fmt.Println("7.0/3.0", 7.0/3.0)
	fmt.Println(true && false)
	fmt.Println(true || false)
	fmt.Println(!true)

	//Variables
	fmt.Println("\n3. Variables")
	var a string = "initial"
	fmt.Println(a)
	var b, c int = 1, 2
	fmt.Println(b, c)
	var d = true
	fmt.Println(d)
	var e int
	fmt.Println(e)

	//Constants
	fmt.Println("\n4. Constants")
	fmt.Println(s)
	const n = 50000000
	const d2 = 3e20 / n
	fmt.Println(d2)
	fmt.Println(int64(d2))
	fmt.Println(math.Sin(n))

	//For
	fmt.Println("\n5. For")
	i := 1
	for i <= 3 {
		fmt.Println(i)
		i = i + 1
	}
	for j := 7; j <= 9; j++ {
		fmt.Println(j)
	}
	for {
		fmt.Println("loop")
		break
	}
	for n := 0; n <= 5; n++ {
		if n%2 == 0 {
			continue
		}
		fmt.Println(n)
	}

	//If/Else
	fmt.Println("\n ")

}
