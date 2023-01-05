package com.nconnect.usersupport.infrastructure.resource;

import com.nconnect.usersupport.infrastructure.ErrorData;
import com.nconnect.usersupport.infrastructure.SupportCaseContentData;
import com.nconnect.usersupport.model.*;
import com.nconnect.usersupport.model.supportcase.SupportCaseState;
import com.nconnect.usersupport.infrastructure.SupportCaseData;
import com.nconnect.usersupport.infrastructure.persistence.QueryModelStateStoreProvider;
import com.nconnect.usersupport.infrastructure.persistence.SupportCaseQueries;
import com.nconnect.usersupport.model.supportcase.SupportCase;
import com.nconnect.usersupport.model.supportcase.SupportCaseEntity;
import io.vavr.control.Either;
import io.vlingo.xoom.actors.Logger;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.http.ContentType;
import io.vlingo.xoom.http.Header;
import io.vlingo.xoom.http.Response;
import io.vlingo.xoom.http.ResponseHeader;
import io.vlingo.xoom.http.resource.DynamicResourceHandler;
import io.vlingo.xoom.http.resource.Resource;
import io.vlingo.xoom.lattice.grid.Grid;
import io.vlingo.xoom.turbo.ComponentRegistry;

import static io.vlingo.xoom.common.Completes.*;
import static io.vlingo.xoom.common.serialization.JsonSerialization.serialized;
import static io.vlingo.xoom.http.Response.Status.Ok;
import static io.vlingo.xoom.http.Response.Status.*;
import static io.vlingo.xoom.http.ResponseHeader.Location;
import static io.vlingo.xoom.http.ResponseHeader.headers;

import static io.vlingo.xoom.http.resource.ResourceBuilder.resource;

/**
 * See <a href="https://docs.vlingo.io/xoom-turbo/xoom-annotations#resourcehandlers">@ResourceHandlers</a>
 */
// TODO: Move the responsibility of authentication to a front-service
public class SupportCaseResource extends DynamicResourceHandler {
    private final Grid $stage;
    private final Logger $logger;
    private final SupportCaseQueries $queries;
    private final TenantOwnedModelResolver<SupportCase, SupportCaseEntity> resolver;

    public SupportCaseResource(final Grid grid) {
        super(grid.world().stage());
        this.$stage = grid;
        this.$logger = super.logger();
        this.$queries = ComponentRegistry.withType(QueryModelStateStoreProvider.class).supportCaseQueries;
        this.resolver = new TenantOwnedModelResolver(grid, SupportCase.class, SupportCaseEntity.class);
    }

    public Completes<Response> openSupportCase(String tenantId, Header authorizationHeader, SupportCaseData data) {
        final Either<AuthenticationError, AuthenticatedUser> authUserEither = AuthenticatedUser.from(authorizationHeader);
        if(authUserEither.isLeft()) {
            AuthenticationError error = authUserEither.getLeft();
            return Completes.withSuccess(entityResponseOf(Forbidden, CorsNegotiation.headers(), serialized(ErrorData.from(error.message()))));
        }
        AuthenticatedUser authUser = authUserEither.get();

        final Tenant tenant = Tenant.from(tenantId);
        //TODO: Get role information from another service ex) RoleService.supporter(authUser.id)
        final Customer customer = Customer.from(authUser.id, authUser.name);
        return SupportCase.open($stage, tenant, customer, data.categoryId, data.subject, data.description)
                .andThenTo(outcome -> {
                    try {
                        SupportCaseState state = outcome.get();
                        return Completes.withSuccess(
                                entityResponseOf(
                                        Created,
                                        ResponseHeader.headers(ResponseHeader.of(Location, location(tenantId, state.id))).and(CorsNegotiation.headers())
                                        , ""));
                    } catch (DomainError error) {
                        return Completes.withSuccess(entityResponseOf(BadRequest, CorsNegotiation.headers(), serialized(ErrorData.from(error.messages()))));
                    }
                })
                .otherwise(arg -> Response.of(NotFound))
                .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
    }

    public Completes<Response> closeSupportCase(String tenantId, String id, Header authorizationHeader) {
        final Either<AuthenticationError, AuthenticatedUser> authUserEither = AuthenticatedUser.from(authorizationHeader);
        if(authUserEither.isLeft()) {
            AuthenticationError error = authUserEither.getLeft();
            return Completes.withSuccess(entityResponseOf(Forbidden, CorsNegotiation.headers(), serialized(ErrorData.from(error.message()))));
        }
        AuthenticatedUser authUser = authUserEither.get();

        User closingUser = User.from(authUser.id, authUser.name);
        return resolver.resolve(tenantId, id)
                .andThenTo(supportCase -> supportCase.close(closingUser))
                .andThenTo(outcome -> {
                    try {
                        outcome.get();
                        return Completes.withSuccess(Response.of(NoContent, CorsNegotiation.headers()));
                    } catch (DomainError error) {
                        return Completes.withSuccess(entityResponseOf(BadRequest, CorsNegotiation.headers(), serialized(ErrorData.from(error.messages()))));
                    }
                })
                .otherwise(arg -> Response.of(NotFound))
                .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
    }

