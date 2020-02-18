import Vue from 'vue'
import Vuex from 'vuex'
import VuexPersistence from 'vuex-persist'

Vue.use(Vuex);

const vuexLocal = new VuexPersistence({
    storage: window.localStorage
});

const store = new Vuex.Store({
    state: {
        properties: {},
        polygons: [],
        packing: {}
    },
    mutations: {
        loadPacking: (state, item) => {
            if(item) {
                state.properties = item.properties;
                state.polygons = item.polygons;
                state.packing = item.packing;
            }
        },
        deleteProperty: (state, item) => {
            if(item) {
                Vue.delete(state.properties, state.properties[item].label);
            }
        },
        editProperty: (state, item) => {
            if(item) {
                Vue.set(state.properties, item.label, item);
            }
        },
        editProperties: (state, item) => {
            if(item) {
                Object.assign(state.properties, item);
            }
        },
        addProperty: (state, item) => {
            if(item) {
                Vue.set(state.properties, item.label, item);
            }
        },
        deletePolygon: (state, item) => {
            if(item) {
                const index = state.polygons.indexOf(item);
                state.polygons.splice(index, 1);
            }
        },
        editPolygon: (state, data) => {
            if(data) {
                Vue.set(state.polygons, data.index, data.item);
            }
        },
        addPolygon: (state, item) => {
            if(item) {
                state.polygons.push(item);
            }
        },
        newPacking: (state, packing) => {
            if(packing) {
                 Object.assign(state.packing, packing);
                 Vue.set(state.packing, "originalPacking", JSON.parse(JSON.stringify(packing)))
            }
        },
        borderElements: (state, borderElements) => {
            if(borderElements) {
                 Vue.set(state.packing, "borderElements", borderElements);
            }
        },
        updatePacking: (state, changes) => {
            if(changes) {
                let draw = {};
                draw.points = changes.points;
                draw.edges = changes.edges;
                draw.polygons = changes.polygons;
                draw.borderPoints = changes.borderPoints;
                draw.borderSegments = changes.borderSegments;
                Vue.set(state.packing, "draw", draw);
                Vue.set(state.packing, "graph", changes.edgesG);
                Vue.set(state.packing, "rGraph", changes.cEdgesG);
            }
        },
        updateOnlyPackingPolygons: (state, polygons) => {
            if(polygons) {
                Vue.set(state.packing, "polygons", polygons);
            }
        },
        assignProperties: (state, changes) => {
            if(changes) {
                Vue.set(state.packing, "draw", changes.draw);
                Vue.set(state.packing, "polygons", changes.polygons);
            }
        },
        updateBorderConditions: (state, borderConditions) => {
            if(borderConditions) {
                Vue.set(state.packing.draw, "borderPoints", borderConditions.borderPointsArray);
                Vue.set(state.packing.draw, "borderSegments", borderConditions.borderSegmentsArray);
            }
        }
    },
    getters: {
        getProperties: state => {
            return state.properties;
        },
        getPolygons: state => {
            return state.polygons;
        },
        getPacking: state => {
            return state.packing;
        }
    },
    plugins: [vuexLocal.plugin]
});

export default store