import VueRouter from 'vue-router';

import Navigation from './components/Navigation.vue'
import PolygonsTab from './components/PolygonsTab.vue'
import ProbabilityTab from './components/ProbabiltyTab.vue'
import PackingTab from './components/PackingTab.vue'


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
                {
                    path: "packing",
                    component: PackingTab
                },
            ]
        }
    ]
});

export default router;