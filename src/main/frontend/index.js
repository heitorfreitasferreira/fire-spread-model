const timeOutMs = 500;
const latticeSteps = 5;
const folder = "./../../../reticulados/campestre/0.5/e/[17-08-2023_10-05]/simulation_0/"
function setLattice(fileNumber) {
    const container = document.querySelector('.container');

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
                        square.classList.add('square');
                        square.style.backgroundColor = colorMap[color];
                        container.appendChild(square);
                    }
                });
            });

            if (fileNumber<99)
                setTimeout(()=> {
                    while (container.firstChild) {
                        container.removeChild(container.firstChild);
                    }
                    setLattice(fileNumber+latticeSteps)
                }, timeOutMs);

        })
        .catch(error => {
            console.error('Error:', error);
        });
}


setLattice(0)