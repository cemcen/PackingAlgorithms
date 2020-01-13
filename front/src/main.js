import Vue from 'vue';
import VueRouter from 'vue-router'
import Vuetify from 'vuetify';
import VueResource from 'vue-resource'
import colors from 'vuetify/es5/util/colors'
import store from './store';
import VuetifyToast from 'vuetify-toast-snackbar'

import router from './router'
import VeeValidate from 'vee-validate'
import App from './components/App.vue';


Vue.use(VeeValidate);
Vue.use(VueResource);
Vue.use(VueRouter);
Vue.use(Vuetify);

Vue.use(VuetifyToast, {
    x: 'center',
    y: 'bottom',
    color: 'primary',
    timeout: 2000,
    property: '$toast' // default
});

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
    store,
    el: '#app',
    render: h => h(App)
});
