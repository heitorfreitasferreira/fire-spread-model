package model

import (
	"github.com/heitorfreitasferreira/fireSpreadSimultor/utils"
)

type WindMatrix [][]float64

func (ogMatrix WindMatrix) Expand(newRadius int) WindMatrix {
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

func (m WindMatrix) ByRelativeNeighborhoodPosition(i, j int) float64 {
	height := len(m)
	width := len(m[0])

	middle := height / 2

	i = utils.Clamp(i, -height, height)
	j = utils.Clamp(j, -width, width)
	return m[middle+i][middle+j]
}
