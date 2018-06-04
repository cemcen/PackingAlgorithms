<template>
    <div class="page-container">
        <md-app md-waterfall md-mode="fixed">
            <md-app-toolbar class="md-primary">
                <img class="logo-image" src="assets/images/tetris.png"/>
                <md-tabs class="md-primary md-transparent">
                    <md-tab id="tab-home" md-label="Packing"></md-tab>
                    <md-tab id="tab-pages" md-label="Analisis"></md-tab>
                </md-tabs>
            </md-app-toolbar>

            <md-app-content>
                <md-content v-on:click="addRectangle" class="drawer">
                    <div ref="myDrawer"></div>
                </md-content>
                <md-content class="options">
                    <md-tabs>
                        <md-tab id="tab-distribution" md-label="Distribución" md-icon="/assets/images/line-chart.svg">
                        </md-tab>
                        <md-tab id="tab-polygon" md-label="Poligonos" md-icon="/assets/images/square.svg"></md-tab>
                    </md-tabs>
                </md-content>
            </md-app-content>
        </md-app>
    </div>
</template>

<script>
    import 'two.js'

    export default {
        data(){
            return {
                twoL: null
            }
        },
        created(){

        },
        mounted() {
            this.twoL = new Two({
                fullscreen: true,
                autostart: true
            }).appendTo(this.$refs.myDrawer);
            let two = this.twoL;
            let rect = two.makeRectangle(two.width / 2, two.height / 2, 50 ,50);
            two.bind('update', function() {
                two.remove(rect);
                rect = two.makeRectangle(two.width / 2, two.height / 2, 50 ,50);
            });
        },
        methods: {
            addRectangle() {
                let rect = this.twoL.makeRectangle(this.twoL.width / 2, this.twoL.width / 2, 100 ,100);
                let two = this.twoL;
                two.bind('update', function() {
                    two.remove(rect);
                    rect = two.makeRectangle(two.width / 3, two.height / 3, 100 ,100);
                });
            }
        }
    }
</script>

<style scoped lang="scss">
    .logo-image{
        width: 2em;
    }

    .drawer{
        min-width: 80%;
    }

    .md-app-content{
        justify-content: center;
        align-items: center;
        width: 100%;
        min-height: 30em;
    }
    .md-content {
        min-height: 30em;
        display: inline-flex;
        border: 1px solid rgba(#000, .12);
    }
</style>