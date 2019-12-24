import Vue from 'vue';
import VueRouter from 'vue-router'
import Vuetify from 'vuetify';
import VueResource from 'vue-resource'

import router from './router'
import VeeValidate from 'vee-validate'
import App from './components/App.vue';


Vue.use(VeeValidate);
Vue.use(VueResource);
Vue.use(VueRouter);
Vue.use(Vuetify);

let vuetify = new Vuetify({
    icons: {
        iconfont: 'mdi',
    },
});

const app = new Vue({
    vuetify,
    router,
    el: '#app',
    render: h => h(App)
});

import '../sass/style.scss';