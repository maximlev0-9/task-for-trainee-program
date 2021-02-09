const rooms = document.getElementById('room_container');
const rooms_view_p = document.getElementById('view_of_rooms');
// const sort_button = document.getElementById('sort_button');
// const count_button = document.getElementById('count_button');
// const search_button = document.getElementById("search_button");
// const clear_button = document.getElementById("clear_button");
// const search_input = document.getElementById("search_model_input");
const toggle_rooms_button = document.getElementById("toggle_rooms_button");
let show_rooms = true;

let data = [];

get_all_rooms();

let x_largest = -100000000000;
let x_smallest = 1000000000000;
let y_largest = -100000000000;
let y_smallest = 1000000000000;

const max = (a, b) => a > b ? a : b;
const min = (a, b) => a > b ? b : a;

let array_of_walls_and_space = []

async function get_all_rooms() {
    const res = await fetch("http://localhost:8080/room");

    const data_resp = await res.json();

    data = []

    for (const dataKey of data_resp) {
        data.push(dataKey);
    }

    // cycle for all rooms to find biggest and smallest coordinates
    for (const room of data) {
        for (const coords of room.coordinates) {
            x_largest = max(x_largest, coords[0])
            x_smallest = min(x_smallest, coords[0])
            y_largest = max(y_largest, coords[1])
            y_smallest = min(y_smallest, coords[1])
        }
    }

    array_of_walls_and_space = []

    for (let i = 0; i <= y_largest - y_smallest; i++) {
        array_of_walls_and_space.push([])
        for (let j = 0; j <= x_largest - x_smallest; j++) {
            array_of_walls_and_space[i].push("_")
        }
    }

    // one more cycle to fill 2d array of room views with asterisks.
    for (const room of data) {
        const coords = [...room.coordinates];
        coords.push(coords[0])
        for (let i = 0; i < coords.length - 1; i++) {
            replace_walls_with_asterisks(coords[i], coords[i + 1])
        }
    }
    updateDOM();
}

function replace_walls_with_asterisks(first_coords, second_coords) {

    if (first_coords[0] === second_coords[0]) {
        for (let y = min(first_coords[1], second_coords[1]) - y_smallest; y <= max(first_coords[1], second_coords[1]) - y_smallest; y++) {
            array_of_walls_and_space[y][first_coords[0] - x_smallest] = '0'
        }
    } else {
        for (let x = min(first_coords[0], second_coords[0]) - x_smallest; x <= max(first_coords[0], second_coords[0]) - x_smallest; x++) {
            array_of_walls_and_space[first_coords[1] - y_smallest][x] = '0'
        }
    }
}

function add_data(obj) {
    data.push(obj);
    updateDOM(data);
}

async function delete_room(id) {

    await fetch(`http://localhost:8080/room/${id}`, {
        method: 'DELETE'
    });

    await get_all_rooms();
    updateDOM();
}

function updateDOM(providedData = data) {
    rooms.innerHTML = "";
    providedData.forEach(room => {
        const element = document.createElement('div');
        element.classList.add('room_card_wrapper');
        element.innerHTML =
            `<div class="room_card">
            <div class="room_card--main">
                <p>Id:${room.id}</p>
                <p>Name:${room.name}</p>
                <p>Coords:${room.coordinates.join("; ")}</p>
            </div>
            <button onclick="delete_room(${room.id})" class="button_around_icon"><img class="room_card--trash_icon" src="assets/trash.svg" /></button>
        </div>`;

        rooms.appendChild(element);
    });
    const element = document.createElement('div');
    element.innerHTML =
        `<a href="create_room.html">
            <div class="room_card_wrapper">
                <div class="room_card lightgrey">
                    <div class="add_button-wrapper">
                        <img class="add_button" src="assets/plus.svg" />
                    </div>
                </div>
            </div>
        </a>`;

    rooms.append(element);

    // use 2d array of room views to represent rooms in room_view_p variable
    let innerHTML = '';
    if (show_rooms) {

        for (const values of array_of_walls_and_space) {
            innerHTML += "<span>"
            innerHTML += values.join('  ')
            innerHTML += "<span/><br/>"
        }
        innerHTML += "<br/><br/>"
        rooms_view_p.innerHTML = innerHTML;
    }
    rooms_view_p.innerHTML = innerHTML;
}

toggle_rooms_button.addEventListener("click", () => {
    show_rooms = !show_rooms;
    updateDOM();
})