    public Completes<Response> assignSupportCaseToMe(String tenantId, String id, Header authorizationHeader) {
        final Either<AuthenticationError, AuthenticatedUser> authUserEither = AuthenticatedUser.from(authorizationHeader);
        if(authUserEither.isLeft()) {
            AuthenticationError error = authUserEither.getLeft();
            return Completes.withSuccess(entityResponseOf(Forbidden, CorsNegotiation.headers(), serialized(ErrorData.from(error.message()))));
        }
        AuthenticatedUser authUser = authUserEither.get();

        //TODO: Get role information from another service ex) RoleService.supporter(authUser.id)
        final Supporter supporter = Supporter.from(authUser.id, authUser.name);
        return resolver.resolve(tenantId, id)
                .andThenTo(supportCase -> supportCase.assign(supporter))
                .andThenTo(outcome -> {
                    try {
                        outcome.get();
                        return Completes.withSuccess(Response.of(NoContent, CorsNegotiation.headers()));
                    } catch (DomainError error) {
                        return Completes.withSuccess(entityResponseOf(BadRequest, CorsNegotiation.headers(), serialized(ErrorData.from(error.messages()))));
                    }
                })
                .otherwise(arg -> Response.of(NotFound))
                .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
    }

    public Completes<Response> editSupportCase(String tenantId, String id, Header authorizationHeader, SupportCaseContentData data) {
        final Either<AuthenticationError, AuthenticatedUser> authUserEither = AuthenticatedUser.from(authorizationHeader);
        if(authUserEither.isLeft()) {
            AuthenticationError error = authUserEither.getLeft();
            return Completes.withSuccess(entityResponseOf(Forbidden, CorsNegotiation.headers(), serialized(ErrorData.from(error.message()))));
        }
        AuthenticatedUser authUser = authUserEither.get();

        //TODO: Get role information from another service ex) RoleService.supporter(authUser.id)
        final Customer customer = Customer.from(authUser.id, authUser.name);

        return resolver.resolve(tenantId, id)
                .andThenTo(supportCase -> supportCase.edit(customer, data.categoryId, data.subject, data.description))
                .andThenTo(outcome -> {
                    try {
                        outcome.get();
                        return Completes.withSuccess(Response.of(NoContent, CorsNegotiation.headers()));
                    } catch (DomainError error) {
                        return Completes.withSuccess(entityResponseOf(BadRequest, CorsNegotiation.headers(), serialized(ErrorData.from(error.messages()))));
                    }
                })
                .otherwise(arg -> Response.of(NotFound))
                .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
    }

    public Completes<Response> supportCases(String tenantId, Integer offset, Integer limit) {
        if (offset == null)
            offset = 0;

        if (limit == null)
            limit = 20;

        if (limit > 100) {
            String errorMessage = "The limit must be less than or equal to 100.";
            return Completes.withSuccess(entityResponseOf(BadRequest, CorsNegotiation.headers(), serialized(ErrorData.from(errorMessage))));
        }

        System.out.println(offset);
        System.out.println(limit);
        System.out.println(tenantId);

        return $queries.supportCases(tenantId, offset, limit)
                .andThenTo(data -> withSuccess(
                        Response.of(Ok, CorsNegotiation.headers(), serialized(data))))
                .otherwise(arg -> Response.of(NotFound))
                .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));


    }

    public Completes<Response> supportCaseOf(String tenantId, String id) {
        return $queries.supportCaseOf(tenantId, id)
                .andThenTo(data -> data.id.isEmpty() ?
                        withSuccess(Response.of(NotFound)) : withSuccess(Response.of(Ok, CorsNegotiation.headers(), serialized(data))))
                .otherwise(arg -> Response.of(NotFound))
                .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
    }

    @Override
    public Resource<?> routes() {
        return resource("SupportCaseResourceHandler",
                io.vlingo.xoom.http.resource.ResourceBuilder.options("/tenants/{tenantId}/support-cases")
                        .handle(CorsNegotiation::response),
                io.vlingo.xoom.http.resource.ResourceBuilder.post("/tenants/{tenantId}/support-cases")
                        .param(String.class)
                        .header("Authorization")
                        .body(SupportCaseData.class)
                        .handle(this::openSupportCase),
                io.vlingo.xoom.http.resource.ResourceBuilder.options("/tenants/{tenantId}/support-cases/{supportCaseId}/closed")
                        .handle(CorsNegotiation::response),
                io.vlingo.xoom.http.resource.ResourceBuilder.post("/tenants/{tenantId}/support-cases/{supportCaseId}/closed")
                        .param(String.class)
                        .param(String.class)
                        .header("Authorization")
                        .handle(this::closeSupportCase),
                io.vlingo.xoom.http.resource.ResourceBuilder.options("/tenants/{tenantId}/support-cases/{supportCaseId}/assigned-to-me")
                        .handle(CorsNegotiation::response),
                io.vlingo.xoom.http.resource.ResourceBuilder.patch("/tenants/{tenantId}/support-cases/{supportCaseId}/assigned-to-me")
                        .param(String.class)
                        .param(String.class)
                        .header("Authorization")
                        .handle(this::assignSupportCaseToMe),
                io.vlingo.xoom.http.resource.ResourceBuilder.options("/tenants/{tenantId}/support-cases/{supportCaseId}/content")
                        .handle(CorsNegotiation::response),
                io.vlingo.xoom.http.resource.ResourceBuilder.patch("/tenants/{tenantId}/support-cases/{supportCaseId}/content")
                        .param(String.class)
                        .param(String.class)
                        .header("Authorization")
                        .body(SupportCaseContentData.class)
                        .handle(this::editSupportCase),
                io.vlingo.xoom.http.resource.ResourceBuilder.get("/tenants/{tenantId}/support-cases")
                        .param(String.class)
                        .query("offset", Integer.class)
                        .query("limit", Integer.class)
                        .handle(this::supportCases),
                io.vlingo.xoom.http.resource.ResourceBuilder.get("/tenants/{tenantId}/support-cases/{id}")
                        .param(String.class)
                        .param(String.class)
                        .handle(this::supportCaseOf)
        );
    }

    @Override
    protected ContentType contentType() {
        return ContentType.of("application/json", "charset=UTF-8");
    }

    private String location(final String tenantId, final String id) {
        return "/tenants/" + tenantId + "/support-cases/" + id;
    }

}
