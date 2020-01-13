import Vue from 'vue'
import Vuex from 'vuex'
import VuexPersistence from 'vuex-persist'

Vue.use(Vuex);

const vuexLocal = new VuexPersistence({
    storage: window.localStorage
});

const store = new Vuex.Store({
    state: {
        properties: window.localStorage.getItem('properties')? JSON.parse(window.localStorage.getItem('properties')): {},
        border: window.localStorage.getItem('properties')? JSON.parse(window.localStorage.getItem('properties')): {},
    },
    mutations: {
        deleteProperty: (state, item) => {
            Vue.delete(state.properties, state.properties[item].label);
            window.localStorage.setItem('properties', JSON.stringify(state.properties));
        },
        editProperty: (state, item) => {
            Vue.set(state.properties, item.label, item);
            window.localStorage.setItem('properties', JSON.stringify(state.properties));
        },
        addProperty: (state, item) => {
            Vue.set(state.properties, item.label, item);
            window.localStorage.setItem('properties', JSON.stringify(state.properties));
        }
    },
    getters: {
        getProperties: state => {
            return state.properties;
        },
    },
    plugins: [vuexLocal.plugin]
});

export default store