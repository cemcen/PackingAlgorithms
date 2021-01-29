<template>
    <v-app id="inspire">
        <v-navigation-drawer app :clipped="$vuetify.breakpoint.lgAndUp" v-model="drawer" fixed
                             width="240">
            <v-list>
                <v-list-item v-for="(item, index) in items" :key="item.text" @click="selectTab(index,item.route)"
                             :dark="url == item.route"
                             :class="url == item.route ? 'primary' : ''">
                    <v-list-item-avatar size="25" tile>
                        <v-icon>{{item.icon}}</v-icon>
                    </v-list-item-avatar>
                    <v-list-item-content>
                        {{ item.text }}
                    </v-list-item-content>
                </v-list-item>
            </v-list>
        </v-navigation-drawer>
        <v-app-bar :clipped-left="$vuetify.breakpoint.lgAndUp" dense color="primary" dark app>
            <v-btn text icon @click="drawer = !drawer"><v-icon>{{menuIcon}}</v-icon></v-btn>
            <v-toolbar-title>Convex Polygon Packing</v-toolbar-title>
        </v-app-bar>
        <v-main>
            <v-row style="height: 93vh; margin: 0">
                <v-col style="height: 93vh">
                    <router-view></router-view>
                </v-col>
            </v-row>
        </v-main>
    </v-app>
</template>


<script>

    import { mdiMenu, mdiShapePlus, mdiShapePolygonPlus, mdiPalette, mdiSelectionDrag, mdiShapeSquarePlus, mdiChartScatterPlotHexbin } from '@mdi/js';

    export default {
        data() {
            return {
                menuIcon: mdiMenu,
                drawer: true,
                items: [
                    {icon: mdiShapePlus, text: 'Packing', route: "/packing"},
                    {icon: mdiShapePolygonPlus, text: 'Polygons', route: "/polygons"},
                    {icon: mdiPalette, text: 'Properties', route: "/properties"},
                    {icon: mdiSelectionDrag, text: 'Boundary Conditions', route: "/boundary"},
                    {icon: mdiShapeSquarePlus, text: 'Add Points', route: "/more-points"},
                    {icon: mdiChartScatterPlotHexbin, text: 'Upload Results', route: "/upload-results"},
                ],
                selectedTab: 0,
            }
        },
        created() {
        },
        methods: {
            selectTab(i, route) {
                this.selectedTab = i;
                this.$router.push(route);
            },
        },
        computed: {
            url() {
                return this.$route.path;
            }
        },
    }
</script>

<style scoped>
</style>