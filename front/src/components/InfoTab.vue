<template>
    <v-layout column pa-2>
        <div class="font-weight-light black--text title ma-2">Selected Polygons</div>
        <v-layout column>
            <v-card v-for="(item, index) in selectedPolygons" :key="index">
                <v-card-title primary-title>
                    <div class="text-centered font-weight-light grey--text title mb-2">{{item.label}}</div>
                    <v-spacer/>
                </v-card-title>
                <v-layout column>
                    <v-flex pl-3 pr-3 v-for="(property, index2) in item.properties" :key="index2">
                        <v-text-field v-model="property.value" outline :background-color="properties[property.key].color"
                                      :type="properties[property.key].typeOfValue === 'Number'? 'number': 'text'"
                                      :label="properties[property.key].label" clearable required>
                        </v-text-field>
                    </v-flex>
                    <v-flex v-show="item.properties == null || item.properties.length === 0"
                            class="text-centered font-weight-light grey--text mb-2">
                        <h4>No properties assigned yet</h4>
                    </v-flex>
                </v-layout>
            </v-card>
        </v-layout>
        <v-flex v-show="selectedPolygons == null || selectedPolygons.length === 0"
                class="text-centered font-weight-light grey--text mb-2">
            <h4>No polygons selected</h4>
        </v-flex>
    </v-layout>
</template>

<script>
    export default {
        name: "InfoTab",
        props: {
            selectedPolygons: {
                type: Array,
                default: function () {
                    return []
                }
            }
        },
        data() {
            return {
                properties: {}
            }
        },
        created() {
            this.properties = JSON.parse(localStorage.getItem('properties'));
        },
        methods: {
        }
    }
</script>

<style scoped>
    .center-flex{
        display: flex;
        justify-content: center;
    }

    .text-centered{
        text-align: center;
    }
</style>