const timeOutMs = 500;
const simulationContainer = document.querySelector('.simulation');
const form = document.querySelector('form');
const outsideContainer = document.querySelector('.container');


const runSimulation = (fileNumber, folder, latticeSteps) => {

    outsideContainer.classList.add('invisible')
    simulationContainer.classList.remove('invisible')
    setLattice(fileNumber, folder, latticeSteps)
}
function setLattice(fileNumber, folder, latticeSteps) {

    const colorMap = [
        "#877473",
        "#b07e54",
        "#b07e54",
        "#4f2906",
        "#615c37",
        "#517538",
        "#00d90e",
        "#00c3d9"
    ]
    fetch(`${folder}${fileNumber}.txt`)
        .then(response => response.text())
        .then(text => {
            const colors = text.split('\n');
            colors.forEach(colorLine => {
                colorLine.split(" ").forEach(color => {
                    if (color) {
                        const square = document.createElement('div');
                        square.classList.add('cell');
                        square.style.backgroundColor = colorMap[color];
                        simulationContainer.appendChild(square);
                    }
                });
            });

            if (fileNumber<99) {
                setTimeout(() => {
                    while (simulationContainer.firstChild) {
                        simulationContainer.removeChild(simulationContainer.firstChild);
                    }
                    setLattice(fileNumber + latticeSteps, folder, latticeSteps)
                }, timeOutMs);
            }
            else{
                outsideContainer.classList.remove('invisible')
                while (simulationContainer.firstChild) {
                    simulationContainer.removeChild(simulationContainer.firstChild);
                }
                simulationContainer.classList.add('invisible')
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

/**
 * @description Gera o nome da pasta de acordo com os valores dos inputs
 * @returns {`./../../../reticulados/${String}/${Number}/${String}/[17-08-2023_10-05]/simulation_0/`}
 */
const generateFolderName = () => {
    const vegetacao = document.querySelector('#vegetacao').value;
    const umidade = document.querySelector('#umidade').value;
    const vento = document.querySelector('#vento').value;
    return `./../../../reticulados/${vegetacao}/${umidade}/${vento}/[17-08-2023_10-05]/simulation_0/`
}
document.querySelector('form button').addEventListener('click', (event) => {
    event.preventDefault();
    runSimulation(0, generateFolderName(), 1);
});