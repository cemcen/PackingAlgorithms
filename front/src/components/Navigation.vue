<template>
    <div id="app">
        <v-app id="inspire">
            <v-app-bar app dense color="teal lighten-2" dark>
                <v-toolbar-title>Convex Polygon Packing</v-toolbar-title>
            </v-app-bar>
            <v-content>
                <v-row style="height: 93vh">
                    <v-col style="height: 93vh">
                        <packing-tab></packing-tab>
                    </v-col>
                </v-row>
            </v-content>
        </v-app>
    </div>
</template>


<script>
    import PackingTab from "./pages/PackingTab.vue";

    export default {
        components: {PackingTab},
        data() {
            return {
                drawer: false,
                script: null,
                ps: null
            }
        },
        created() {
        },
        methods: {},
        props: {
            source: String
        },
    }
</script>

<style scoped>
</style>