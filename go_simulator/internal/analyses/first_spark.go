package analyses

import (
	"fmt"
	"reflect"

	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/lattice/cell"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/pkg/automata/model"
	"github.com/heitorfreitasferreira/fireSpreadSimultor/utils"
)

type RiverAnalysis []firstSparkResult

type firstSparkResult struct {
	model.WindDirection
	Radius                        int
	utils.Vector3D[int]               // Posição onde primeiro teve fogo a oeste do rio
	TotalAreaBurned               int // Quantidade de células queimadas
	TotalAreaBurnedWestOfTheRiver int // Quantidade de células queimadas a oeste do rio
}

type RiverAnalysisInput struct {
	model.WindDirection
	Radius int
	lattice.SimulationResult
}

func NewRiverAnalysis(in []RiverAnalysisInput) RiverAnalysis {
	results := make(RiverAnalysis, 0)
	for _, input := range in {
		results = append(results, input.analyse())
	}
	return results
}

func (r firstSparkResult) toString(separator rune) string {
	v := reflect.ValueOf(r)
	numFields := v.NumField()
	result := ""
	for i := 0; i < numFields; i++ {
		field := v.Field(i)
		result += fmt.Sprintf("%v", field.Interface()) + string(separator)
	}
	result = result[:len(result)-1]
	return result
}

func (in RiverAnalysisInput) analyse() firstSparkResult {
	areaburnedchan := make(chan int)
	areaburnedwestchan := make(chan int)
	firstFirePosition := make(chan utils.Vector3D[int])
	defer close(areaburnedchan)
	defer close(areaburnedwestchan)
	defer close(firstFirePosition)

	go func() {
		areaburnedchan <- in.totalAreaBurned()
	}()

	go func() {
		areaburnedwestchan <- in.totalAreaBurnedWestOfTheRiver()
	}()

	go func() {
		firstFirePosition <- in.firstFirePosition()
	}()

	return firstSparkResult{
		WindDirection:                 in.WindDirection,
		Radius:                        in.Radius,
		Vector3D:                      <-firstFirePosition,
		TotalAreaBurned:               <-areaburnedchan,
		TotalAreaBurnedWestOfTheRiver: <-areaburnedwestchan,
	}
}

func (r RiverAnalysisInput) totalAreaBurnedWestOfTheRiver() int {
	cells := 0
	lastIndex := len(r.SimulationResult) - 1
	for i := 0; i < len(r.SimulationResult[lastIndex]); i++ {
		for j := len(r.SimulationResult[lastIndex][i]) / 2; j >= 0; j-- {
			if r.SimulationResult[lastIndex][i][j].IsFire() || r.SimulationResult[lastIndex][i][j] == cell.ASH {
				cells++
			}
		}
	}

	return cells
}

func (r RiverAnalysisInput) firstFirePosition() utils.Vector3D[int] {
	for iter := 0; iter < len(r.SimulationResult); iter++ {
		for i := 0; i < len(r.SimulationResult[iter]); i++ {
			for j := len(r.SimulationResult[iter][i]) / 2; j >= 0; j-- {
				if r.SimulationResult[iter][i][j].IsFire() {
					return utils.Vector3D[int]{I: iter, J: i, K: j}
				}
			}
		}
	}
	return utils.Vector3D[int]{I: -1, J: -1, K: -1}
}

func (r RiverAnalysisInput) totalAreaBurned() int {
	cells := 0
	lastIndex := len(r.SimulationResult) - 1
	lattice2d := r.SimulationResult[lastIndex]
	for _, row := range lattice2d {
		for _, c := range row {
			if c.IsFire() || c == cell.ASH {
				cells++
			}
		}
	}
	return cells
}

func (r RiverAnalysis) ToCsv(separator rune) string {
	headers := getHeaders(r[0], separator)
	body := ""
	for _, result := range r {
		body += result.toString(separator) + "\n"
	}
	return headers + body
}

func getHeaders(data interface{}, separator rune) string {
	t := reflect.TypeOf(data)
	numFields := t.NumField()
	headers := ""
	for i := 0; i < numFields; i++ {
		field := t.Field(i)
		headers += field.Name + string(separator)
	}
	headers = headers[:len(headers)-1] + "\n"
	return headers
}
