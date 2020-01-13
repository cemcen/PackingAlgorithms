<template>
    <v-dialog v-model="dialog" persistent max-width="500px">
        <v-card>
            <v-card-title>
                <span class="headline">{{ formTitle }}</span>
            </v-card-title>

            <v-card-text>
                <v-row justify="center">
                    <v-col class="pa-0 pr-3 pl-3">
                        <v-text-field :disabled="isEditing"
                                      v-model="editedItem.label" label="Label" clearable required>
                        </v-text-field>
                    </v-col>
                </v-row>
                <v-row>
                    <v-col class="pa-0 pr-3 pl-3">
                        <swatches v-model="editedItem.color" colors="material-basic" inline/>
                    </v-col>
                </v-row>
                <v-row>
                    <v-col class="pa-0 pr-3 pl-3">
                        <v-select v-model="editedItem.typeOfValue" :items="typeOfValues"
                                  label="Type of Value" clearable required></v-select>
                    </v-col>
                </v-row>
                <v-row>
                    <v-col class="pa-0 pr-3 pl-3">
                        <v-text-field v-model="editedItem.default" label="Default Value"
                                      :type="editedItem.typeOfValue === 'Number'? 'number': 'text'"
                                      clearable required>
                        </v-text-field>
                    </v-col>
                </v-row>
            </v-card-text>

            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="teal lighten-2" text @click.native="close">Cancel</v-btn>
                <v-btn dark color="teal lighten-2" @keyup.enter="save" @click.native="save">Save</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    import Swatches from 'vue-swatches';
    import "vue-swatches/dist/vue-swatches.min.css"
    import validation from './../../services/validation.service';
    export default {
        name: "PropertiesForm",
        props: {
            isEditing: {
                type: Boolean,
                defaultValue: false,
            },
        },
        components: {
            Swatches,
        },
        data() {
            return {
                dialog: false,
                validation: validation,
                typeOfValues: ['Number', 'String'],
                editedItem: {
                    label: '',
                    typeOfValue: '',
                    color: "#F44336"
                },
            }
        },
        computed: {
            properties () {
                return this.$store.getters.getProperties;
            },
            formTitle() {
                return (!this.isEditing)? 'Add Property' : 'Edit Property';
            }
        },
        methods: {
            openDialog(e){
                this.editedItem = {
                    label: '',
                    typeOfValue: '',
                    color: "#F44336"
                };
                if(e) {
                   this.editedItem = Object.assign({}, e);
                }
                this.dialog = true;
            },
            close() {
                this.dialog = false;
            },
            save(){
                this.$validator.validateAll().then(result => {
                    if (result) {
                        if(this.isEditing) {
                            this.$store.commit("editProperty", this.editedItem);
                            this.isEditing = false;
                        } else {
                            this.$store.commit("addProperty", this.editedItem);
                        }
                        this.close();
                    }
                });
            },
        }
    }
</script>

<style scoped>

</style>