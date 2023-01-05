package com.nconnect.usersupport.infrastructure.resource;

import com.nconnect.usersupport.model.Commenter;
import io.vavr.control.Either;
import io.vlingo.xoom.http.Header;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class AuthenticatedUser {
    private static final String AUTH_TYPE = "usersupport";

    public final String id;
    public final String name;

    public static Either<AuthenticationError, AuthenticatedUser> from(Header authHeader) {
        if(authHeader == null) {
            return Either.left(new AuthenticationError("authentication header is required"));
        }

        String authValue = authHeader.value;
        if(!authValue.startsWith(AUTH_TYPE)) {
            return Either.left(new AuthenticationError("not supported authentication type"));
        }
        authValue = authValue.substring((AUTH_TYPE + " ").length());

        if(!authValue.contains("," +" ")) {
            return Either.left(new AuthenticationError("invalid authentication value"));
        }

        String idPart = authValue.split("," +" ")[0];
        if(!idPart.startsWith("id=")) {
            return Either.left(new AuthenticationError("invalid authentication value"));
        }
        String id = idPart.substring("id=".length());

        String namePart = authValue.split("," +" ")[1];
        if(!namePart.startsWith("name=")) {
            return Either.left(new AuthenticationError("invalid authentication value"));
        }
        String name = namePart.substring("name=".length());

        return Either.right(new AuthenticatedUser(id, name));
    }

    private AuthenticatedUser(final String id, final String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(31, 17)
                .append(id)
                .append(name)
                .toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Commenter another = (Commenter) other;
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
