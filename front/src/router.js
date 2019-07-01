import VueRouter from 'vue-router';

import Navigation from './components/Navigation.vue'
import PolygonsTab from './components/PolygonsTab.vue'
import ProbabilityTab from './components/ProbabiltyTab.vue'
import PropertiesTab from './components/PropertiesTab.vue'
import PackingTab from './components/PackingTab.vue'
import InfoTab from './components/InfoTab.vue'


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
                    path: "properties",
                    component: PropertiesTab
                },
                {
                    path: "polygons",
                    component: PolygonsTab
                },
                {
                    path: "packing",
                    component: PackingTab
                },
                {
                    path: "info",
                    component: InfoTab
                },
            ]
        }
    ]
});

export default router;