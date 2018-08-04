<template>
    <v-app id="inspire" dark>
        <v-navigation-drawer v-model="drawer" clipped fixed app>
            <v-list dense>
                <v-list-tile @click="">
                    <v-list-tile-action>
                        <v-icon>dashboard</v-icon>
                    </v-list-tile-action>
                    <v-list-tile-content>
                        <v-list-tile-title>Dashboard</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>
                <v-list-tile @click="">
                    <v-list-tile-action>
                        <v-icon>settings</v-icon>
                    </v-list-tile-action>
                    <v-list-tile-content>
                        <v-list-tile-title>Settings</v-list-tile-title>
                    </v-list-tile-content>
                </v-list-tile>
            </v-list>
        </v-navigation-drawer>
        <v-toolbar app fixed clipped-left>
            <v-toolbar-side-icon @click.stop="drawer = !drawer"></v-toolbar-side-icon>
            <v-toolbar-title>Packing Geometrico</v-toolbar-title>
        </v-toolbar>
        <v-content>
            <v-container>
                <v-card>
                    <v-tabs centered color="teal darken-1" dark icons-and-text>
                        <v-tabs-slider color="teal lighten-4"></v-tabs-slider>

                        <v-tab id="tab-packing" @click="selectTab(2)" >
                            Packing
                            <v-icon>category</v-icon>
                        </v-tab>

                        <v-tab id="tab-polygon" @click="selectTab(1)">
                            Add Polygon
                            <v-icon>add_box</v-icon>
                        </v-tab>

                        <v-tab id="tab-distribution" @click="selectTab(0)">
                            Analysis
                            <v-icon>dashboard</v-icon>
                        </v-tab>
                    </v-tabs>
                    <router-view/>
                </v-card>
            </v-container>
        </v-content>
    </v-app>
</template>


<script>
    const routes = ["/probability", "/polygons", "/packing"];

    export default {
        data(){
            return {
                drawer: false,
                selectedTab: 0,
                script: null,
                ps: null
            }
        },
        created() {
            this.selectTab(2);
        },
        methods: {
            selectTab(i){
                this.selectedTab = i;
                this.$router.push(routes[i]);
            },
        },
        props: {
            source: String
        }
    }
</script>

<style scoped>

</style>