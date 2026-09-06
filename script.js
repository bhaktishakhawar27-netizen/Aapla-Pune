/* =========================================
   GLOBAL VARIABLES
========================================= */

let currentFrom = "";
let currentTo = "";

let selectedMode = "auto";
let selectedPreference = "fastest";

let selectedRoute = null;

let selectedPayment = "upi";


let recentJourneys =
    JSON.parse(
        localStorage.getItem("recentJourneys")
    ) || [];


let bookedTickets =
    JSON.parse(
        localStorage.getItem("bookedTickets")
    ) || [];



/* =========================================
   METRO ROUTE
   SWARGATE → PCMC BHAVAN
========================================= */

const metroRoute = {

    id: "metro-swargate-pcmc",

    number: "Pune Metro Line",

    name: "Pune Metro",

    color: "Purple Corridor",

    type: "metro",

    stops: [

        "Swargate",

        "Mahatma Phule Mandai",

        "Kasba Peth",

        "District Court",

        "Shivaji Nagar",

        "Civil Court",

        "Range Hills",

        "Khadki",

        "Bopodi",

        "Dapodi",

        "Phugewadi",

        "Kasarwadi",

        "Nashik Phata",

        "Sant Tukaram Nagar",

        "PCMC Bhavan"

    ]

};



/* =========================================
   PMPML BUS ROUTE 121
========================================= */

const busRoute121 = {

    id: "bus-121",

    number: "121",

    name: "PMPML Bus Route 121",

    type: "bus",

    stops: [

        "Ma.Na.Pa Main Bus Stand",

        "Chhatrapati Shivaji Maharaj Putala",

        "Lokmangal Office (Shivajinagar)",

        "Patil Estate",

        "Labour Office",

        "Wakdewadi",

        "Wakdewadi ST Stand",

        "Poultry Farm Mumbai Road",

        "Raja Bungalow",

        "Khadki Post Office",

        "Factory Hospital",

        "Khadki Station",

        "Gopi Chal",

        "Bopodi",

        "Dapodi",

        "Phugewadi",

        "Phugewadi Jakat Naka",

        "Atlas COPCO India",

        "Kasarwadi",

        "Nashik Phata",

        "CIRT Office",

        "Bhosari Police Station",

        "MIDC Bhosari",

        "Philips Company",

        "Landewadi",

        "Shitalbaug",

        "Century Enka Colony",

        "Atlas Company",

        "Bhosari Terminal"

    ]

};



/* =========================================
   ALL LOCATIONS FOR SUGGESTIONS
========================================= */

const locations = [

    ...new Set([

        ...metroRoute.stops,

        ...busRoute121.stops,

        "PCMC",

        "PCMC Bhavan",

        "Shivajinagar",

        "Shivaji Nagar",

        "Ma.Na.Pa",

        "Mana Pa",

        "Bhosari"

    ])

];



/* =========================================
   INITIALIZE APP
========================================= */

function initializeApp() {

    const today =
        new Date()
            .toISOString()
            .split("T")[0];


    const now =
        new Date()
            .toTimeString()
            .slice(0, 5);


    const dateInput =
        document.getElementById(
            "journeyDate"
        );


    const timeInput =
        document.getElementById(
            "journeyTime"
        );


    if (dateInput) {

        dateInput.value = today;

        dateInput.min = today;

    }


    if (timeInput) {

        timeInput.value = now;

    }


    renderRecentJourneys();

    renderTickets();


    setTimeout(() => {

        const splash =
            document.getElementById("splash");


        if (splash) {

            splash.classList.remove("active");

        }


        showScreen("home");

    }, 1800);

}


document.addEventListener(
    "DOMContentLoaded",
    initializeApp
);



/* =========================================
   SCREEN NAVIGATION
========================================= */

function showScreen(screenId) {

    const screens =
        document.querySelectorAll(".screen");


    screens.forEach(screen => {

        screen.classList.remove("active");

    });


    const selectedScreen =
        document.getElementById(screenId);


    if (selectedScreen) {

        selectedScreen.classList.add("active");

    }


    updateBottomNavigation(screenId);

    closeMenu();


    window.scrollTo({

        top: 0,

        behavior: "smooth"

    });

}



