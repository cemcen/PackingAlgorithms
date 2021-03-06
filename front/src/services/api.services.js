import Vue from 'vue'

export default {
    sendMesh(mesh){
        return Vue.http.post("/api/2d/mesh/create", mesh);
    },
    sendMeshMultiLayers(mesh) {
        return Vue.http.post("/api/2d/mesh/multilayer/create", mesh);
    }
}