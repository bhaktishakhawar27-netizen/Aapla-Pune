/* =========================================================
   AAPLA PRAVAS - SCRIPT.JS
   Smart Pune Public Transport Journey Planner
========================================================= */


/* =========================================================
   GLOBAL VARIABLES
========================================================= */

let currentFrom = "";
let currentTo = "";

let selectedMode = "auto";
let selectedPreference = "fastest";

let selectedRoute = null;
let selectedPayment = "upi";

let recentJourneys =
    JSON.parse(localStorage.getItem("recentJourneys")) || [];

let bookedTickets =
    JSON.parse(localStorage.getItem("bookedTickets")) || [];

window.availableRoutes = [];


/* =========================================================
   PUNE METRO ROUTE
   SWARGATE → PCMC
========================================================= */

const metroLines = [
    {
        id: "metro-line-1",
        name: "Pune Metro",
        color: "Purple Line",
        mode: "metro",

        stations: [
            "Swargate",
            "Mahatma Phule Mandai",
            "Kasba Peth",
            "District Court",
            "Shivajinagar",
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
    }
];


/* =========================================================
   PMPML BUS ROUTE 121
========================================================= */

const busRoutes = [
    {
        id: "bus-121",
        number: "121",
        name: "PMPML Bus Route 121",
        mode: "bus",

        stops: [
            "Ma.Na.Pa Main Bus Stand",
            "Chhatrapati Shivaji Maharaj Putala",
            "Lokmangal Office",
            "Shivajinagar",
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
    }
];


/* =========================================================
   ALL LOCATIONS FOR SUGGESTIONS
========================================================= */

const locations = [...new Set([
    ...metroLines.flatMap(line => line.stations),
    ...busRoutes.flatMap(route => route.stops),

    "PCMC",
    "PCMC Bhavan",
    "Ma.Na.Pa",
    "Mana Pa",
    "Bhosari Terminal"
])].sort();


/* =========================================================
   INITIALIZE APP
========================================================= */

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
        document.getElementById("journeyDate");

    const timeInput =
        document.getElementById("journeyTime");

    if (dateInput) {
        dateInput.value = today;
        dateInput.min = today;
    }

    if (timeInput) {
        timeInput.value = now;
    }

    renderRecentJourneys();
    renderTickets();

    const splash =
        document.getElementById("splash");

    if (splash) {

        setTimeout(() => {

            splash.classList.remove("active");

            showScreen("home");

        }, 1800);

    } else {

        showScreen("home");

    }

}


document.addEventListener(
    "DOMContentLoaded",
    initializeApp
);


/* =========================================================
   SCREEN NAVIGATION
========================================================= */

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


/* =========================================================
   BOTTOM NAVIGATION
========================================================= */

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


/* =========================================================
   SIDE MENU
========================================================= */

function toggleMenu() {

    const sideMenu =
        document.getElementById("sideMenu");

    const overlay =
        document.getElementById("menuOverlay");

    if (sideMenu) {
        sideMenu.classList.toggle("active");
    }

    if (overlay) {
        overlay.classList.toggle("active");
    }

}


function closeMenu() {

    const sideMenu =
        document.getElementById("sideMenu");

    const overlay =
        document.getElementById("menuOverlay");

    if (sideMenu) {
        sideMenu.classList.remove("active");
    }

    if (overlay) {
        overlay.classList.remove("active");
    }

}


/* =========================================================
   LOCATION SUGGESTIONS
========================================================= */

function showSuggestions(type) {

    const input =
        document.getElementById(type);

    const dropdown =
        document.getElementById(
            type + "Suggestions"
        );

    if (!input || !dropdown) return;

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
            locations.filter(location =>
                location
                    .toLowerCase()
                    .includes(searchText)
            ).slice(0, 10);

    }


    if (filteredLocations.length === 0) {

        dropdown.innerHTML = `
            <div class="no-suggestion">
                No location found
            </div>
        `;

    } else {

        dropdown.innerHTML =
            filteredLocations.map(location => `

                <button
                    type="button"
                    class="suggestion-item"
                    onclick="selectSuggestion(
                        '${location.replace(/'/g, "\\'")}',
                        '${type}'
                    )"
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

            `).join("");

    }


    dropdown.classList.add("show");

}