function updateBottomNavigation(screenId) {

    const navItems =
        document.querySelectorAll(".nav-item");


    navItems.forEach(item => {

        item.classList.remove("active");

    });


    const mapping = {

        home: 0,

        journey: 1,

        tickets: 2,

        profile: 3

    };


    if (
        mapping[screenId] !== undefined &&
        navItems[mapping[screenId]]
    ) {

        navItems[
            mapping[screenId]
        ].classList.add("active");

    }

}



/* =========================================
   SIDE MENU
========================================= */

function toggleMenu() {

    document
        .getElementById("sideMenu")
        .classList.toggle("active");


    document
        .getElementById("menuOverlay")
        .classList.toggle("active");

}



function closeMenu() {

    const sideMenu =
        document.getElementById("sideMenu");


    const menuOverlay =
        document.getElementById("menuOverlay");


    if (sideMenu) {

        sideMenu.classList.remove("active");

    }


    if (menuOverlay) {

        menuOverlay.classList.remove("active");

    }

}



/* =========================================
   LOCATION SUGGESTIONS
========================================= */

function showSuggestions(type) {

    const input =
        document.getElementById(type);


    const dropdown =
        document.getElementById(
            type + "Suggestions"
        );


    if (!input || !dropdown) {

        return;

    }


    const searchText =
        input.value
            .trim()
            .toLowerCase();


    let filteredLocations;


    if (searchText === "") {

        filteredLocations =
            locations.slice(0, 10);

    } else {

        filteredLocations =
            locations
                .filter(location =>

                    location
                        .toLowerCase()
                        .includes(searchText)

                )
                .slice(0, 10);

    }


    if (filteredLocations.length === 0) {

        dropdown.innerHTML = `

            <div class="no-suggestion">

                📍 No matching location found

            </div>

        `;

    } else {

        dropdown.innerHTML =
            filteredLocations
                .map(location => `

                    <button
                        class="suggestion-item"
                        onclick="selectSuggestion('${location.replace(/'/g, "\\'")}', '${type}')"
                    >

                        <span class="suggestion-icon">
                            📍
                        </span>


                        <span class="suggestion-text">

                            <strong>
                                ${location}
                            </strong>

                            <small>
                                Pune Public Transport
                            </small>

                        </span>

                    </button>

                `)
                .join("");

    }


    dropdown.classList.add("show");

}



function selectSuggestion(location, type) {

    const input =
        document.getElementById(type);


    const dropdown =
        document.getElementById(
            type + "Suggestions"
        );


    input.value = location;


    if (type === "from") {

        currentFrom = location;

    } else {

        currentTo = location;

    }


    dropdown.classList.remove("show");

}



/* =========================================
   CLOSE SUGGESTIONS
========================================= */

document.addEventListener(
    "click",
    function (event) {

        const fromInput =
            document.getElementById("from");


        const toInput =
            document.getElementById("to");


        const fromDropdown =
            document.getElementById("fromSuggestions");


        const toDropdown =
            document.getElementById("toSuggestions");


        if (
            fromInput &&
            fromDropdown &&
            !fromInput.parentElement.parentElement.contains(event.target)
        ) {

            fromDropdown.classList.remove("show");

        }


        if (
            toInput &&
            toDropdown &&
            !toInput.parentElement.parentElement.contains(event.target)
        ) {

            toDropdown.classList.remove("show");

        }

    }
);



/* =========================================
   SWAP LOCATIONS
========================================= */

function swapLocations() {

    const fromInput =
        document.getElementById("from");


    const toInput =
        document.getElementById("to");


    const temporaryValue =
        fromInput.value;


    fromInput.value =
        toInput.value;


    toInput.value =
        temporaryValue;


    currentFrom =
        fromInput.value;


    currentTo =
        toInput.value;

}



/* =========================================
   JOURNEY TIME
========================================= */

