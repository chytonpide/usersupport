package com.nconnect.usersupport.infrastructure.resource;

import com.nconnect.usersupport.infrastructure.CommentBodyData;
import com.nconnect.usersupport.infrastructure.CommentData;
import com.nconnect.usersupport.infrastructure.ErrorData;
import com.nconnect.usersupport.infrastructure.persistence.CommentQueries;
import com.nconnect.usersupport.infrastructure.persistence.QueryModelStateStoreProvider;
import com.nconnect.usersupport.model.Commenter;
import com.nconnect.usersupport.model.Tenant;
import com.nconnect.usersupport.model.DomainError;
import com.nconnect.usersupport.model.comment.Comment;
import com.nconnect.usersupport.model.comment.CommentEntity;
import com.nconnect.usersupport.model.comment.CommentState;
import com.nconnect.usersupport.model.supportcase.SupportCase;
import com.nconnect.usersupport.model.supportcase.SupportCaseEntity;
import io.vavr.control.Either;
import io.vlingo.xoom.actors.Address;
import io.vlingo.xoom.actors.Definition;
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

import static io.vlingo.xoom.common.Completes.withSuccess;
import static io.vlingo.xoom.common.serialization.JsonSerialization.serialized;
import static io.vlingo.xoom.http.Response.Status.*;
import static io.vlingo.xoom.http.ResponseHeader.Location;
import static io.vlingo.xoom.http.resource.ResourceBuilder.resource;

/**
 * See <a href="https://docs.vlingo.io/xoom-turbo/xoom-annotations#resourcehandlers">@ResourceHandlers</a>
 */
public class CommentResource extends DynamicResourceHandler {
    private final Grid $stage;
    private final Logger $logger;
    private final CommentQueries $queries;
    private final TenantOwnedModelResolver<Comment, CommentEntity> resolver;

    public CommentResource(final Grid grid) {
        super(grid.world().stage());
        this.$stage = grid;
        this.$logger = super.logger();
        this.$queries = ComponentRegistry.withType(QueryModelStateStoreProvider.class).commentQueries;
        this.resolver = new TenantOwnedModelResolver(grid, Comment.class, CommentEntity.class);
    }

    public Completes<Response> commentToSupportCase(String tenantId, Header authorizationHeader, CommentData data) {
        // TODO: Move the responsibility of authentication to a front-service
        final Either<AuthenticationError, AuthenticatedUser> authUserEither = AuthenticatedUser.from(authorizationHeader);
        if(authUserEither.isLeft()) {
            AuthenticationError error = authUserEither.getLeft();
            return Completes.withSuccess(entityResponseOf(Forbidden, CorsNegotiation.headers(), serialized(ErrorData.from(error.message()))));
        }
        AuthenticatedUser authUser = authUserEither.get();

        final Tenant tenant = Tenant.from(tenantId);
        final Commenter commenter = Commenter.from(authUser.id, authUser.name);
        return resolveSupportCase(data.supportCaseId)
                .andThenTo(supportCase -> supportCase.commentFor($stage, tenant, commenter, data.body))
                .andThenTo(outcome -> {
                    try {
                        CommentState state = outcome.get();
                        return Completes.withSuccess(
                                entityResponseOf(
                                        Created,
                                        ResponseHeader.headers(
                                                        ResponseHeader.of(Location, location(tenantId, state.id)))
                                                .and(CorsNegotiation.headers()), ""));
                    } catch (DomainError error) {
                        return Completes.withSuccess(entityResponseOf(BadRequest, CorsNegotiation.headers(), serialized(ErrorData.from(error.messages()))));
                    }
                })
                .otherwise(arg -> Response.of(NotFound))
                .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
    }

