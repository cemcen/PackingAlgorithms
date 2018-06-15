<template>
    <div class="page-container">
        <md-app md-waterfall md-mode="fixed">
            <md-app-toolbar class="md-primary">
                <img class="logo-image" src="assets/images/tetris.png"/>
                <md-tabs class="md-primary md-transparent">
                    <md-tab id="tab-home" md-label="Packing"></md-tab>
                </md-tabs>
            </md-app-toolbar>

            <md-app-content>
                <md-content ref="drawerContainer" class="drawer">
                    <div ref="myDrawer"></div>
                </md-content>
                <div class="options">
                    <md-tabs md-alignment="fixed">
                        <md-tab id="tab-polygon" @click="selectTab(1)" md-icon="/assets/images/square.svg"></md-tab>
                        <md-tab id="tab-distribution" @click="selectTab(0)"
                                md-icon="/assets/images/line-chart.svg"></md-tab>
                    </md-tabs>
                    <router-view/>
                </div>
            </md-app-content>
        </md-app>
    </div>
</template>

<script>
    const routes = ["/probability", "/polygons"];

    export default {
        data(){
            return {
                selectedTab: 0,
                script: null,
                ps: null
            }
        },
        created(){

        },
        mounted() {
            this.script = p => {
                let canvas = null;

                // Settings of the canvas.
                p.setup = () => {
                    // We use the div size as the canvas size.
                    canvas = p.createCanvas(this.$refs.drawerContainer.$el.clientWidth,this.$refs.drawerContainer.$el.clientHeight);
                    canvas.parent(this.$refs.myDrawer);
                    // Amount of frames per second, how many times per second it's drawn.
                    p.frameRate(60)
                };

                // What's been drawn on the canvas
                p.draw = () => {
                    //p.background(255);
                    /*if (p.mouseIsPressed) {
                        p.fill(0);
                    } else {
                        p.fill(255);
                    }*/
                    //p.rect(100, 100, 50, 50)
                };
            };
            const P5 = require('p5');
            this.ps = new P5(this.script);
            //console.log(this.ps);
            this.selectTab(1);
        },
        methods: {
            selectTab(i){
                this.selectedTab = i;
                this.$router.push(routes[i]);
            },
        }
    }
</script>

<style scoped lang="scss">
    .logo-image{
        width: 2em;
    }

    .drawer{
        min-width: 75%;
    }

    .options{
        min-height: 35em;
        border: 1px solid rgba(#000, .12);
    }

    .md-app-content{
        justify-content: center;
        align-items: center;
        width: 100%;
        min-height: 35em;
    }
    .md-content {
        min-height: 35em;
        display: inline-flex;
        border: 1px solid rgba(#000, .12);
    }
</style>