function setJourneyTime(option, button) {

    document
        .querySelectorAll(".quick-time")
        .forEach(item => {

            item.classList.remove("active");

        });


    button.classList.add("active");


    if (option === "now") {

        const now =
            new Date();


        document.getElementById(
            "journeyDate"
        ).value =
            now
                .toISOString()
                .split("T")[0];


        document.getElementById(
            "journeyTime"
        ).value =
            now
                .toTimeString()
                .slice(0, 5);

    }

}



/* =========================================
   TRANSPORT MODE
========================================= */

function selectMode(mode) {

    selectedMode = mode;


    document
        .querySelectorAll(".transport-option")
        .forEach(item => {

            item.classList.remove("active");

        });


    const selectedButton =
        document.getElementById(
            mode + "Mode"
        );


    if (selectedButton) {

        selectedButton.classList.add("active");

    }

}



/* =========================================
   NORMALIZE LOCATION
========================================= */

function normalizeLocation(location) {

    const aliases = {

        "pcmc":
            "PCMC Bhavan",

        "pcmc bhavan":
            "PCMC Bhavan",

        "shivajinagar":
            "Shivaji Nagar",

        "shivaji nagar":
            "Shivaji Nagar",

        "mana pa":
            "Ma.Na.Pa Main Bus Stand",

        "ma.na.pa":
            "Ma.Na.Pa Main Bus Stand",

        "ma.na.pa main bus stand":
            "Ma.Na.Pa Main Bus Stand",

        "bhosari":
            "Bhosari Terminal"

    };


    const key =
        location
            .trim()
            .toLowerCase();


    return aliases[key] ||
        location.trim();

}



/* =========================================
   GET STOPS BETWEEN
========================================= */

function getStopsBetween(stops, from, to) {

    const fromIndex =
        stops.findIndex(stop =>

            stop.toLowerCase() ===
            from.toLowerCase()

        );


    const toIndex =
        stops.findIndex(stop =>

            stop.toLowerCase() ===
            to.toLowerCase()

        );


    if (
        fromIndex === -1 ||
        toIndex === -1
    ) {

        return null;

    }


    if (fromIndex <= toIndex) {

        return stops.slice(
            fromIndex,
            toIndex + 1
        );

    }


    return stops
        .slice(
            toIndex,
            fromIndex + 1
        )
        .reverse();

}



/* =========================================
   FIND METRO ROUTE
========================================= */

function findMetroRoutes(from, to) {

    const routeStops =
        getStopsBetween(
            metroRoute.stops,
            from,
            to
        );


    if (!routeStops) {

        return [];

    }


    return [

        {

            type: "metro",

            title: "Pune Metro",

            subtitle:
                "Swargate → PCMC Bhavan",

            stops: routeStops

        }

    ];

}



/* =========================================
   FIND BUS ROUTE 121
========================================= */

function findBusRoutes(from, to) {

    const routeStops =
        getStopsBetween(
            busRoute121.stops,
            from,
            to
        );


    if (!routeStops) {

        return [];

    }


    return [

        {

            type: "bus",

            title: "PMPML Bus 121",

            subtitle:
                "PMPML Route 121",

            stops: routeStops

        }

    ];

}



/* =========================================
   CALCULATIONS
========================================= */

function estimateDuration(stopCount, mode) {

    if (mode === "metro") {

        return Math.max(
            5,
            stopCount * 3
        );

    }


    return Math.max(
        8,
        stopCount * 5
    );

}



function estimateFare(stopCount, mode) {

    if (mode === "metro") {

        return Math.min(
            60,
            10 + stopCount * 3
        );

    }


    return Math.min(
        50,
        5 + stopCount * 2
    );

}



function getCrowd(stopCount, mode) {

    let percentage;


    if (mode === "metro") {

        percentage = Math.min(
            85,
            30 + stopCount * 4
        );

    } else {

        percentage = Math.min(
            90,
            35 + stopCount * 3
        );

    }


    let level;


    if (percentage < 45) {

        level = "Low";

    }

    else if (percentage < 70) {

        level = "Medium";

    }

    else {

        level = "High";

    }


    return {

        percentage,

        level

    };

}



