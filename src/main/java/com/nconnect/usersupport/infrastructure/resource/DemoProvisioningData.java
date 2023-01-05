package com.nconnect.usersupport.infrastructure.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

@SuppressWarnings("all")
public class DemoProvisioningData {
    public final DemoTenantData tenant;
    public final List<DemoUserData> users;

    public static DemoProvisioningData from(final DemoTenantData tenant, final List<DemoUserData> users) {
        return new DemoProvisioningData(tenant, users);
    }

    private DemoProvisioningData(final DemoTenantData tenant, final List<DemoUserData> users) {
        this.tenant = tenant;
        this.users = users;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        DemoProvisioningData another = (DemoProvisioningData) other;
        return new EqualsBuilder()
                .append(this.tenant, another.tenant)
                .append(this.users, another.users)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
                .append("tenant", tenant)
                .append("users", users)
                .toString();
    }

}

class DemoTenantData {
    public final String id;
    public final String name;

    public static DemoTenantData from(final String id, final String name) {
        return new DemoTenantData(id, name);
    }

    private DemoTenantData(final String id, final String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        DemoTenantData another = (DemoTenantData) other;
        return new EqualsBuilder()
                .append(this.id, another.id)
                .append(this.name, another.name)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
                .append("id", id)
                .append("name", name)
                .toString();
    }
}


class DemoUserData {
    enum Role {
        SUPPORTER,
        CUSTOMER
    }

    public final String id;
    public final String name;
    public final Role role;

    public static DemoUserData from(final String id, final String name, final Role role) {
        return new DemoUserData(id, name, role);
    }

    private DemoUserData(final String id, final String name, final Role role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        DemoUserData another = (DemoUserData) other;
        return new EqualsBuilder()
                .append(this.id, another.id)
                .append(this.name, another.name)
                .append(this.role, another.role)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.DEFAULT_STYLE)
                .append("id", id)
                .append("name", name)
                .append("role", role)
                .toString();
    }
}


