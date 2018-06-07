import VueRouter from 'vue-router';

import Navigation from './components/Navigation.vue'
import PolygonsTab from './components/PolygonsTab.vue'
import ProbabilityTab from './components/ProbabiltyTab.vue'


const router = new VueRouter({
    mode: 'history',
    base: __dirname,
    routes: [
        {
            path: '/',
            component: Navigation,
            children: [
                {
                    path: "probability",
                    component: ProbabilityTab
                },
                {
                    path: "polygons",
                    component: PolygonsTab
                },
            ]
        }
    ]
});

export default router;