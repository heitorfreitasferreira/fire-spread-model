package model

import (
	"math"

	"github.com/heitorfreitasferreira/fireSpreadSimultor/utils"
)

type WindMatrix [][]float64

var verticalHorizontalDecai float64 = .5
var diagonalDecai float64 = verticalHorizontalDecai * math.Sqrt2

func (ogMatrix WindMatrix) Expand(newRadius int) WindMatrix {
	if len(ogMatrix) != 3 {
		panic("model:WindMatrix.Expand(int) -> Length of ogMatrix must be 3")
	}
	new := ogMatrix.expand(newRadius)
	new.fillExpandedArea(1, newRadius)
	return new
}
func (ogMatrix WindMatrix) expand(newRadius int) WindMatrix {
	if newRadius < 2 {
		return ogMatrix
	}
	newMatrix := make(WindMatrix, newRadius*2+1)
	for i := range newMatrix {
		newMatrix[i] = make([]float64, newRadius*2+1)
	}
	for i := 0; i < len(ogMatrix); i++ {
		for j := 0; j < len(ogMatrix[0]); j++ {
			newMatrix[newRadius-1+i][newRadius-1+j] = ogMatrix[i][j]
		}
	}
	return newMatrix
}

func (m WindMatrix) GetByRelativeNeighborhoodPosition(i, j int) float64 {
	height := len(m)
	width := len(m[0])

	middle := height / 2

	i = utils.Clamp(i, -height, height)
	j = utils.Clamp(j, -width, width)
	return m[middle+i][middle+j]
}

func (m WindMatrix) SetByRelativeNeighborhoodPosition(i, j int, value float64) {
	height := len(m)
	width := len(m[0])

	middle := height / 2

	i = utils.Clamp(i, -height, height)
	j = utils.Clamp(j, -width, width)
	m[middle+i][middle+j] = value
}

func (m *WindMatrix) fillExpandedArea(currentRadius, goalRadius int) {
	if goalRadius < currentRadius {
		panic("model:WindMatrix.fillExpandedArea(int, int) -> New radius must be greater than old radius when filling expanded area")
	}
	if goalRadius == currentRadius {
		return
	}

	// Fill vertical and horizontal
	for i := 0; i <= currentRadius; i++ {

		m.SetByRelativeNeighborhoodPosition(i, currentRadius+1, m.GetByRelativeNeighborhoodPosition(i, currentRadius)*verticalHorizontalDecai)
		m.SetByRelativeNeighborhoodPosition(i, -currentRadius-1, m.GetByRelativeNeighborhoodPosition(i, -currentRadius)*verticalHorizontalDecai)

		m.SetByRelativeNeighborhoodPosition(currentRadius+1, i, m.GetByRelativeNeighborhoodPosition(currentRadius, i)*verticalHorizontalDecai)
		m.SetByRelativeNeighborhoodPosition(-currentRadius-1, i, m.GetByRelativeNeighborhoodPosition(-currentRadius, i)*verticalHorizontalDecai)
	}

	// Fill corners
	m.SetByRelativeNeighborhoodPosition(currentRadius+1, currentRadius+1, m.GetByRelativeNeighborhoodPosition(currentRadius, currentRadius)*diagonalDecai)
	m.SetByRelativeNeighborhoodPosition(-currentRadius-1, -currentRadius-1, m.GetByRelativeNeighborhoodPosition(-currentRadius, -currentRadius)*diagonalDecai)
	m.SetByRelativeNeighborhoodPosition(currentRadius+1, -currentRadius-1, m.GetByRelativeNeighborhoodPosition(currentRadius, -currentRadius)*diagonalDecai)
	m.SetByRelativeNeighborhoodPosition(-currentRadius-1, currentRadius+1, m.GetByRelativeNeighborhoodPosition(-currentRadius, currentRadius)*diagonalDecai)

	// Fill the next layer
	m.fillExpandedArea(currentRadius+1, goalRadius)
}