function getSignalCount(stopCount, mode) {

    if (mode === "metro") {

        return 0;

    }


    return Math.max(
        1,
        Math.floor(stopCount / 2)
    );

}



/* =========================================
   BUILD ROUTES
========================================= */

function buildRoutes(from, to) {

    let routes = [];


    if (
        selectedMode === "auto" ||
        selectedMode === "metro"
    ) {

        routes =
            routes.concat(
                findMetroRoutes(from, to)
            );

    }


    if (
        selectedMode === "auto" ||
        selectedMode === "bus"
    ) {

        routes =
            routes.concat(
                findBusRoutes(from, to)
            );

    }


    return routes.map(
        (route, index) => {

            const stopCount =
                route.stops.length - 1;


            const duration =
                estimateDuration(
                    stopCount,
                    route.type
                );


            const fare =
                estimateFare(
                    stopCount,
                    route.type
                );


            const crowd =
                getCrowd(
                    stopCount,
                    route.type
                );


            const signals =
                getSignalCount(
                    stopCount,
                    route.type
                );


            return {

                id:
                    Date.now() + index,

                ...route,

                stopCount,

                duration,

                fare,

                crowd,

                signals

            };

        }
    );

}



/* =========================================
   PLAN JOURNEY
========================================= */

function planJourney() {

    const fromInput =
        document.getElementById("from");


    const toInput =
        document.getElementById("to");


    currentFrom =
        normalizeLocation(
            fromInput.value
        );


    currentTo =
        normalizeLocation(
            toInput.value
        );


    fromInput.value =
        currentFrom;


    toInput.value =
        currentTo;


    selectedRoute = null;


    if (!currentFrom || !currentTo) {

        alert(
            "Please enter both starting location and destination."
        );

        return;

    }


    if (
        currentFrom.toLowerCase() ===
        currentTo.toLowerCase()
    ) {

        alert(
            "Starting location and destination cannot be the same."
        );

        return;

    }


    const routes =
        buildRoutes(
            currentFrom,
            currentTo
        );


    document.getElementById(
        "routeFrom"
    ).textContent =
        currentFrom;


    document.getElementById(
        "routeTo"
    ).textContent =
        currentTo;


    if (routes.length === 0) {

        document.getElementById(
            "routesContainer"
        ).innerHTML = `

            <div class="empty-state">

                <div>🗺️</div>

                <h2>
                    No Direct Route Found
                </h2>

                <p>
                    Try locations available on Pune Metro
                    or PMPML Bus Route 121.
                </p>

            </div>

        `;


        showScreen("journey");

        return;

    }


    addRecentJourney(
        currentFrom,
        currentTo
    );


    window.availableRoutes =
        routes;


    displayRoutes(routes);

    updateRecommendation(routes);

    displayComparison(routes);

    resetJourneyAlert();

    showScreen("journey");

}



/* =========================================
   DISPLAY ROUTES
========================================= */

function displayRoutes(routes) {

    const container =
        document.getElementById(
            "routesContainer"
        );


    const sortedRoutes =
        sortRoutes(
            routes,
            selectedPreference
        );


    window.availableRoutes =
        sortedRoutes;


    container.innerHTML =
        sortedRoutes.map(route => {

            const icon =
                route.type === "metro"
                    ? "🚇"
                    : "🚌";


            return `

                <div
                    class="route-card"
                    id="route-${route.id}"
                >

                    <div class="route-card-header">

                        <div class="route-type">

                            <div
                                class="route-type-icon ${route.type}"
                            >
                                ${icon}
                            </div>


                            <div>

                                <h3>
                                    ${route.title}
                                </h3>

                                <p class="route-subtitle">
                                    ${route.subtitle}
                                </p>

                            </div>

                        </div>


                        <button
                            class="select-route-button"
                            onclick="selectDynamicRoute(${route.id})"
                        >

                            Select

                        </button>

                    </div>



                    <div class="route-data">

                        <div>

                            <span>Stops</span>

                            <strong>
                                ${route.stopCount}
                            </strong>

                        </div>


                        <div>

                            <span>Time</span>

                            <strong>
                                ${route.duration} min
                            </strong>

                        </div>


                        <div>

                            <span>Fare</span>

                            <strong>
                                ₹${route.fare}
                            </strong>

                        </div>


                        <div>

                            <span>Crowd</span>

                            <strong>
                                ${route.crowd.level}
                            </strong>

                        </div>

                    </div>



                    <div class="route-extra">

                        <span>

                            🚦 ${route.signals} Signals

                        </span>


                        <span>

                            ${route.type === "metro"
                                ? "⚡ Fast & Comfortable"
                                : "🚌 PMPML Route 121"}

                        </span>

                    </div>

                </div>

            `;

        }).join("");

}



