package com.utils;




public class CabRequest {

    private String driver_ids;
    private String metadata;
    private String created_at;


    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getDriver_ids() {
        return driver_ids;
    }

    public void setDriver_ids(String driver_ids) {
        this.driver_ids = driver_ids;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
