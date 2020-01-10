import Vue from 'vue';
import VueRouter from 'vue-router'
import Vuetify from 'vuetify';
import VueResource from 'vue-resource'
import colors from 'vuetify/es5/util/colors'

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
    theme: {
        themes: {
            light: {
                primary: colors.teal.lighten2,
            },
            dark: {
                primary: colors.teal.lighten2,
            }
        },
    },
});

const app = new Vue({
    vuetify,
    router,
    el: '#app',
    render: h => h(App)
});
