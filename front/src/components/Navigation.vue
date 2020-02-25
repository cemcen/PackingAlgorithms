<template>
    <v-app id="inspire">
        <v-navigation-drawer app :clipped="$vuetify.breakpoint.lgAndUp" v-model="drawer" fixed
                             width="240">
            <v-list>
                <v-list-item v-for="(item, index) in items" :key="item.text" @click="selectTab(index,item.route)"
                             :dark="url == item.route"
                             :class="url == item.route ? 'primary' : ''">
                    <v-list-item-avatar>
                        <v-icon>{{item.icon}}</v-icon>
                    </v-list-item-avatar>
                    <v-list-item-content>
                        {{ item.text }}
                    </v-list-item-content>
                </v-list-item>
            </v-list>
        </v-navigation-drawer>
        <v-app-bar :clipped-left="$vuetify.breakpoint.lgAndUp" dense color="primary" dark app>
            <v-app-bar-nav-icon @click.stop="drawer = !drawer">
            </v-app-bar-nav-icon>
            <v-toolbar-title>Convex Polygon Packing</v-toolbar-title>
        </v-app-bar>
        <v-content>
            <v-row style="height: 93vh">
                <v-col style="height: 93vh">
                    <router-view></router-view>
                </v-col>
            </v-row>
        </v-content>
    </v-app>
</template>


<script>

    export default {
        data() {
            return {
                drawer: true,
                items: [
                    {icon: 'mdi-shape-plus', text: 'Packing', route: "/packing"},
                    {icon: 'mdi-shape-polygon-plus', text: 'Polygons', route: "/polygons"},
                    {icon: 'mdi-palette', text: 'Properties', route: "/properties"},
                    {icon: 'mdi-selection-drag', text: 'Boundary Conditions', route: "/boundary"},
                    {icon: 'mdi-shape-square-plus', text: 'Add Points', route: "/more-points"},
                    {icon: 'mdi-chart-scatter-plot-hexbin', text: 'Upload Results', route: "/upload-results"},
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