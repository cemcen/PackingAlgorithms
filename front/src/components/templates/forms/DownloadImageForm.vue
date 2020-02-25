<template>
    <v-card elevation="0">
        <v-card-title>
            <v-row justify="center">
                <v-btn dark color="teal lighten-2" :loading="isLoading" @click.native="downloadFile">
                    Download
                </v-btn>
            </v-row>
        </v-card-title>

        <v-card-text>
            <v-form ref="downloadFileForm">
                <v-row justify="center">
                    <v-col class="pa-0 pr-3 pl-3">
                        <v-text-field v-model="pixelRatio" type="number"
                                      :rules="[validation.required(), validation.requiredPositive()]"
                                      label="pixel ratio" clearable required>
                        </v-text-field>
                    </v-col>
                </v-row>
            </v-form>
        </v-card-text>
    </v-card>
</template>

<script>
    import validation from '../../../services/validation.service';

    export default {
        name: "DownloadImageForm",
        data() {
            return {
                validation: validation,
                isLoading: false,
                pixelRatio: 3,
            }
        },
        methods: {
            downloadFile() {
                this.$emit("download", this.pixelRatio)
            }
        }
    }
</script>

<style scoped>

</style>