/* =========================================
   SORT ROUTES
========================================= */

function sortRoutes(routes, preference) {

    const copiedRoutes =
        [...routes];


    if (preference === "fastest") {

        copiedRoutes.sort(
            (a, b) =>
                a.duration -
                b.duration
        );

    }


    if (preference === "cheapest") {

        copiedRoutes.sort(
            (a, b) =>
                a.fare -
                b.fare
        );

    }


    if (preference === "crowd") {

        copiedRoutes.sort(
            (a, b) =>
                a.crowd.percentage -
                b.crowd.percentage
        );

    }


    if (preference === "eco") {

        copiedRoutes.sort(
            (a, b) => {

                if (
                    a.type === "metro" &&
                    b.type !== "metro"
                ) {

                    return -1;

                }


                if (
                    b.type === "metro" &&
                    a.type !== "metro"
                ) {

                    return 1;

                }


                return 0;

            }
        );

    }


    return copiedRoutes;

}



/* =========================================
   FILTER
========================================= */

function applyFilter(preference, button) {

    selectedPreference =
        preference;


    document
        .querySelectorAll(".filter-button")
        .forEach(item => {

            item.classList.remove("active");

        });


    button.classList.add("active");


    if (
        window.availableRoutes &&
        window.availableRoutes.length > 0
    ) {

        const sorted =
            sortRoutes(
                window.availableRoutes,
                preference
            );


        displayRoutes(sorted);

        updateRecommendation(sorted);

        displayComparison(sorted);

    }

}



/* =========================================
   RECOMMENDATION
========================================= */

function updateRecommendation(routes) {

    if (!routes || routes.length === 0) {

        return;

    }


    const bestRoute =
        sortRoutes(
            routes,
            selectedPreference
        )[0];


    document.getElementById(
        "recommendationTitle"
    ).textContent =
        bestRoute.title;


    let description = "";


    if (selectedPreference === "fastest") {

        description =
            "This route gives you the shortest estimated travel time.";

    }


    if (selectedPreference === "cheapest") {

        description =
            "This route offers the lowest estimated fare.";

    }


    if (selectedPreference === "crowd") {

        description =
            "This route is expected to have comparatively less crowd.";

    }


    if (selectedPreference === "eco") {

        description =
            "Metro is recommended for a more eco-friendly journey.";

    }


    document.getElementById(
        "recommendationDescription"
    ).textContent =
        description;


    document.getElementById(
        "recommendationTime"
    ).textContent =
        bestRoute.duration + " min";


    document.getElementById(
        "recommendationFare"
    ).textContent =
        "₹" + bestRoute.fare;


    document.getElementById(
        "recommendationCrowd"
    ).textContent =
        bestRoute.crowd.level;

}



/* =========================================
   COMPARISON
========================================= */

function displayComparison(routes) {

    const container =
        document.getElementById(
            "comparisonContainer"
        );


    container.innerHTML =
        routes.map(route => `

            <div class="comparison-card">

                <h3>

                    ${route.type === "metro"
                        ? "🚇"
                        : "🚌"}

                    ${route.title}

                </h3>


                <div class="comparison-grid">

                    <div>

                        <span>Stops</span>

                        <strong>
                            ${route.stopCount}
                        </strong>

                    </div>


                    <div>

                        <span>Time</span>

                        <strong>
                            ${route.duration}m
                        </strong>

                    </div>


                    <div>

                        <span>Fare</span>

                        <strong>
                            ₹${route.fare}
                        </strong>

                    </div>


                    <div>

                        <span>Crowd</span>

                        <strong>
                            ${route.crowd.level}
                        </strong>

                    </div>

                </div>

            </div>

        `).join("");

}