/* =========================================================
   SELECT SUGGESTION
========================================================= */

function selectSuggestion(location, type) {

    const input =
        document.getElementById(type);

    const dropdown =
        document.getElementById(
            type + "Suggestions"
        );

    if (input) {
        input.value = location;
    }

    if (type === "from") {
        currentFrom = location;
    } else {
        currentTo = location;
    }

    if (dropdown) {
        dropdown.classList.remove("show");
    }

}


/* =========================================================
   CLOSE SUGGESTIONS
========================================================= */

document.addEventListener(
    "click",
    function (event) {

        ["from", "to"].forEach(type => {

            const input =
                document.getElementById(type);

            const dropdown =
                document.getElementById(
                    type + "Suggestions"
                );

            if (!input || !dropdown) return;

            if (
                !input.parentElement.contains(event.target)
            ) {

                dropdown.classList.remove("show");

            }

        });

    }
);


/* =========================================================
   SWAP LOCATIONS
========================================================= */

function swapLocations() {

    const fromInput =
        document.getElementById("from");

    const toInput =
        document.getElementById("to");

    if (!fromInput || !toInput) return;


    const temp =
        fromInput.value;

    fromInput.value =
        toInput.value;

    toInput.value =
        temp;


    currentFrom =
        fromInput.value;

    currentTo =
        toInput.value;

}


/* =========================================================
   JOURNEY TIME
========================================================= */

function setJourneyTime(option, button) {

    document
        .querySelectorAll(".quick-time")
        .forEach(item => {

            item.classList.remove("active");

        });

    if (button) {

        button.classList.add("active");

    }


    if (option === "now") {

        const now =
            new Date();

        const dateInput =
            document.getElementById("journeyDate");

        const timeInput =
            document.getElementById("journeyTime");

        if (dateInput) {

            dateInput.value =
                now.toISOString().split("T")[0];

        }

        if (timeInput) {

            timeInput.value =
                now.toTimeString().slice(0, 5);

        }

    }

}


/* =========================================================
   TRANSPORT MODE
========================================================= */

function selectMode(mode) {

    selectedMode = mode;

    document
        .querySelectorAll(".transport-option")
        .forEach(item => {

            item.classList.remove("active");

        });


    const button =
        document.getElementById(
            mode + "Mode"
        );

    if (button) {

        button.classList.add("active");

    }

}


/* =========================================================
   NORMALIZE LOCATIONS
========================================================= */

function normalizeLocation(location) {

    if (!location) return "";

    const key =
        location.trim().toLowerCase();


    const aliases = {

        "pcmc":
            "PCMC Bhavan",

        "pcmc bhavan":
            "PCMC Bhavan",

        "shivaji nagar":
            "Shivajinagar",

        "shivajinagar":
            "Shivajinagar",

        "mana pa":
            "Ma.Na.Pa Main Bus Stand",

        "ma.na.pa":
            "Ma.Na.Pa Main Bus Stand",

        "ma.na.pa main bus stand":
            "Ma.Na.Pa Main Bus Stand",

        "bhosari":
            "Bhosari Terminal"

    };


    return aliases[key] ||
        location.trim();

}


/* =========================================================
   MATCH STOP
   Handles Shivajinagar / Lokmangal Office etc.
========================================================= */

function locationsMatch(stop, location) {

    const stopText =
        stop.toLowerCase().trim();

    const locationText =
        location.toLowerCase().trim();


    if (stopText === locationText) {
        return true;
    }


    if (
        stopText.includes(locationText) ||
        locationText.includes(stopText)
    ) {
        return true;
    }


    const aliases = {

        "shivajinagar": [
            "lokmangal office"
        ],

        "lokmangal office": [
            "shivajinagar"
        ],

        "pcmc bhavan": [
            "pcmc"
        ]

    };


    if (
        aliases[locationText] &&
        aliases[locationText].some(
            alias => stopText.includes(alias)
        )
    ) {
        return true;
    }


    return false;

}


