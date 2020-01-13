import VueRouter from 'vue-router';

import Navigation from './components/Navigation.vue'
import PolygonsTab from './components/pages/PolygonsTab.vue'
import ProbabilityTab from './components/pages/ProbabiltyTab.vue'
import PropertiesTab from './components/pages/PropertiesTab.vue'
import PackingTab from './components/pages/PackingTab.vue'
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