/* =========================================
   SELECT ROUTE
========================================= */

function selectDynamicRoute(routeId) {

    if (!window.availableRoutes) {

        return;

    }


    selectedRoute =
        window.availableRoutes.find(
            route =>
                route.id === routeId
        );


    if (!selectedRoute) {

        return;

    }


    document
        .querySelectorAll(".route-card")
        .forEach(card => {

            card.classList.remove("selected");

        });


    const selectedCard =
        document.getElementById(
            "route-" + routeId
        );


    if (selectedCard) {

        selectedCard.classList.add("selected");

    }


    const journeyAlert =
        document.getElementById(
            "journeyAlert"
        );


    if (journeyAlert) {

        journeyAlert.innerHTML = `

            <div class="alert-icon">
                🎫
            </div>

            <div style="width:100%;">

                <strong>
                    ${selectedRoute.title} Selected!
                </strong>

                <p>
                    Your route is ready for ticket booking.
                </p>


                <button
                    onclick="openConfirmTicket()"
                    style="
                        margin-top:10px;
                        padding:10px 18px;
                        border-radius:20px;
                        background:#123449;
                        color:#d9ef18;
                        font-size:11px;
                        font-weight:800;
                    "
                >

                    🎫 Book Ticket

                </button>

            </div>

        `;

    }


    journeyAlert.scrollIntoView({

        behavior: "smooth",

        block: "center"

    });

}



/* =========================================
   RESET ALERT
========================================= */

function resetJourneyAlert() {

    const journeyAlert =
        document.getElementById(
            "journeyAlert"
        );


    if (journeyAlert) {

        journeyAlert.innerHTML = `

            <div class="alert-icon">
                🔔
            </div>

            <div>

                <strong>
                    Journey Update
                </strong>

                <p>
                    Select a route to book your ticket.
                </p>

            </div>

        `;

    }

}



/* =========================================
   ENSURE ROUTE SELECTED
========================================= */

function ensureRouteSelected() {

    if (!selectedRoute) {

        alert(
            "Please select a route first."
        );

        return false;

    }


    return true;

}



/* =========================================
   STATIONS
========================================= */

function openStations() {

    closeMenu();


    if (!ensureRouteSelected()) {

        return;

    }


    document.getElementById(
        "stationSummaryTitle"
    ).textContent =
        selectedRoute.title;


    document.getElementById(
        "stationSummaryText"
    ).textContent =
        currentFrom +
        " → " +
        currentTo +
        " • " +
        selectedRoute.stopCount +
        " stops";


    const stationList =
        document.getElementById(
            "stationList"
        );


    stationList.innerHTML =
        selectedRoute.stops.map(
            (stop, index) => `

                <div class="stop-item">

                    <div class="stop-number">

                        ${index + 1}

                    </div>


                    <strong>

                        ${stop}

                    </strong>

                </div>

            `
        ).join("");


    showScreen("stations");

}



/* =========================================
   LIVE TRACKING
========================================= */

function openTracking() {

    closeMenu();


    if (!ensureRouteSelected()) {

        return;

    }


    const vehicleIcon =
        selectedRoute.type === "metro"
            ? "🚇"
            : "🚌";


    document.getElementById(
        "trackingVehicle"
    ).textContent =
        vehicleIcon +
        " " +
        selectedRoute.title;


    document.getElementById(
        "trackingRoute"
    ).textContent =
        currentFrom +
        " → " +
        currentTo;


    const eta =
        Math.max(
            2,
            Math.floor(
                selectedRoute.duration / 3
            )
        );


    document.getElementById(
        "trackingETA"
    ).textContent =
        eta + " min";


    showScreen("tracking");

}



/* =========================================
   CROWD PREDICTION
========================================= */

