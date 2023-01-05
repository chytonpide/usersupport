package com.nconnect.usersupport.infrastructure.resource;


import com.nconnect.usersupport.model.TenantOwned;
import io.vlingo.xoom.actors.Actor;
import io.vlingo.xoom.actors.Address;
import io.vlingo.xoom.actors.Definition;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.grid.Grid;

public class TenantOwnedModelResolver<T, D extends Actor & TenantOwned> {
    private final Grid stage;
    private final Class<T> resolvedType;
    private final Class<D> definitionType;

    public TenantOwnedModelResolver(Grid stage, Class<T> resolvedType, Class<D> definitionType) {
        this.stage = stage;
        this.resolvedType = resolvedType;
        this.definitionType = definitionType;
    }

    public Completes<T> resolve(final String tenantId, final String id) {
        return checkOwnership(tenantId, id).andThenTo(isOwnedByTenant ->
        {
            if (isOwnedByTenant) {
                return resolve(id);
            } else {
                return Completes.withFailure();
            }
        });
    }


    private Completes<T> resolve(final String id) {
        final Address address = stage.addressFactory().from(id);
        return stage.actorOf(resolvedType, address, Definition.has(definitionType, Definition.parameters(id)));
    }

    private Completes<Boolean> checkOwnership(final String tenantId, final String id) {
        final Address address = stage.addressFactory().from(id);
        Completes<TenantOwned> tenantOwnedCompletes = stage.actorOf(TenantOwned.class, address, Definition.has(definitionType, Definition.parameters(id)));

        return tenantOwnedCompletes.andThenTo(tenantOwned -> tenantOwned.isOwnedBy(tenantId));
    }

}
