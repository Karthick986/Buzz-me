package com.android.bluetooothpro;

class GetSet {

    public GetSet() {}

    public String bname, bdatetime, bdist;

    public GetSet(String bname, String bdatetime, String bdist) {
        this.bname = bname;
        this.bdatetime = bdatetime;
        this.bdist = bdist;
    }

    public String getBname() {
        return bname;
    }

    public String getBdatetime() {
        return bdatetime;
    }

    public String getBdist() {
        return bdist;
    }
}
