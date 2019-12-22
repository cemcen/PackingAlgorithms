
<template>
    <div class="page-container">
        <router-view></router-view>
    </div>
</template>

<script>
    import Navigation from "./Navigation.vue";

    export default {
        components: {
            Navigation
        },
        created() {
        },
        data(){
            return {
                name : "App"
            }
        },
        methods: {
        }
    }
</script>

<style>

    .page-container{
        background-color: #ffffff;
    }
</style>

