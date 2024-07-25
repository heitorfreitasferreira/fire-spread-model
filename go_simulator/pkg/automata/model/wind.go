package model

import (
	"math"

	"github.com/heitorfreitasferreira/fireSpreadSimultor/utils"
)

type WindDirection string

const (
	N  WindDirection = "N"
	NE WindDirection = "NE"
	E  WindDirection = "E"
	SE WindDirection = "SE"
	S  WindDirection = "S"
	SW WindDirection = "SW"
	W  WindDirection = "W"
	NW WindDirection = "NW"
)

type WindParams struct {
	Coef      float64
	MultBase  float64
	Decai     float64
	Direction WindDirection
	Radius    int
}

func (params WindParams) CreateMatrix() WindMatrix {
	var matrizVento WindMatrix = make([][]float64, 3)
	for i := range matrizVento {
		matrizVento[i] = make([]float64, 3)
	}
	matrizVento[1][1] = 0
	matrizVento[0][0] = params.Coef * (params.MultBase - (params.Decai * 1))
	matrizVento[0][1] = params.Coef * (params.MultBase - (params.Decai * 0))
	matrizVento[0][2] = params.Coef * (params.MultBase - (params.Decai * 1))
	matrizVento[1][0] = params.Coef * (params.MultBase - (params.Decai * 2))
	matrizVento[1][2] = params.Coef * (params.MultBase - (params.Decai * 2))
	matrizVento[2][0] = params.Coef * (params.MultBase - (params.Decai * 3))
	matrizVento[2][1] = params.Coef * (params.MultBase - (params.Decai * 4))
	matrizVento[2][2] = params.Coef * (params.MultBase - (params.Decai * 3))

	rotationTimesPerDirection := map[WindDirection]int{
		S:  0,
		SW: 1,
		W:  2,
		NW: 3,
		N:  4,
		NE: 5,
		E:  6,
		SE: 7,
	}
	rotate(matrizVento, rotationTimesPerDirection[params.Direction])
	return matrizVento.Expand(params.Radius)
}

func rotate(matrix [][]float64, times int) {
	for i := 0; i < times; i++ {
		rotateOnce(matrix)
	}
}

// Retorna as posições dos n maiores valores da matriz (posição de onde o vento está vindo)
func NBigestPositions(n int, matrix [][]float64) []utils.Vector2D[int] {
	biggests := make([]utils.Vector2D[int], n)
	avaliables := make(map[utils.Vector2D[int]]bool)
	for i := 0; i < len(matrix); i++ {
		for j := 0; j < len(matrix[i]); j++ {
			avaliables[utils.Vector2D[int]{I: i, J: j}] = true
		}
	}
	for i := 0; i < n; i++ {
		biggest := utils.Vector2D[int]{I: 0, J: 0}
		biggestValue := 0.0
		for pos, isAv := range avaliables {
			if !isAv {
				continue
			}
			if matrix[pos.I][pos.J] > biggestValue {
				biggest = pos
				biggestValue = matrix[pos.I][pos.J]
			}
		}
		biggests[i] = biggest
		avaliables[biggest] = false
	}
	return biggests
}

// Retorna as posições dos n menores valores da matriz (posição de onde o vento está indo)
func NLowestPositions(n int, matrix [][]float64) []utils.Vector2D[int] {
	lowests := make([]utils.Vector2D[int], n)
	avaliables := make(map[utils.Vector2D[int]]bool)
	for i := 0; i < len(matrix); i++ {
		for j := 0; j < len(matrix[i]); j++ {
			avaliables[utils.Vector2D[int]{I: i, J: j}] = true
		}
	}
	for i := 0; i < n; i++ {
		lowest := utils.Vector2D[int]{I: 0, J: 0}
		lowestValue := math.MaxFloat64
		for pos, isAv := range avaliables {
			if !isAv {
				continue
			}
			if matrix[pos.I][pos.J] < lowestValue {
				lowest = pos
				lowestValue = matrix[pos.I][pos.J]
			}
		}
		lowests[i] = lowest
		avaliables[lowest] = false
	}
	return lowests
}

func rotateOnce(matrizVento [][]float64) {
	size := 3
	row, col := 0, 0
	m, n := size, size
	var prev, curr float64

	for row < m && col < n {

		if row+1 == m || col+1 == n {
			break
		}

		prev = matrizVento[row+1][col]

		for i := col; i < n; i++ {
			curr = matrizVento[row][i]
			matrizVento[row][i] = prev
			prev = curr
		}
		row++

		for i := row; i < m; i++ {
			curr = matrizVento[i][n-1]
			matrizVento[i][n-1] = prev
			prev = curr
		}
		n--

		if row < m {
			for i := n - 1; i >= col; i-- {
				curr = matrizVento[m-1][i]
				matrizVento[m-1][i] = prev
				prev = curr
			}
		}
		m--

		if col < n {
			for i := m - 1; i >= row; i-- {
				curr = matrizVento[i][col]
				matrizVento[i][col] = prev
				prev = curr
			}
		}
		col++
	}
}
