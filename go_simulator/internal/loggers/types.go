package loggers

type OutputType string

const (
	SingleJson                OutputType = "single-json"
	FolderOfTxt               OutputType = "multiple-txt"
	ViewSparkData             OutputType = "spark-data"
	ViewInConsole             OutputType = "console"
	ViewInConsoleEndOfLattice OutputType = "console-end"
)
