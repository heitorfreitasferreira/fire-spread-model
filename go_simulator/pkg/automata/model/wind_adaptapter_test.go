package model

import "testing"

func TestWindMatrixExpansion(t *testing.T) {
	centralValue := 1.0
	borderValue := 0.5
	ogMatrix := WindMatrix{
		{borderValue, borderValue, borderValue},
		{borderValue, centralValue, borderValue},
		{borderValue, borderValue, borderValue},
	}

	for i := 2; i < 100; i++ {
		if ogMatrix.expand(i)[i][i] != centralValue {
			t.Errorf("When exapanding wind matrix to radius %d Expected %f, got %f", i, centralValue, ogMatrix.expand(i)[i][i])
		}
	}
}

func TestGettingValueByRelativePos(t *testing.T) {
	ogMatrix := WindMatrix{
		{1, 2, 3},
		{4, 5, 6},
		{7, 8, 9},
	}
	if ogMatrix.GetByRelativeNeighborhoodPosition(0, 0) != 5 {
		t.Errorf("Expected 5, got %f", ogMatrix.GetByRelativeNeighborhoodPosition(0, 0))
	}
	if ogMatrix.GetByRelativeNeighborhoodPosition(1, 1) != 9 {
		t.Errorf("Expected 9, got %f", ogMatrix.GetByRelativeNeighborhoodPosition(1, 1))
	}
	if ogMatrix.GetByRelativeNeighborhoodPosition(-1, -1) != 1 {
		t.Errorf("Expected 1, got %f", ogMatrix.GetByRelativeNeighborhoodPosition(-1, -1))
	}
	if ogMatrix.GetByRelativeNeighborhoodPosition(-1, 1) != 3 {
		t.Errorf("Expected 3, got %f", ogMatrix.GetByRelativeNeighborhoodPosition(-1, 1))
	}
	if ogMatrix.GetByRelativeNeighborhoodPosition(1, -1) != 7 {
		t.Errorf("Expected 7, got %f", ogMatrix.GetByRelativeNeighborhoodPosition(1, -1))
	}
}

func TestFillingWindMatrixValues(t *testing.T) {
	ogMatrix := WindMatrix{
		{1, 1, 1},
		{1, 0, 1},
		{1, 1, 1},
	}
	newMatrix := ogMatrix.expand(2)
	newMatrix.fillExpandedArea(1, 2)

	if newMatrix.GetByRelativeNeighborhoodPosition(2, 2) != ogMatrix.GetByRelativeNeighborhoodPosition(1, 1)*diagonalDecai {
		t.Errorf("Expected %f, got %f", ogMatrix.GetByRelativeNeighborhoodPosition(1, 1)*diagonalDecai, newMatrix.GetByRelativeNeighborhoodPosition(2, 2))
	}

	if newMatrix.GetByRelativeNeighborhoodPosition(0, 2) != ogMatrix.GetByRelativeNeighborhoodPosition(0, 1)*verticalHorizontalDecai {
		t.Errorf("Expected %f, got %f", ogMatrix.GetByRelativeNeighborhoodPosition(0, 1)*verticalHorizontalDecai, newMatrix.GetByRelativeNeighborhoodPosition(0, 2))

	}

	newMatrix = ogMatrix.expand(3)
	newMatrix.fillExpandedArea(1, 3)

	if newMatrix.GetByRelativeNeighborhoodPosition(3, 3) != ogMatrix.GetByRelativeNeighborhoodPosition(1, 1)*diagonalDecai*diagonalDecai {
		t.Errorf("Expected %f, got %f", ogMatrix.GetByRelativeNeighborhoodPosition(1, 1)*diagonalDecai*diagonalDecai, newMatrix.GetByRelativeNeighborhoodPosition(3, 3))
	}

	if newMatrix.GetByRelativeNeighborhoodPosition(0, 3) != ogMatrix.GetByRelativeNeighborhoodPosition(0, 1)*verticalHorizontalDecai*verticalHorizontalDecai {
		t.Errorf("Expected %f, got %f", ogMatrix.GetByRelativeNeighborhoodPosition(0, 1)*verticalHorizontalDecai*verticalHorizontalDecai, newMatrix.GetByRelativeNeighborhoodPosition(0, 3))
	}

}
