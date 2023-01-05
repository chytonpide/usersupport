package com.nconnect.usersupport.infrastructure.persistence;

import com.nconnect.usersupport.infrastructure.CommentView;
import io.vlingo.xoom.actors.Actor;
import io.vlingo.xoom.actors.CompletesEventually;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.common.Outcome;
import io.vlingo.xoom.reactivestreams.sink.TerminalOperationConsumerSink;
import io.vlingo.xoom.symbio.Metadata;
import io.vlingo.xoom.symbio.StateBundle;
import io.vlingo.xoom.symbio.store.QueryExpression;
import io.vlingo.xoom.symbio.store.Result;
import io.vlingo.xoom.symbio.store.StorageException;
import io.vlingo.xoom.symbio.store.state.StateStore;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * See <a href="https://docs.vlingo.io/xoom-lattice/entity-cqrs#querying-a-statestore">Querying a StateStore</a>
 */
@SuppressWarnings("all")
public class CommentQueriesActor extends Actor implements CommentQueries, StateStore.ReadResultInterest {
    private final CommentQueries self;
    private final StateStore stateStore;
    private final StateStore.ReadResultInterest readInterest;

    public CommentQueriesActor(StateStore store) {
        //super();
        self = selfAs(CommentQueries.class);
        this.stateStore = store;
        this.readInterest = (StateStore.ReadResultInterest) this.selfAs(StateStore.ReadResultInterest.class);
    }

    @Override
    public Completes<CommentView> commentOf(String tenantId, String id) {
        CompletesEventually completes = this.completesEventually();

        CommentView notFoundState = CommentView.empty();
        Consumer<CommentView> collector = (commentView) -> {
            if (commentView.tenant != null && commentView.tenant.id.equals(tenantId)) {
                completes.with(commentView);
            } else {
                completes.with(notFoundState);
            }
        };

        QueryResultHandler resultHandler = new QueryResultHandler(collector, notFoundState);

        this.stateStore.read(
                id,
                CommentView.class,
                this.readInterest,
                resultHandler);

        return this.completes();
    }



    @Override
    public final <T> void readResultedIn(Outcome<StorageException, Result> outcome, String id, T state, int stateVersion, Metadata metadata, Object resultHandler) {
        outcome.andThen((result) -> {
            QueryResultHandler.from(resultHandler).completeFoundWith(state);
            return result;
        }).otherwise((cause) -> {
            if (cause.result.isNotFound()) {
                QueryResultHandler.from(resultHandler).completeNotFound();
            } else {
                this.logger().info("Query state not read for update because: " + cause.getMessage(), cause);
            }
            return cause.result;
        });
    }

}
