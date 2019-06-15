<template>
    <v-app id="inspire">
        <v-toolbar color="teal lighten-2" app fixed clipped-left>
            <v-toolbar-title>Convex Polygon Packing</v-toolbar-title>
        </v-toolbar>
        <v-content class="content-style">
            <v-layout class="all-height">
                <v-flex sm9 pa-2>
                    <v-card class="my-card">
                        <Packing></Packing>
                    </v-card>
                </v-flex>
                <v-flex sm3 pa-2>
                    <v-card class="my-card">
                        <v-tabs show-arrows grow color="#eeeeee">
                            <v-tabs-slider color="teal lighten-2"></v-tabs-slider>

                            <v-tab id="tab-polygon" @click="selectTab(1)">
                                <v-icon>category</v-icon> Polygons
                            </v-tab>

                            <v-tab id="tab-distribution" @click="selectTab(0)">
                                <v-icon>list_alt</v-icon> Properties
                            </v-tab>
                        </v-tabs>
                        <router-view/>
                    </v-card>
                </v-flex>
            </v-layout>
        </v-content>
    </v-app>
</template>


<script>
    import Packing from "./PackingTab.vue";

    const routes = ["/properties", "/polygons"];

    export default {
        components: {Packing},
        data() {
            return {
                drawer: false,
                selectedTab: 0,
                script: null,
                ps: null
            }
        },
        created() {
            this.selectTab(1);
        },
        methods: {
            selectTab(i) {
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
    .content-style {
        background-color: #ffffff;
    }

    .all-height{
        height: 100%;
    }

    .my-card{
        height: 100%;
    }
</style>