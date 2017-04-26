package com.knowbox.teacher.base.bean;

import java.io.Serializable;
import java.util.List;

public class CityModel implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4638803721103897427L;
	private String id;
    private String name;
    private String fullName;
    private CityModel parent;
    private List<CityModel> children;

    public CityModel() {
        id = "0";
        name = "";
        fullName = "";
    }

    public CityModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public CityModel getParent() {
        return parent;
    }

    public void setParent(CityModel parent) {
        this.parent = parent;
    }

    public List<CityModel> getChildren() {
        return children;
    }

    public void setChildren(List<CityModel> children) {
        this.children = children;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        CityModel other = (CityModel) obj;
        if (id != null && id.equals(other.id) && name != null
                && name.equals(other.name)) {
            return true;
        }
        return false;
    }

}
