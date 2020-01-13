<template>
    <v-dialog eager v-model="dialog" persistent max-width="500px">
        <v-card>
            <v-card-title>
                <span class="headline">Import Packing</span>
            </v-card-title>

            <v-card-text>
                <v-form ref="fileImportForm">
                    <v-row justify="center">
                        <v-col class="pa-0 pr-3 pl-3">
                            <v-file-input v-model="file" accept=".txt,.json" prepend-icon="mdi-file-code" :rules="[validation.required()]" show-size label="Upload mesh file"></v-file-input>
                        </v-col>
                    </v-row>
                </v-form>
            </v-card-text>

            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn color="teal lighten-2" text @click.native="close">Cancel</v-btn>
                <v-btn dark color="teal lighten-2" @keyup.enter="loadPacking" @click.native="loadPacking">Load</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    import validation from './../../services/validation.service';

    export default {
        name: "ImportPacking",
        data() {
            return {
                dialog: false,
                validation: validation,
                file: null
            }
        },
        created() {
            this.validation.changeLanguage('en');
        },
        methods: {
            loadPacking() {
                if (this.$refs.fileImportForm.validate()) {
                    if(this.file.type === 'application/json') {
                        this.file.text().then(res => {
                            this.$store.commit("loadPacking", JSON.parse(res));
                            this.close();
                        });
                    } else if(this.file.type === 'text/plain') {
                        this.file.text().then(res => {
                            console.log(res);
                            this.close();
                        });
                    } else {
                        this.$toast("File not supported");
                    }
                }
            },
            openDialog() {
                this.resetValidation();
                this.file = null;
                this.dialog = true;
            },
            close() {
                this.resetValidation();
                this.dialog = false;
            },
            resetValidation() {
                this.$refs.fileImportForm.resetValidation();
            },
        }
    }
</script>

<style scoped>

</style>