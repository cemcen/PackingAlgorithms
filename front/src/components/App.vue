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
        data(){
            return {
                name : "App"
            }
        },
        methods: {
        }
    }
</script>