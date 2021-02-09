let submit_button = document.getElementById("submit_button");
let name_input = document.getElementById("name_input");
let form = document.getElementById("form");
let add_more_button = document.getElementById("add_more_coords");
let inputs = document.getElementById("input_container");


let coords = [
    [0, 0]
]

updateDOM()

async function post() {
    let room = { coordinates: [], name: name_input.value }
    coords.forEach((coord_pair) => {
        room.coordinates.push(coord_pair)
    })
    console.log(room)
    await fetch('http://localhost:8080/room', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json;charset=utf-8'
            },
            body: JSON.stringify(room)
        }).then(response => response.json())
        .then(response => {
            if (response.error) {
                alert(response.error)
            } else {
                window.location.href = "./main.html"
            }
        });

}

function changeToMainHtmlPage() {
    window.location.href = "./main.html";
}



function updateDOM() {
    // save inputs values
    try {
        coords.forEach((coord_arr, index) => {
            coord_arr[0] = (parseInt(document.getElementById("x" + index).value))
            coord_arr[1] = (parseInt(document.getElementById("y" + index).value))
        })
    } catch (error) {
        console.log(error);
    }

    inputs.innerHTML = "";

    coords.forEach((coord_pair, index) => {
        const element = document.createElement('div');
        element.classList.add('input_container');
        console.log(coord_pair)
        element.innerHTML =
            `<p>${(index+1)} pair</p>
            <div class="flex">
                <input class="container__input half_width" type="number" autocomplete="off" id="x${index}" name="x${index}" value="${coord_pair[0]}" required>
                <input class="container__input half_width" type="number" autocomplete="off" id="y${index}" name="y${index}" value="${coord_pair[1]}" required>
            </div>`;

        inputs.appendChild(element);
    });
}


add_more_button.addEventListener("click", () => {
    coords.push([0, 0])
    updateDOM()
})


form.addEventListener("submit", async(event) => {
    event.preventDefault();
    updateDOM()
    post();
})