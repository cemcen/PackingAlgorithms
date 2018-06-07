<template>
    <div>
        <div v-if="polygons != null && polygons.length > 0" class="chart">
            <pie-chart></pie-chart>
        </div>
        <table v-if="polygons != null && polygons.length > 0"  class="table">
            <thead>
            <tr>
                <th>Nombre</th>
                <th>Vértices</th>
                <th>Apariciones</th>
                <th>Diámetro</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="item in polygons">
                <td>{{ item.label }} </td>
                <td>{{ item.numberOfVertex }}</td>
                <td>{{ item.percentage }}</td>
                <td>{{ item.radius }}</td>
            </tr>
            </tbody>
        </table>
        <md-empty-state v-if="polygons != null && polygons.length === 0"
                        md-icon="/assets/images/square-circle-and-triangle.svg"
                        md-label="No hay polígonos"
                        md-description="Agrega un polígono con la opción a la derecha.">
        </md-empty-state>
    </div>
</template>

<script>
    import PieChart from './js/PieChart.js'

    export default {
        components: {
            PieChart
        },
        data(){
            return {
                polygons: []
            }
        },
        created(){
            this.updatePolygons();
        },
        watch: {
            polygons: {
                handler() {
                    localStorage.setItem('polygons', JSON.stringify(this.polygons));
                },
                deep: true
            },
        },
        methods: {
            updatePolygons(){
                if(localStorage.getItem('polygons')) {
                    this.polygons = JSON.parse(localStorage.getItem('polygons'));
                }
            }
        }
    }
</script>

<style scoped lang="scss">
    .chart {
        width: 70%;
        margin: 0 auto;
    }
    .table {
        table-layout: fixed;
        max-width: 300px;
        max-height: 300px;
        overflow: auto;
        display: block;
        font-size: 11px;
        padding-top: 10px;
        margin: 0 auto;
    }

    td {
        max-width: 10px;
        overflow: hidden;
    }

</style>