    public Completes<Response> editComment(String tenantId, String id, Header authorizationHeader, CommentBodyData data) {
        // TODO: Move the responsibility of authentication to a front-service
        final Either<AuthenticationError, AuthenticatedUser> authUserEither = AuthenticatedUser.from(authorizationHeader);
        if(authUserEither.isLeft()) {
            AuthenticationError error = authUserEither.getLeft();
            return Completes.withSuccess(entityResponseOf(Forbidden, CorsNegotiation.headers(), serialized(ErrorData.from(error.message()))));
        }
        AuthenticatedUser authUser = authUserEither.get();

        final Commenter commenter = Commenter.from(authUser.id, authUser.name);
        return resolver.resolve(tenantId, id)
                .andThenTo(comment -> comment.edit(commenter, data.body))
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

    public Completes<Response> markCommentAsRemoved(String tenantId, String id, Header authorizationHeader) {
        // TODO: Move the responsibility of authentication to a front-service

        final Either<AuthenticationError, AuthenticatedUser> authUserEither = AuthenticatedUser.from(authorizationHeader);
        if(authUserEither.isLeft()) {
            AuthenticationError error = authUserEither.getLeft();
            return Completes.withSuccess(entityResponseOf(Forbidden, CorsNegotiation.headers(), serialized(ErrorData.from(error.message()))));
        }
        AuthenticatedUser authUser = authUserEither.get();
        final Commenter commenter = Commenter.from(authUser.id, authUser.name);
        return resolver.resolve(tenantId, id)
                .andThenTo(comment -> comment.markAsRemoved(commenter))
                .andThenTo(outcome -> {
                    try {
                        outcome.get();
                        return withSuccess(Response.of(NoContent, CorsNegotiation.headers()));
                    } catch (DomainError error) {
                        return withSuccess(entityResponseOf(BadRequest, CorsNegotiation.headers(), serialized(ErrorData.from(error.messages()))));
                    }
                })
                .otherwise(arg -> Response.of(NotFound))
                .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));


    }

    public Completes<Response> commentOf(String tenantId, String id) {
        return $queries.commentOf(tenantId, id)
                .andThenTo(data -> data.id.isEmpty() ?
                        withSuccess(Response.of(NotFound)) : withSuccess(Response.of(Ok, CorsNegotiation.headers(), serialized(data))))
                .otherwise(arg -> Response.of(NotFound))
                .recoverFrom(e -> Response.of(InternalServerError, e.getMessage()));
    }


    @Override
    public Resource<?> routes() {
        return resource("CommentResourceHandler",
                io.vlingo.xoom.http.resource.ResourceBuilder.get("/tenants/{tenantId}/comments/{commentId}")
                        .param(String.class)
                        .param(String.class)
                        .handle(this::commentOf),
                io.vlingo.xoom.http.resource.ResourceBuilder.options("/tenants/{tenantId}/comments")
                        .handle(CorsNegotiation::response),
                io.vlingo.xoom.http.resource.ResourceBuilder.post("/tenants/{tenantId}/comments")
                        .param(String.class)
                        .header("Authorization")
                        .body(CommentData.class)
                        .handle(this::commentToSupportCase),
                io.vlingo.xoom.http.resource.ResourceBuilder.options("/tenants/{tenantId}/comments/{commentId}/body")
                        .handle(CorsNegotiation::response),
                io.vlingo.xoom.http.resource.ResourceBuilder.patch("/tenants/{tenantId}/comments/{commentId}/body")
                        .param(String.class)
                        .param(String.class)
                        .header("Authorization")
                        .body(CommentBodyData.class)
                        .handle(this::editComment),
                io.vlingo.xoom.http.resource.ResourceBuilder.options("/tenants/{tenantId}/comments/{commentId}")
                        .handle(CorsNegotiation::response),
                io.vlingo.xoom.http.resource.ResourceBuilder.delete("/tenants/{tenantId}/comments/{commentId}")
                        .param(String.class)
                        .param(String.class)
                        .header("Authorization")
                        .handle(this::markCommentAsRemoved)

        );
    }

    @Override
    protected ContentType contentType() {
        return ContentType.of("application/json", "charset=UTF-8");
    }

    private String location(final String tenantId, final String id) {
        return "/tenants/" + tenantId +"/comments/" + id;
    }

    private Completes<Comment> resolve(final String id) {
        final Address address = $stage.addressFactory().from(id);
        return $stage.actorOf(Comment.class, address, Definition.has(CommentEntity.class, Definition.parameters(id)));
    }

    private Completes<SupportCase> resolveSupportCase(final String supportCaseId) {
        final Address address = $stage.addressFactory().from(supportCaseId);
        return $stage.actorOf(SupportCase.class, address, Definition.has(SupportCaseEntity.class, Definition.parameters(supportCaseId)));
    }

}
