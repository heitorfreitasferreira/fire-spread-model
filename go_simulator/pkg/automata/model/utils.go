package model

func calculateHumidityInfluence(humidity float32) float64 {
	switch {
	case humidity > 0.0 && humidity <= 0.25:
		return 1.5
	case humidity > 0.25 && humidity <= 0.5:
		return 1.0
	case humidity > 0.5 && humidity <= 0.75:
		return 0.8
	case humidity > 0.75 && humidity <= 1.0:
		return 0.6
	default:
		return 0
	}
}
