import VueRouter from 'vue-router';

import Navigation from './components/Navigation.vue'
import Polygons from './components/pages/Polygons.vue'
import Properties from './components/pages/Properties.vue'
import Packing from './components/pages/Packing.vue'
import Boundary from "./components/pages/Boundary.vue";
import MorePoints from "./components/pages/MorePoints.vue";
import Results from "./components/pages/Results.vue";

const router = new VueRouter({
    mode: 'history',
    base: __dirname,
    routes: [
        {
            path: '/',
            component: Navigation,
            children: [
                {
                    path: "packing",
                    component: Packing
                },
                {
                    path: "polygons",
                    component: Polygons
                },
                {
                    path: "properties",
                    component: Properties
                },
                {
                    path: "boundary",
                    component: Boundary
                },
                {
                    path: "more-points",
                    component: MorePoints
                },
                {
                    path: "upload-results",
                    component: Results
                },
                { path: '*', redirect: '/packing' },
            ]
        }
    ]
});


export default router;