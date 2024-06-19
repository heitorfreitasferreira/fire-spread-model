package model

import "testing"

func TestPostionsToLookRadius1_2_3(t *testing.T) {
	testCases := []struct {
		radius   int
		expected [][]int
	}{
		{
			radius: 1,
			expected: [][]int{
				{-1, -1}, {-1, 0}, {-1, 1},
				{0, -1}, {0, 1},
				{1, -1}, {1, 0}, {1, 1},
			},
		},
		{
			radius: 2,
			expected: [][]int{
				{-2, -2}, {-2, -1}, {-2, 0}, {-2, 1}, {-2, 2},
				{-1, -2}, {-1, 2},
				{0, -2}, {0, 2},
				{1, -2}, {1, 2},
				{2, -2}, {2, -1}, {2, 0}, {2, 1}, {2, 2},
			},
		},
		{
			radius: 3,
			expected: [][]int{
				{-3, -3}, {-3, -2}, {-3, -1}, {-3, 0}, {-3, 1}, {-3, 2}, {-3, 3},
				{-2, -3}, {-2, 3},
				{-1, -3}, {-1, 3},
				{0, -3}, {0, 3},
				{1, -3}, {1, 3},
				{2, -3}, {2, 3},
				{3, -3}, {3, -2}, {3, -1}, {3, 0}, {3, 1}, {3, 2}, {3, 3},
			},
		},
	}

	for _, tc := range testCases {
		got := positionsToLook(tc.radius)

		for _, exp := range tc.expected {
			found := false
			for _, g := range got {
				if exp[0] == g[0] && exp[1] == g[1] {
					found = true
					break
				}
			}
			if !found {
				t.Errorf("Expected %v to be in the result, but it was not found", exp)
			}
		}
	}
}
func TestPostionsAreTuples(t *testing.T) {
	for radius := 0; radius < 10; radius++ {
		got := positionsToLook(radius)
		for _, g := range got {
			if len(g) != 2 {
				t.Errorf("Expected a tuple, got len %v", g)
			}
		}
	}
}

func TestPositionToLookNever0_0(t *testing.T) {
	for radius := 0; radius < 10; radius++ {
		got := positionsToLook(radius)
		for _, tuple := range got {
			if tuple[0] == 0 && tuple[1] == 0 {
				t.Errorf("Expected not to have 0,0 in the result, but it was found with radius %v", radius)
			}
		}
	}
}