function openCrowdPrediction() {

    closeMenu();


    if (!ensureRouteSelected()) {

        return;

    }


    const percentage =
        selectedRoute.crowd.percentage;


    const level =
        selectedRoute.crowd.level;


    document.getElementById(
        "crowdPercentage"
    ).textContent =
        percentage + "%";


    document.getElementById(
        "crowdPercentageDetails"
    ).textContent =
        percentage +
        "% - " +
        level;


    document.getElementById(
        "crowdStatus"
    ).textContent =
        level +
        " Crowd Expected";


    document.getElementById(
        "crowdRouteName"
    ).textContent =
        selectedRoute.title;


    const circle =
        document.querySelector(
            ".crowd-circle"
        );


    if (circle) {

        const degrees =
            percentage * 3.6;


        circle.style.background =
            `conic-gradient(
                #d9ef18 0deg ${degrees}deg,
                #e8edef ${degrees}deg 360deg
            )`;

    }


    showScreen("crowd");

}



/* =========================================
   OPEN TICKET CONFIRMATION
========================================= */

function openConfirmTicket() {

    closeMenu();


    if (!ensureRouteSelected()) {

        return;

    }


    document.getElementById(
        "confirmFrom"
    ).textContent =
        currentFrom;


    document.getElementById(
        "confirmTo"
    ).textContent =
        currentTo;


    document.getElementById(
        "confirmMode"
    ).textContent =
        selectedRoute.type === "metro"
            ? "Pune Metro"
            : "PMPML Bus";


    document.getElementById(
        "confirmVehicle"
    ).textContent =
        selectedRoute.title;


    document.getElementById(
        "confirmTime"
    ).textContent =
        selectedRoute.duration +
        " minutes";


    document.getElementById(
        "confirmFare"
    ).textContent =
        "₹" +
        selectedRoute.fare;


    showScreen("confirmTicket");

}



/* =========================================
   PAYMENT
========================================= */

function selectPayment(payment) {

    selectedPayment =
        payment;


    document
        .querySelectorAll(".payment-option")
        .forEach(item => {

            item.classList.remove("active");

        });


    const selectedButton =
        document.getElementById(
            payment + "Payment"
        );


    if (selectedButton) {

        selectedButton.classList.add("active");

    }

}



/* =========================================
   CONFIRM TICKET
========================================= */

function confirmTicket() {

    if (!ensureRouteSelected()) {

        return;

    }


    const journeyDate =
        document.getElementById(
            "journeyDate"
        )?.value || "";


    const journeyTime =
        document.getElementById(
            "journeyTime"
        )?.value || "";


    const ticket = {

        id: Date.now(),


        ticketNumber:
            "AP" +
            String(Date.now()).slice(-6),


        from:
            currentFrom,


        to:
            currentTo,


        route:
            selectedRoute.title,


        mode:
            selectedRoute.type,


        fare:
            selectedRoute.fare,


        time:
            selectedRoute.duration,


        payment:
            selectedPayment,


        journeyDate,


        journeyTime,


        status:
            "Confirmed",


        bookedAt:
            new Date().toLocaleString()

    };


    bookedTickets.unshift(ticket);


    localStorage.setItem(
        "bookedTickets",
        JSON.stringify(bookedTickets)
    );


    renderTickets();


    alert(
        "🎉 Ticket Confirmed Successfully!\n\n" +
        "Ticket: " +
        ticket.ticketNumber +
        "\nJourney: " +
        currentFrom +
        " → " +
        currentTo +
        "\nFare: ₹" +
        selectedRoute.fare
    );


    showScreen("tickets");

}



/* =========================================
   RENDER TICKETS
========================================= */

