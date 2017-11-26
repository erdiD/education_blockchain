package main

import "fmt"
import "time"

func main() {

	//Switch
	fmt.Println("\n6. Switch")
	i := 2
	fmt.Print("Write ", i, " as ")
	switch i {
	case 1:
		fmt.Println("one")
	case 2:
		fmt.Println("two")
	case 3:
		fmt.Println("three")
	}

	switch time.Now().Weekday() {
	case time.Saturday, time.Sunday:
		fmt.Println("It's weekend")
	default:
		fmt.Println("It's weekday")
	}

	t := time.Now()
	switch {
	case t.Hour() < 12:
		fmt.Println("It's before noon")
	default:
		fmt.Println("It's after noon")
	}

	whatAmI := func(i interface{}) {
		switch t := i.(type) {
		case bool:
			fmt.Println("BOOL")
		case int:
			fmt.Println("INT")
		default:
			fmt.Println("???", t)
		}
	}
	whatAmI(true)
	whatAmI(1)
	whatAmI("hey")

	//Arrays
	fmt.Println("\n7. Arrays")
	var a [5]int
	fmt.Println("emp:", a)

	a[4] = 100
	fmt.Println("set:", a)
	fmt.Println("get:", a[4])

	fmt.Println("len:", len(a))

	b := [5]int{1, 2, 3, 4, 5}
	fmt.Println("dcl:", b)

	var twoD [2][3]int
	for i := 0; i < 2; i++ {
		for j := 0; j < 3; j++ {
			twoD[i][j] = i + j
		}
	}
	fmt.Println("2d: ", twoD)

	//Slices
	fmt.Println("\n8. Slices")

	s := make([]string, 3)
	fmt.Println("emp:", s)

	s[0] = "a"
	s[1] = "b"
	s[2] = "c"
	fmt.Println("set:", s)
	fmt.Println("get:", s[2])

	fmt.Println("len:", len(s))

	s = append(s, "d")
	s = append(s, "e", "f")
	fmt.Println("apd:", s)

	c := make([]string, len(s))
	copy(c, s)
	fmt.Println("cpy:", c)

	l := s[2:5]
	fmt.Println("sl1:", l)

	l = s[:5]
	fmt.Println("sl2:", l)

	l = s[2:]
	fmt.Println("sl3", l)

	x := []string{"g", "h", "i"}
	fmt.Println("dcl:", x)

	twoDSlice := make([][]int, 3)
	for i := 0; i < 3; i++ {
		innerLen := i + 1
		twoDSlice[i] = make([]int, innerLen)
		for j := 0; j < innerLen; j++ {
			twoDSlice[i][j] = i + j
		}
	}
	fmt.Println("2d:", twoDSlice)

}
