package model

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

type MatrixParams struct {
	Coef      float64
	MultBase  float64
	Decai     float64
	Direction WindDirection
}

func (params MatrixParams) CreateMatrix() [][]float64 {
	matrizVento := make([][]float64, 3)
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

	return matrizVento
}

func rotate(matrix [][]float64, times int) {
	for i := 0; i < times; i++ {
		rotateOnce(matrix)
	}
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