function renderTickets() {

    const container =
        document.getElementById(
            "ticketsContainer"
        );


    if (!container) {

        return;

    }


    if (bookedTickets.length === 0) {

        container.innerHTML = `

            <div class="empty-state">

                <div>🎫</div>

                <h2>
                    No Tickets Yet
                </h2>

                <p>
                    Your booked tickets will appear here.
                </p>

            </div>

        `;

        return;

    }


    container.innerHTML =
        bookedTickets.map(ticket => `

            <div class="ticket-summary">

                <div class="ticket-title">

                    <span>

                        ${ticket.mode === "metro"
                            ? "🚇"
                            : "🚌"}

                    </span>


                    <div>

                        <small>
                            AAPLA PRAVAS TICKET
                        </small>

                        <h3>
                            ${ticket.route}
                        </h3>

                    </div>

                </div>



                <div class="ticket-info">

                    <p>

                        <strong>
                            Ticket ID
                        </strong>

                        <span>
                            ${ticket.ticketNumber}
                        </span>

                    </p>


                    <p>

                        <strong>
                            Journey
                        </strong>

                        <span>
                            ${ticket.from}
                            →
                            ${ticket.to}
                        </span>

                    </p>


                    <p>

                        <strong>
                            Date
                        </strong>

                        <span>
                            ${ticket.journeyDate || "Today"}
                        </span>

                    </p>


                    <p>

                        <strong>
                            Journey Time
                        </strong>

                        <span>
                            ${ticket.journeyTime || "-"}
                        </span>

                    </p>


                    <p>

                        <strong>
                            Travel Time
                        </strong>

                        <span>
                            ${ticket.time} minutes
                        </span>

                    </p>


                    <p>

                        <strong>
                            Payment
                        </strong>

                        <span>

                            ${ticket.payment === "upi"
                                ? "UPI Payment"
                                : "Pay at Counter"}

                        </span>

                    </p>


                    <p class="ticket-fare">

                        <strong>
                            Total Fare
                        </strong>

                        <span>
                            ₹${ticket.fare}
                        </span>

                    </p>


                    <button
                        onclick="deleteTicket(${ticket.id})"
                        style="
                            width:100%;
                            margin-top:15px;
                            padding:11px;
                            border-radius:10px;
                            background:#fff0f0;
                            color:#e63946;
                            font-weight:700;
                        "
                    >

                        Cancel Ticket

                    </button>

                </div>

            </div>

        `).join("");

}



/* =========================================
   DELETE TICKET
========================================= */

function deleteTicket(ticketId) {

    const confirmDelete =
        confirm(
            "Are you sure you want to cancel this ticket?"
        );


    if (!confirmDelete) {

        return;

    }


    bookedTickets =
        bookedTickets.filter(
            ticket =>
                ticket.id !== ticketId
        );


    localStorage.setItem(
        "bookedTickets",
        JSON.stringify(bookedTickets)
    );


    renderTickets();


    alert(
        "Ticket cancelled successfully."
    );

}



/* =========================================
   RECENT JOURNEYS
========================================= */

function addRecentJourney(from, to) {

    const exists =
        recentJourneys.find(
            journey =>
                journey.from === from &&
                journey.to === to
        );


    if (!exists) {

        recentJourneys.unshift({

            from,

            to

        });

    }


    recentJourneys =
        recentJourneys.slice(0, 5);


    localStorage.setItem(
        "recentJourneys",
        JSON.stringify(recentJourneys)
    );


    renderRecentJourneys();

}



function renderRecentJourneys() {

    const container =
        document.getElementById(
            "recentJourneys"
        );


    if (!container) {

        return;

    }


    if (recentJourneys.length === 0) {

        container.innerHTML = `

            <div class="recent-empty">

                🕘 Your recently searched journeys
                will appear here.

            </div>

        `;

        return;

    }


    container.innerHTML =
        recentJourneys.map(
            (journey, index) => `

                <div class="recent-route">

                    <div>

                        <strong>

                            ${journey.from}
                            →
                            ${journey.to}

                        </strong>

                        <small>
                            Recent Journey
                        </small>

                    </div>


                    <button
                        onclick="useRecentJourney(${index})"
                    >

                        Use

                    </button>

                </div>

            `
        ).join("");

}



function useRecentJourney(index) {

    const journey =
        recentJourneys[index];


    if (!journey) {

        return;

    }


    document.getElementById(
        "from"
    ).value =
        journey.from;


    document.getElementById(
        "to"
    ).value =
        journey.to;


    currentFrom =
        journey.from;


    currentTo =
        journey.to;


    planJourney();

}