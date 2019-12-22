<template>
    <v-app id="inspire">
        <v-toolbar dark color="teal lighten-2" app fixed clipped-left>
            <v-toolbar-title>Convex Polygon Packing</v-toolbar-title>
        </v-toolbar>
        <v-content class="content-style">
            <v-layout class="all-height">
                <v-flex sm12 pa-2>
                    <packing-tab></packing-tab>
                </v-flex>
            </v-layout>
        </v-content>
    </v-app>
</template>


<script>
    import PackingTab from "./PackingTab.vue";

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
        methods: {
        },
        props: {
            source: String
        },
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