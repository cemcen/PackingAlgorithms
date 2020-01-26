<template>
    <v-dialog eager v-model="dialog" persistent max-width="500px">
        <v-card>
            <v-card-title>
                <span class="headline">More Border Points</span>
            </v-card-title>

            <v-card-text>
                <v-form ref="addBorderPointsForm">
                    <v-row justify="center">
                        <v-col class="pa-0 pr-3 pl-3">
                            <v-select label="Choose which segments should be divided"
                                      item-text="name"
                                      v-model="selectedOptionType" :items="optionType"
                                      return-object>
                            </v-select>
                        </v-col>
                    </v-row>
                    <v-row justify="center">
                        <v-col class="pa-0 pr-3 pl-3">
                            <v-text-field v-model="divisionTimes" label="Division times" type="number"
                                          :rules="[validation.required(), validation.greaterThanAndPositive(1)]"
                                          clearable required>
                            </v-text-field>
                        </v-col>
                    </v-row>
                </v-form>
            </v-card-text>

            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="teal lighten-2" text @click.native="dialog = false">Close
                </v-btn>
                <v-btn dark color="teal lighten-2" @keyup.enter="addBorderPoints"
                       @click.native="addBorderPoints">Add points
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    import validation from './../../services/validation.service';

    export default {
        name: "AddBorderPoints",
        props: {
            bs: Array,
        },
        data() {
            return {
                dialog: false,
                validation: validation,
                divisionTimes: 2,
                selectedOptionType: {
                    value: 0,
                    name: "All Segments"
                },
                optionType: [
                    {
                        value: 0,
                        name: "All Segments"
                    },
                    {
                        value: 1,
                        name: "Only selected ones"
                    }
                ],
            }
        },
        computed: {
            packing () {
                return this.$store.getters.getPacking;
            }
        },
        methods: {
            openDialog() {
                this.divisionTimes = 2;
                this.dialog = true;
            },
            addBorderPoints() {
                if(this.$refs.addBorderPointsForm.validate()) {
                    this.bs.forEach(bsI => {
                        if(bsI.isSelected()) {
                            let polygon = this.packing.polygons[bsI.getPolygonIndex()];
                            let indices = [];
                            polygon.points.forEach((pnt, index) => {
                                if(bsI.contains(pnt)) {
                                    indices.push(index);
                                }
                            });

                            if(indices.length === 2) {
                                let pntA = indices[0];
                                let pntB = indices[1];
                                if(indices[0] > indices[1] || (indices[0] === 0 && indices[1] !== 1)) {
                                    pntA = indices[1];
                                    pntB = indices[0];
                                }

                                let x1 = polygon.points[pntA].x;
                                let y1 = polygon.points[pntA].y;
                                let x2 = polygon.points[pntB].x;
                                let y2 = polygon.points[pntB].y;

                                let index = pntB;

                                Array(this.divisionTimes - 1).fill().map((_, i) => {
                                    let pnt = {
                                        x: x1 + ((i + 1)/this.divisionTimes)*(x2 - x1),
                                        y: y1 + ((i + 1)/this.divisionTimes)*(y2 - y1),
                                    };


                                    if(pntB === 0) {
                                        polygon.points.push(pnt);
                                    }
                                    else {
                                        polygon.points.splice(index, 0, pnt);
                                        index += 1;
                                    }
                                });
                            }
                        }
                    });
                    this.$emit("changedBoundary");
                    this.dialog = false;
                }

            }
        }

    }
</script>

<style scoped>

</style>