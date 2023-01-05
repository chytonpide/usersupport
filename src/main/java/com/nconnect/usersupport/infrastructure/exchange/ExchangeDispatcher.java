package com.nconnect.usersupport.infrastructure.exchange;

import com.nconnect.usersupport.model.comment.CommentEdited;
import com.nconnect.usersupport.model.supportcase.SupportCaseOpened;
import io.vlingo.xoom.common.serialization.JsonSerialization;
import io.vlingo.xoom.lattice.exchange.Exchange;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.State;
import io.vlingo.xoom.symbio.store.Result;
import io.vlingo.xoom.symbio.store.dispatch.ConfirmDispatchedResultInterest;
import io.vlingo.xoom.symbio.store.dispatch.Dispatchable;
import io.vlingo.xoom.symbio.store.dispatch.Dispatcher;
import io.vlingo.xoom.symbio.store.dispatch.DispatcherControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

import com.nconnect.usersupport.model.supportcase.SupportCaseAssigned;
import com.nconnect.usersupport.model.comment.CommentedToSupportCase;
import com.nconnect.usersupport.model.supportcase.SupportCaseClosed;

/**
 * See
 * <a href="https://docs.vlingo.io/xoom-lattice/projections#dispatcher-and-projectiondispatcher">
 *   Dispatcher and ProjectionDispatcher
 * </a>
 */
public class ExchangeDispatcher implements Dispatcher<Dispatchable<Entry<String>, State<String>>>, ConfirmDispatchedResultInterest {
  private static final Logger logger = LoggerFactory.getLogger(ExchangeDispatcher.class);

  private DispatcherControl control;
  private final Exchange producer;
  private final List<String> outgoingEvents = new ArrayList<>();

  public ExchangeDispatcher(final Exchange producer) {
    this.producer = producer;
    this.outgoingEvents.add(SupportCaseClosed.class.getCanonicalName());
    this.outgoingEvents.add(CommentEdited.class.getCanonicalName());
    this.outgoingEvents.add(SupportCaseAssigned.class.getCanonicalName());
    this.outgoingEvents.add(SupportCaseOpened.class.getCanonicalName());
    this.outgoingEvents.add(CommentedToSupportCase.class.getCanonicalName());
  }

  @Override
  public void dispatch(final Dispatchable<Entry<String>, State<String>> dispatchable) {
    logger.debug("Going to dispatch id {}", dispatchable.id());

    for (Entry<String> entry : dispatchable.entries()) {
      this.send(JsonSerialization.deserialized(entry.entryData(), entry.typed()));
    }

    this.control.confirmDispatched(dispatchable.id(), this);
  }

  @Override
  public void confirmDispatchedResultedIn(final Result result, final String dispatchId) {
    logger.debug("Dispatch id {} resulted in {}", dispatchId, result);
  }

  @Override
  public void controlWith(final DispatcherControl control) {
    this.control = control;
  }

  private void send(final Object event) {
    if(shouldPublish(event)) {
      this.producer.send(event);
    }
  }

  private boolean shouldPublish(final Object event) {
    return outgoingEvents.contains(event.getClass().getCanonicalName());
  }

}
