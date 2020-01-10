<template>
    <v-card color="#ffffff" class="ma-3 fill-height" style="max-height: 80%">
        <v-card-title>
            <span class="headline">Assign Properties</span>
        </v-card-title>

        <v-card-text style="max-height: 50%">
            <v-row justify="center">
                <v-col md="12">
                    <v-select label="All border elements or only selected ones."
                              v-model="selectedOptionProperties"
                              :items="optionProperties">
                    </v-select>
                </v-col>
            </v-row>
            <v-row justify="center">
                <v-col md="12">
                    <v-select label="All border elements, only segments or only vertices."
                              v-model="selectedOptionType" :items="optionType">
                    </v-select>
                </v-col>
            </v-row>
            <v-row justify="center">
                <v-list style="width: 100%" two-line>
                    <div class="text-centered font-weight-light grey--text title mb-2"
                         v-show="properties.length === 0">
                        No properties registered.
                    </div>
                    <template v-for="(key, value) in Object.entries(properties)">

                        <v-list-item :key="key[1].label">

                            <v-list-item-action>
                                <v-checkbox color="teal lighten-2"
                                            v-model="key[1].selected"></v-checkbox>
                            </v-list-item-action>

                            <v-list-item-avatar>
                                <swatches v-model="key[1].color" disabled class="mr-3"
                                          colors="material-basic"/>
                            </v-list-item-avatar>

                            <v-list-item-content>
                                <v-list-item-title v-html="key[1].label"></v-list-item-title>
                                <v-list-item-subtitle>
                                    <span>Type of value: {{ key[1].typeOfValue }}</span> <br/>
                                    <span>Default value: {{ key[1].default? key[1].default : 'Not Defined' }}</span>
                                </v-list-item-subtitle>
                            </v-list-item-content>
                        </v-list-item>
                        <v-divider></v-divider>
                    </template>
                </v-list>
            </v-row>
        </v-card-text>

        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn dark color="teal lighten-2" @click.native="assignProperties">Assign Properties</v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>
    import Swatches from 'vue-swatches';
    import "vue-swatches/dist/vue-swatches.min.css"
    export default {
        name: "BorderProperties",
        props: {
            properties: {
                type: Object,
                defaultValue: {},
            },
        },
        components: {
            Swatches
        },
        data() {
            return {
                selectedOptionProperties: "All",
                selectedOptionType: "All",
                optionProperties: ["All", "Only selected"],
                optionType: ["All", "All Nodes", "All Segments"],
            }
        },
    }
</script>

<style scoped>

</style>