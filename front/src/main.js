import Vue from 'vue';
import VueRouter from 'vue-router'
import VueMaterial from 'vue-material'
import VueResource from 'vue-resource'
import 'vue-material/dist/vue-material.min.css'

import router from './router'

import App from './components/App.vue';

Vue.use(VueMaterial);
Vue.use(VueResource);
Vue.use(VueRouter);

//new Vue(App).$mount('#app');
const app = new Vue({
    router,
    el: '#app',
    render: h => h(App)
});

import '../sass/style.scss';