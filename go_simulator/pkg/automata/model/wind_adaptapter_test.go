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
		if ogMatrix.Expand(i)[i][i] != centralValue {
			t.Errorf("When exapanding wind matrix to radius %d Expected %f, got %f", i, centralValue, ogMatrix.Expand(i)[i][i])
		}
	}
}

func TestGettingValueByRelativePos(t *testing.T) {
	ogMatrix := WindMatrix{
		{1, 2, 3},
		{4, 5, 6},
		{7, 8, 9},
	}
	if ogMatrix.ByRelativeNeighborhoodPosition(0, 0) != 5 {
		t.Errorf("Expected 5, got %f", ogMatrix.ByRelativeNeighborhoodPosition(0, 0))
	}
	if ogMatrix.ByRelativeNeighborhoodPosition(1, 1) != 9 {
		t.Errorf("Expected 9, got %f", ogMatrix.ByRelativeNeighborhoodPosition(1, 1))
	}
	if ogMatrix.ByRelativeNeighborhoodPosition(-1, -1) != 1 {
		t.Errorf("Expected 1, got %f", ogMatrix.ByRelativeNeighborhoodPosition(-1, -1))
	}
	if ogMatrix.ByRelativeNeighborhoodPosition(-1, 1) != 3 {
		t.Errorf("Expected 3, got %f", ogMatrix.ByRelativeNeighborhoodPosition(-1, 1))
	}
	if ogMatrix.ByRelativeNeighborhoodPosition(1, -1) != 7 {
		t.Errorf("Expected 7, got %f", ogMatrix.ByRelativeNeighborhoodPosition(1, -1))
	}
}
