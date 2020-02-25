<template>
    <v-dialog eager v-model="dialog" persistent max-width="500px">
        <v-card>
            <v-card-title>
                <span class="headline">{{ formTitle }}</span>
            </v-card-title>

            <v-card-text>
                <v-form ref="propertiesForm">
                    <v-row justify="center">
                        <v-col class="pa-0 pr-3 pl-3">
                            <v-text-field :disabled="isEditing" :rules="[validation.required()]"
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
                                      :rules="[validation.required()]"
                                      label="Type of Value" clearable required></v-select>
                        </v-col>
                    </v-row>
                    <v-row>
                        <v-col class="pa-0 pr-3 pl-3">
                            <v-text-field v-model="editedItem.default" label="Default Value"
                                          :rules="[validation.required()]"
                                          :type="editedItem.typeOfValue === 'Number'? 'number': 'text'"
                                          clearable required>
                            </v-text-field>
                        </v-col>
                    </v-row>
                </v-form>
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
    import validation from '../../../services/validation.service';

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
        created() {
            this.validation.changeLanguage('en');
        },
        computed: {
            properties() {
                return this.$store.getters.getProperties;
            },
            formTitle() {
                return (!this.isEditing) ? 'Add Property' : 'Edit Property';
            }
        },
        methods: {
            openDialog(e) {
                this.resetValidation();
                this.editedItem = {
                    label: '',
                    typeOfValue: '',
                    color: "#F44336"
                };
                if (e) {
                    this.editedItem = Object.assign({}, e);
                }
                this.dialog = true;
            },
            close() {
                this.resetValidation();
                this.dialog = false;
            },
            resetValidation() {
                this.$refs.propertiesForm.resetValidation();
            },
            save() {
                if (this.$refs.propertiesForm.validate()) {
                    if (this.isEditing) {
                        this.$store.commit("editProperty", this.editedItem);
                        this.isEditing = false;
                    } else {
                        this.$store.commit("addProperty", this.editedItem);
                    }
                    this.close();
                }
            },
        }
    }
</script>

<style scoped>

</style>