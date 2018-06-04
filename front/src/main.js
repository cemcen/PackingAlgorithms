import Vue from 'vue';
import VueMaterial from 'vue-material'
import 'vue-material/dist/vue-material.min.css'

import App from './components/App.vue';

Vue.use(VueMaterial);

new Vue(App).$mount('#app');

import '../sass/style.scss';