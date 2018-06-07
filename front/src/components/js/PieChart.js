import {Pie} from 'vue-chartjs'

export default {
    extends: Pie,
    data() {
        let myLabels = [];
        let myData = [];

        if(localStorage.getItem('polygons')) {
            let polygons = JSON.parse(localStorage.getItem('polygons'));
            polygons.forEach(function(polygon){
                myData.push(polygon.percentage);
                myLabels.push(polygon.label);
            })
        }
        return {
            datacollection:{
                labels: myLabels,
                datasets: [
                    {
                        backgroundColor: [
                            'rgba(65, 184, 131, .8)',
                            'rgba(228, 102, 81, .8)',
                            'rgba(0, 216, 255, .8)',
                            'rgba(155, 89, 182, .8)'
                        ],
                        borderWidth: 0,
                        data: myData
                    }
                ]
            },
            options: {
                legend: {
                    display: true
                },
                responsive: true,
                maintainAspectRatio: false
            }
        }
    },
    mounted () {
        this.renderChart(this.datacollection, this.options)
    }
}