/* =========================================================
   GET STOPS BETWEEN
========================================================= */

function getStopsBetween(stops, from, to) {

    const fromIndex =
        stops.findIndex(stop =>
            locationsMatch(stop, from)
        );


    const toIndex =
        stops.findIndex(stop =>
            locationsMatch(stop, to)
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


/* =========================================================
   FIND METRO ROUTES
========================================================= */

function findMetroRoutes(from, to) {

    const routes = [];


    metroLines.forEach(line => {

        const routeStops =
            getStopsBetween(
                line.stations,
                from,
                to
            );


        if (routeStops) {

            routes.push({

                type: "metro",

                title:
                    line.name,

                subtitle:
                    line.color,

                stops:
                    routeStops

            });

        }

    });


    return routes;

}


/* =========================================================
   FIND BUS ROUTES
========================================================= */

function findBusRoutes(from, to) {

    const routes = [];


    busRoutes.forEach(route => {

        const routeStops =
            getStopsBetween(
                route.stops,
                from,
                to
            );


        if (routeStops) {

            routes.push({

                type: "bus",

                title:
                    "PMPML Bus " +
                    route.number,

                subtitle:
                    route.name,

                stops:
                    routeStops

            });

        }

    });


    return routes;

}


/* =========================================================
   CALCULATE DURATION
========================================================= */

function estimateDuration(stopCount, mode) {

    if (mode === "metro") {

        return Math.max(
            5,
            stopCount * 3
        );

    }


    return Math.max(
        8,
        stopCount * 4
    );

}


/* =========================================================
   CALCULATE FARE
========================================================= */

function estimateFare(stopCount, mode) {

    if (mode === "metro") {

        return Math.min(
            60,
            10 + stopCount * 3
        );

    }


    return Math.min(
        50,
        8 + stopCount * 2
    );

}


/* =========================================================
   CROWD PREDICTION
========================================================= */

function getCrowd(stopCount, mode) {

    let percentage;


    if (mode === "metro") {

        percentage =
            Math.min(
                85,
                30 + stopCount * 4
            );

    } else {

        percentage =
            Math.min(
                90,
                35 + stopCount * 3
            );

    }


    let level;


    if (percentage < 45) {

        level = "Low";

    } else if (percentage < 70) {

        level = "Medium";

    } else {

        level = "High";

    }


    return {

        percentage,
        level

    };

}


/* =========================================================
   SIGNAL COUNT
========================================================= */

function getSignalCount(stopCount, mode) {

    if (mode === "metro") {

        return 0;

    }


    return Math.max(
        1,
        Math.floor(stopCount / 2)
    );

}


/* =========================================================
   BUILD ROUTES
========================================================= */

function buildRoutes(from, to) {

    let routes = [];


    if (
        selectedMode === "auto" ||
        selectedMode === "metro"
    ) {

        routes = routes.concat(
            findMetroRoutes(from, to)
        );

    }


    if (
        selectedMode === "auto" ||
        selectedMode === "bus"
    ) {

        routes = routes.concat(
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


/* =========================================================
   PLAN JOURNEY
========================================================= */

function planJourney() {

    const fromInput =
        document.getElementById("from");

    const toInput =
        document.getElementById("to");


    if (!fromInput || !toInput) return;


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


    if (
        !currentFrom ||
        !currentTo
    ) {

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


    const routeFrom =
        document.getElementById("routeFrom");

    const routeTo =
        document.getElementById("routeTo");


    if (routeFrom) {
        routeFrom.textContent = currentFrom;
    }

    if (routeTo) {
        routeTo.textContent = currentTo;
    }


    if (routes.length === 0) {

        const container =
            document.getElementById(
                "routesContainer"
            );

        if (container) {

            container.innerHTML = `

                <div class="empty-state">

                    <div>🗺️</div>

                    <h2>
                        No Direct Route Found
                    </h2>

                    <p>
                        Try locations available on
                        Pune Metro or PMPML Route 121.
                    </p>

                </div>

            `;

        }


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


/* =========================================================
   DISPLAY ROUTES
========================================================= */

function displayRoutes(routes) {

    const container =
        document.getElementById(
            "routesContainer"
        );

    if (!container) return;


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

                                : "🚌 PMPML Route 121"

                            }

                        </span>

                    </div>


                    <button
                        class="view-stops-button"
                        onclick="selectDynamicRoute(${route.id}); openStations();"
                    >
                        📍 View Stops
                    </button>

                </div>

            `;

        }).join("");

}


/* =========================================================
   SORT ROUTES
========================================================= */

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
                ) return -1;

                if (
                    b.type === "metro" &&
                    a.type !== "metro"
                ) return 1;

                return 0;

            }
        );

    }


    return copiedRoutes;

}


/* =========================================================
   FILTER
========================================================= */

function applyFilter(preference, button) {

    selectedPreference =
        preference;


    document
        .querySelectorAll(".filter-button")
        .forEach(item => {

            item.classList.remove("active");

        });


    if (button) {

        button.classList.add("active");

    }


    if (
        window.availableRoutes &&
        window.availableRoutes.length > 0
    ) {

        displayRoutes(
            window.availableRoutes
        );

        updateRecommendation(
            window.availableRoutes
        );

        displayComparison(
            window.availableRoutes
        );

    }

}


/* =========================================================
   RECOMMENDATION
========================================================= */

function updateRecommendation(routes) {

    if (!routes || routes.length === 0) return;


    const bestRoute =
        sortRoutes(
            routes,
            selectedPreference
        )[0];


    const title =
        document.getElementById(
            "recommendationTitle"
        );

    const description =
        document.getElementById(
            "recommendationDescription"
        );

    const time =
        document.getElementById(
            "recommendationTime"
        );

    const fare =
        document.getElementById(
            "recommendationFare"
        );

    const crowd =
        document.getElementById(
            "recommendationCrowd"
        );


    if (title) {

        title.textContent =
            bestRoute.title;

    }


    let text = "";


    if (selectedPreference === "fastest") {

        text =
            "This route gives you the shortest estimated travel time.";

    } else if (selectedPreference === "cheapest") {

        text =
            "This route offers the lowest estimated fare.";

    } else if (selectedPreference === "crowd") {

        text =
            "This route is expected to have comparatively less crowd.";

    } else if (selectedPreference === "eco") {

        text =
            "This route is recommended as a more eco-friendly option.";

    }


    if (description) {
        description.textContent = text;
    }

    if (time) {
        time.textContent =
            bestRoute.duration + " min";
    }

    if (fare) {
        fare.textContent =
            "₹" + bestRoute.fare;
    }

    if (crowd) {
        crowd.textContent =
            bestRoute.crowd.level;
    }

}


/* =========================================================
   COMPARISON
========================================================= */

function displayComparison(routes) {

    const container =
        document.getElementById(
            "comparisonContainer"
        );

    if (!container) return;


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
                        <strong>${route.stopCount}</strong>
                    </div>

                    <div>
                        <span>Time</span>
                        <strong>${route.duration}m</strong>
                    </div>

                    <div>
                        <span>Fare</span>
                        <strong>₹${route.fare}</strong>
                    </div>

                    <div>
                        <span>Crowd</span>
                        <strong>${route.crowd.level}</strong>
                    </div>

                </div>

            </div>

        `).join("");

}


/* =========================================================
   SELECT ROUTE
========================================================= */

function selectDynamicRoute(routeId) {

    selectedRoute =
        window.availableRoutes.find(
            route => route.id === routeId
        );


    if (!selectedRoute) return;


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

            <div>

                <strong>
                    ${selectedRoute.title} Selected!
                </strong>

                <p>
                    Your route is ready for booking.
                </p>

                <button
                    onclick="openConfirmTicket()"
                >
                    🎫 Book Ticket
                </button>

            </div>

        `;

    }

}


/* =========================================================
   RESET JOURNEY ALERT
========================================================= */

function resetJourneyAlert() {

    const journeyAlert =
        document.getElementById(
            "journeyAlert"
        );

    if (!journeyAlert) return;


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


/* =========================================================
   ENSURE ROUTE SELECTED
========================================================= */

function ensureRouteSelected() {

    if (!selectedRoute) {

        alert(
            "Please select a route first."
        );

        return false;

    }

    return true;

}


/* =========================================================
   OPEN STATIONS / STOPS
========================================================= */

function openStations() {

    closeMenu();

    if (!ensureRouteSelected()) return;


    const title =
        document.getElementById(
            "stationSummaryTitle"
        );

    const text =
        document.getElementById(
            "stationSummaryText"
        );

    const stationList =
        document.getElementById(
            "stationList"
        );


    if (title) {

        title.textContent =
            selectedRoute.title;

    }


    if (text) {

        text.textContent =
            currentFrom +
            " → " +
            currentTo +
            " • " +
            selectedRoute.stopCount +
            " stops";

    }


    if (stationList) {

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

    }


    showScreen("stations");

}


/* =========================================================
   LIVE TRACKING + ETA
========================================================= */

function openTracking() {

    closeMenu();

    if (!ensureRouteSelected()) return;


    const icon =
        selectedRoute.type === "metro"
            ? "🚇"
            : "🚌";


    const vehicle =
        document.getElementById(
            "trackingVehicle"
        );

    const route =
        document.getElementById(
            "trackingRoute"
        );

    const etaElement =
        document.getElementById(
            "trackingETA"
        );


    if (vehicle) {

        vehicle.textContent =
            icon +
            " " +
            selectedRoute.title;

    }


    if (route) {

        route.textContent =
            currentFrom +
            " → " +
            currentTo;

    }


    const eta =
        Math.max(
            2,
            Math.floor(
                selectedRoute.duration / 3
            )
        );


    if (etaElement) {

        etaElement.textContent =
            eta + " min";

    }


    showScreen("tracking");

}


/* =========================================================
   CROWD PREDICTION
========================================================= */

function openCrowdPrediction() {

    closeMenu();

    if (!ensureRouteSelected()) return;


    const percentage =
        selectedRoute.crowd.percentage;

    const level =
        selectedRoute.crowd.level;


    const percentageElement =
        document.getElementById(
            "crowdPercentage"
        );

    const details =
        document.getElementById(
            "crowdPercentageDetails"
        );

    const status =
        document.getElementById(
            "crowdStatus"
        );

    const routeName =
        document.getElementById(
            "crowdRouteName"
        );


    if (percentageElement) {

        percentageElement.textContent =
            percentage + "%";

    }


    if (details) {

        details.textContent =
            percentage +
            "% - " +
            level;

    }


    if (status) {

        status.textContent =
            level +
            " Crowd Expected";

    }


    if (routeName) {

        routeName.textContent =
            selectedRoute.title;

    }


    const circle =
        document.querySelector(
            ".crowd-circle"
        );


    if (circle) {

        const degrees =
            percentage * 3.6;

        circle.style.background =
            `conic-gradient(
                var(--orange) 0deg ${degrees}deg,
                #e8edf0 ${degrees}deg 360deg
            )`;

    }


    showScreen("crowd");

}


/* =========================================================
   OPEN CONFIRM TICKET
========================================================= */

function openConfirmTicket() {

    closeMenu();

    if (!ensureRouteSelected()) return;


    const setText = (id, value) => {

        const element =
            document.getElementById(id);

        if (element) {
            element.textContent = value;
        }

    };


    setText(
        "confirmFrom",
        currentFrom
    );

    setText(
        "confirmTo",
        currentTo
    );

    setText(
        "confirmMode",

        selectedRoute.type === "metro"
            ? "Pune Metro"
            : "PMPML Bus"
    );

    setText(
        "confirmVehicle",
        selectedRoute.title
    );

    setText(
        "confirmTime",
        selectedRoute.duration +
        " minutes"
    );

    setText(
        "confirmFare",
        "₹" + selectedRoute.fare
    );


    showScreen("confirmTicket");

}


/* =========================================================
   PAYMENT
========================================================= */

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


/* =========================================================
   CONFIRM TICKET
========================================================= */

function confirmTicket() {

    if (!ensureRouteSelected()) return;


    const journeyDate =
        document.getElementById(
            "journeyDate"
        )?.value || "";


    const journeyTime =
        document.getElementById(
            "journeyTime"
        )?.value || "";


    const ticket = {

        id:
            Date.now(),

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
            "Confirmed"

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
        ticket.from +
        " → " +
        ticket.to +
        "\nFare: ₹" +
        ticket.fare
    );


    showScreen("tickets");

}


/* =========================================================
   RENDER TICKETS
========================================================= */

function renderTickets() {

    const container =
        document.getElementById(
            "ticketsContainer"
        );

    if (!container) return;


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
                        <strong>Ticket ID</strong>
                        <span>${ticket.ticketNumber}</span>
                    </p>

                    <p>
                        <strong>Journey</strong>

                        <span>
                            ${ticket.from}
                            →
                            ${ticket.to}
                        </span>
                    </p>

                    <p>
                        <strong>Date</strong>
                        <span>${ticket.journeyDate || "Today"}</span>
                    </p>

                    <p>
                        <strong>Time</strong>
                        <span>${ticket.journeyTime || "-"}</span>
                    </p>

                    <p>
                        <strong>Travel Time</strong>
                        <span>${ticket.time} minutes</span>
                    </p>

                    <p>
                        <strong>Payment</strong>

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
                    >
                        Cancel Ticket
                    </button>

                </div>

            </div>

        `).join("");

}


/* =========================================================
   DELETE TICKET
========================================================= */

function deleteTicket(ticketId) {

    const confirmDelete =
        confirm(
            "Are you sure you want to cancel this ticket?"
        );


    if (!confirmDelete) return;


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


/* =========================================================
   RECENT JOURNEYS
========================================================= */

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


/* =========================================================
   RENDER RECENT JOURNEYS
========================================================= */

function renderRecentJourneys() {

    const container =
        document.getElementById(
            "recentJourneys"
        );

    if (!container) return;


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


/* =========================================================
   USE RECENT JOURNEY
========================================================= */

function useRecentJourney(index) {

    const journey =
        recentJourneys[index];

    if (!journey) return;


    const fromInput =
        document.getElementById("from");

    const toInput =
        document.getElementById("to");


    if (fromInput) {

        fromInput.value =
            journey.from;

    }


    if (toInput) {

        toInput.value =
            journey.to;

    }


    currentFrom =
        journey.from;

    currentTo =
        journey.to;


    planJourney();

}


/* =========================================================
   FILTER DROPDOWN SUPPORT
========================================================= */

function toggleFilterDropdown() {

    const dropdown =
        document.getElementById(
            "filterDropdown"
        );

    if (dropdown) {

        dropdown.classList.toggle("show");

    }

}


/* =========================================================
   CHECKBOX FILTER SUPPORT
========================================================= */

function applyCheckboxFilters() {

    const fastest =
        document.getElementById("fastestFilter");

    const cheapest =
        document.getElementById("cheapestFilter");

    const crowd =
        document.getElementById("crowdFilter");

    const eco =
        document.getElementById("ecoFilter");


    if (
        fastest &&
        fastest.checked
    ) {

        selectedPreference = "fastest";

    }

    else if (
        cheapest &&
        cheapest.checked
    ) {

        selectedPreference = "cheapest";

    }

    else if (
        crowd &&
        crowd.checked
    ) {

        selectedPreference = "crowd";

    }

    else if (
        eco &&
        eco.checked
    ) {

        selectedPreference = "eco";

    }


    if (
        window.availableRoutes &&
        window.availableRoutes.length > 0
    ) {

        displayRoutes(
            window.availableRoutes
        );

        updateRecommendation(
            window.availableRoutes
        );

        displayComparison(
            window.availableRoutes
        );

    }


    const dropdown =
        document.getElementById(
            "filterDropdown"
        );

    if (dropdown) {

        dropdown.classList.remove("show");

    }

}